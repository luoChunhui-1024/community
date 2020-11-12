package demo.腾讯云;

/**
 * 题目五（数组区间求和）
 * 描述：给定一个数组，同时多次给定不同的区间下标i和j（下标从0开始），快速求这些区间和
 * 样例：
 * 输入A = [1, 3, 8, 4, 6]
 * 输入下标
 * i = 1
 * j = 3
 * 输出：15（3+8+4）
 * */
public class Solution2 {

    public static int getIntevalSum(int nums[], int m, int n){

        // 前缀和
        int len = nums.length;
        if(n >= len || m < 0){
            return -1;
        }
        int[] prefixSum = new int[len];
        prefixSum[0] = nums[0];
        for(int i = 1; i < len; i++){
            prefixSum[i] = prefixSum[i-1] + nums[i];
        }

        return prefixSum[n] - prefixSum[m - 1];
    }

    public static void main(String[] args) {
        int[] arr = {1, 3, 8, 4, 6, 3, 89, 23, 21, 3};
        System.out.println(getIntevalSum(arr, 4, 6));
    }
}
