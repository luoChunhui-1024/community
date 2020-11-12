package demo.腾讯云;


/**
 * 描述：找出数组中只出现一次的元素
 * 样例：
 * 输入一个数组：
 * A = [1，2，2，2，1，5，6，6，8，7，7]  n
 * 输出数组：
 * B = [5，8]
 */
public class Solution1 {

    public static int[] getOnceElmement(int[] arr){

        // 遍历数组，找到最大值，最为计数数组的长度
        int maxNum = 0;
        for(int i = 0; i < arr.length; i++){
            maxNum = Math.max(maxNum, arr[i]);
        }

        // 借助一个额外的数组，数组元素初始值为0
        int[] tempArr = new int[maxNum + 1];

        // 遍历数组
        for(int i = 0; i < arr.length; i++){
            // 把每个元素作为下标，标记该下标对应的元素，加一
            tempArr[arr[i]]++;
        }
        // 遍历临时数组，如果值为1， 则只出现一次，
        // 先统计值为1的元素的个数
        int count = 0;
        for(int i = 0; i <= maxNum; i++){
            if(tempArr[i] == 1){
                count++;
            }
        }
        int[] res = new int[count];
        int index = 0;
        for(int i = 0; i <= maxNum; i++){
            if(tempArr[i] == 1){
                res[index++] = i;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        int[] arr = {1,2,2,2,1,20,6,3,8,4,7};
        int[] res = getOnceElmement(arr);
        for(int num : res){
            System.out.print(num + " ");
        }
    }
}
