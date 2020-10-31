package demo;

public class Solution {

    // 单词反转
    public static String reverseWords(String lines){

        // 分割
        String[] words = lines.split(" ");
        StringBuilder sb = new StringBuilder("");

        // 倒序拼接
        for(int i = words.length - 1; i >= 0; i--){
            if(i != words.length - 1){
                sb.append(" ");
            }
            sb.append(words[i]);
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String line = "i am a student";
        System.out.println(reverseWords(line));
    }

    // http接口

    // 查询个人信息

    // 一秒钟不能超过10次


}
