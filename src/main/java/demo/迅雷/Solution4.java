package demo.迅雷;

public class Solution4 {

    // 求连续子数组的最大乘积
    public static int getMaxProduct(int[] arr){
        // 维护两个值，一个当前临时子数组的乘积最大值，一个最小值
        int tmpMax = -1 << 30, tmpMin = 1 >> 30;
        int maxProduct = -1 << 30;
        // 遍历数组
        for(int i = 0; i < arr.length; i++){
            // 碰到负数，最大值和最小值交换
            if(arr[i] < 0){
                int temp = tmpMax;
                tmpMax = tmpMin;
                tmpMin = temp;
            }

            tmpMax = Math.max(tmpMax * arr[i], arr[i]);
            tmpMin = Math.min(tmpMin * arr[i], arr[i]);

            // 更新最大连续子数组的的最大乘积
            maxProduct = Math.max(maxProduct, tmpMax);
        }

        return maxProduct;
    }

    public static void main(String[] args) {
        int[] arr = {-1, 2, 0, 4, 5, 6};
        System.out.println(getMaxProduct(arr));
    }
}
