
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * @author 庞新程
 * @version 1.0
 */
public class BB4TSP {

	int NoEdge = -1; //表示没有边
	private int minCost = Integer.MAX_VALUE; //当前最小代价
	public int getMinCost() {
		return minCost;
	}

	public void setMinCost(int minCost) {
		this.minCost = minCost;
	}

	Comparator<HeapNode> cmp = new Comparator<HeapNode>() {
		public int compare(HeapNode e1, HeapNode e2) {//从大到小排序
			return e1.lcost - e2.lcost;
		}
	};

	private PriorityQueue<HeapNode> priorHeap = new PriorityQueue<HeapNode>(100);//存储活节点
	private Vector<Integer> bestH = new Vector<Integer>();


	@SuppressWarnings("rawtypes")
	public static class HeapNode implements Comparable{
		Vector<Integer> liveNode;//城市排列
		int lcost; //代价的下界
		int level;//0-level的城市是已经排好的
		//构造方法
		public HeapNode(Vector<Integer> node,int lb, int lev){
			liveNode = new Vector<>();
			liveNode.addAll(0, node);
			lcost = lb;
			level = lev;
		}

		@Override
		public int compareTo(Object x) {//升序排列, 每一次pollFirst
			int xu=((HeapNode)x).lcost;
			if(lcost<xu) return -1;
			if(lcost==xu) return 0;
			return 1;
		}

		public boolean equals(Object x){
			return lcost==((HeapNode)x).lcost;
		}

	}

	/**
	 * 计算部分解的下界.
	 *
	 * @param liveNode
	 * 		              城市的排列
	 *
	 * @param level
	 * 			   当前确定的城市的个数.
	 * @param cMatrix
	 *            邻接矩阵，第0行，0列不算
	 *
	 * @exception IllegalArgumentException
	 */
	public int computeLB(Vector<Integer> liveNode, int level, int[][] cMatrix)
	{
		int res = 0;
		int len = cMatrix.length;
		boolean[] vis = new boolean[len];

		//首先先把所有已经排好序的点走过的点的距离计算出来
		for (int i = 1; i < level; i++) {
			res += cMatrix[liveNode.get(i)][liveNode.get(i + 1)] * 2;
			vis[liveNode.get(i)] = true;
			vis[liveNode.get(i + 1)] = true;
		}

		//如果已经走完全图了,就差回到起点
		if (level == len - 1) {

			//没法回去, 将权值置为无穷大, 在优先队列里将自动被放置到最后
			if (cMatrix[liveNode.get(level)][liveNode.get(1)] == -1) {
				return Integer.MAX_VALUE;
			} else {
				//可以回去,那么此时的下界即为当前解
				res += cMatrix[liveNode.get(level)][liveNode.get(1)] * 2;
			}
		} else {
			//没走完全图, 那么加上到起点的最小值
			res += cMatrix[0][liveNode.get(1)];

			//加上当前路径里最后一个的出边的最小值
			res += cMatrix[liveNode.get(level)][0];
			vis[liveNode.get(1)] = true;
			vis[liveNode.get(level)] = true;

			//对其他的点,采用启发式的方法, 直接加上每个点的入边和出边的最小值
			for (int i = 1; i <= len - 1; i++) {
				if (!vis[i]) {
					res += cMatrix[0][i];
					res += cMatrix[i][0];
				}
			}
		}

		//算出来的结果需要除以2
		return res / 2;
	}

	/**
	 * 计算TSP问题的最小代价的路径.
	 *
	 * @param cMatrix
	 *            邻接矩阵，第0行，0列不算
	 * @param n   城市个数.
	 * @return 返回TSP问题的最优解, 如果不存在, 则返回Integer.MAX_VALUE(0x7fffffff)
	 * @exception IllegalArgumentException
	 */
	public int bb4TSP(int[][] cMatrix, int n)
	{
		//对邻接矩阵进行预处理,将邻接矩阵的第0行和第0列利用起来, cMatrix[i][0]表示从i节点出发的最短路径, cMatrix[0][j]表示到j节点的最短路
		for (int i = 1; i <= n; i++) {
			cMatrix[i][0] = Integer.MAX_VALUE;
			cMatrix[0][i] = Integer.MAX_VALUE;
		}
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= n; j++) {
				if (cMatrix[i][j] != -1) {
					cMatrix[i][0] = Math.min(cMatrix[i][0], cMatrix[i][j]);
					cMatrix[0][j] = Math.min(cMatrix[0][j], cMatrix[i][j]);
				}
			}
		}

		for (int i = 1; i <= n; i++) {
			if (cMatrix[i][0] == Integer.MAX_VALUE || cMatrix[0][i] == Integer.MAX_VALUE) {
				return Integer.MAX_VALUE;
			}
		}

		//构造初始节点
		Vector<Integer> liveNode = new Vector<Integer>() ;//城市排列
		liveNode.add(-1);
		for(int i = 1; i<=n; i++) liveNode.add(i);
		int level = 1;//0-level的城市是已经排好的
		int lcost = computeLB(liveNode, level, cMatrix) ; //代价的下界
		HeapNode cNode = new HeapNode(liveNode, lcost, level);
		while(level != n)
		{
			Vector<Integer> cLiveNodes = cNode.liveNode;
			int cLevel = cNode.level;
			int cLCost = cNode.lcost;
			int curV = cLiveNodes.get(cLevel);
			int t = cLiveNodes.get(cLevel + 1);
			for (int i = cLevel + 1; i <= n; i++) {
				int nextV = cLiveNodes.get(i);
				if (cMatrix[curV][nextV] != -1) {
					cLiveNodes.set(cLevel + 1, nextV);
					cLiveNodes.set(i, t);
					int nextCost = computeLB(cLiveNodes, cLevel + 1, cMatrix);
					priorHeap.add(new HeapNode(cLiveNodes, nextCost, cLevel + 1));//添加新节点到优先队列
					cLiveNodes.set(i, nextV);
					cLiveNodes.set(cLevel + 1, t);
				}
			}

			//当队列为空时, 也没有搜索到一个解, 说明无解, 设置为MAX_VALUE之后返回
			if (priorHeap.isEmpty()){
				setMinCost(Integer.MAX_VALUE);
				return minCost;
			}
			cNode = priorHeap.poll();//从队首取一个元素
			level = cNode.level;//更新当前的level, 一旦level变成n, 说明已经搜索到了最优解
		}
		setMinCost(cNode.lcost);//设置最优
		return minCost;
	}

	public static void main(String[] args) {
		BB4TSP bb4TSP = new BB4TSP();
		int[][] b = { { -1, -1, -1, -1, -1 }, { -1, -1, 9, 19, 13 }, { -1, 21, -1, -1, 14 }, { -1, 1, 40, -1, 17 },
				{ -1, 41, 80, 10, -1 } };
//        int[][] b = {
//                {-1, -1, -1, -1, -1, -1},
//                {-1, -1,  3,  1,  5,  8},
//                {-1,  3, -1,  6,  7,  9},
//                {-1,  1,  6, -1,  4,  2},
//                {-1,  5,  7,  4, -1,  3},
//                {-1,  8,  9,  2,  3, -1}};
		int n = 4;
		System.out.print(bb4TSP.bb4TSP(b, n));

	}
}
