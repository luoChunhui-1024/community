package demo.跟谁学;

import java.util.Scanner;

public class Solution5 {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[][] matrix = new int[n][n];
        // 读取数据
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++) {
                matrix[i][j] = in.nextInt();
            }
        }
        // 先上下翻转矩阵
        for(int i = 0; i < n / 2; i++){
            for(int j = 0; j < n; j++){
                int temp = matrix[i][j];
                matrix[i][j] = matrix[n-i-1][j];
                matrix[n-i-1][j] = temp;
            }
        }

        // 然后左右翻转矩阵
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n / 2; j++){
                int temp = matrix[i][j];
                matrix[i][j] = matrix[i][n - j - 1];
                matrix[i][n - j - 1] = temp;
            }
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(j > 0){
                    System.out.print(" ");
                }
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }

    }
}
