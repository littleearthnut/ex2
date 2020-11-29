public class Back4TSP {

	int NoEdge = -1;
	int bigInt = Integer.MAX_VALUE;
	int[][] a; // 邻接矩阵
	int cc = 0; // 存储当前代价
	int bestc = bigInt;// 当前最优代价
	int[] x; // 当前解
	int[] bestx;// 当前最优解
	int n = 0; // 顶点个数
	
	private void backtrack(int t) {//i为初始深度
		if (t > n) {
			//TODO
			bestc = cc;
			bestx = x;
		} else {
			//TODO
			for (int j = t; j <= n; j++){
				if (check(x, j, t, a, n)){
					swap(x[t], x[j]);
					if (t < n && cc + a[x[t - 1]][x[t]] < bestc){
						cc = cc + a[x[t - 1]][x[t]];
						backtrack(t + 1);
						cc = cc - a[x[t - 1]][x[t]];
					}
					if (t == n && cc + a[x[t - 1]][x[t]] + a[x[n]][x[1]] < bestc){
						cc = cc + a[x[t - 1]][x[t]] + a[x[n]][x[1]];
						backtrack(t + 1);
						cc = cc - a[x[t - 1]][x[t]] - a[x[n]][x[1]];
					}
					swap(x[t], x[j]);
				}
			}
		}
	}
	
	private void swap(int i, int j) {
		int temp = x[i];
		x[i] = x[j];
		x[j] = temp;
	}
	
	public boolean check(int[] x, int j, int t, int[][] a, int n) {
		//TODO
		if (t < 2) return true;
		if (t < n && a[x[t - 1]][x[j]] != NoEdge) return true;
		if (t == n && a[x[t - 1]][x[j]] != NoEdge && a[x[j]][x[1]] != NoEdge) return true;
		return false;
	}
	
	public void backtrack4TSP(int[][] b, int num) {
		n = num;
		x = new int[n + 1];
		for (int i = 0; i <= n; i++)
			x[i] = i;
		bestx = new int[n + 1];
		a = b;
		backtrack(2);
	}

	public static void main(String[] args) {
		Back4TSP back4TSP = new Back4TSP();
		int[][] b = { { -1, -1, -1, -1, -1 }, { -1, -1, 9, 19, 13 }, { -1, 21, -1, -1, 14 }, { -1, 1, 40, -1, 17 },
				{ -1, 41, 80, 10, -1 } };
		int n = 4;
		back4TSP.backtrack4TSP(b, n);
		System.out.println(back4TSP.bestc);
	}
}
