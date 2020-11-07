package demo.微盟;

public class Solution2 {


    public static String bigAdd(String num1, String num2){

        StringBuilder n1 = new StringBuilder(num1);
        StringBuilder n2 = new StringBuilder(num2);

        // 让长的一个字符串作为中间结果,也就是n1
        if(n2.length() > n1.length()){
            StringBuilder temp = n1;
            n1 = n2;
            n2 = n1;
        }

        // 先把num1反转后存入一个StringBuilder中作为中间结果
        //StringBuilder res = new StringBuilder(n1.reverse());
        n1 = n1.reverse();
        // 反转num2
        n2 = n2.reverse();
        /// System.out.println();
        // 循环累加，计数，进位
        int len1 = n1.length(), len2 = n2.length();
        int fn = 0; // 存储进位
        int temp = 0;   // 两个个位数的和
        int i = 0;
        for(; i < len1 && i < len2; i++){
            temp = (n1.charAt(i) - '0') + (n2.charAt(i) - '0');
            n1.setCharAt(i, (char)((temp + fn) % 10 + '0'));
            fn = (temp + fn) / 10;
        }

        // 如果长度不一样
        // 如果n1更长，让n1继续循环累加fn,直至fn为0
        if(i < len1){
            while(fn != 0){
                temp = (n1.charAt(i) - '0');
                if(i >= len1){
                    n1.append((char)((temp + fn) % 10 + '0'));
                }else{
                    n1.setCharAt(i, (char)((temp + fn) % 10 + '0'));
                }
                fn = (temp + fn) / 10;
                i++;
            }
        }
        // 反转StringBuilder后作为结果
        return n1.reverse().toString();
    }

    public static void main(String[] args) {
        String num1 = "1", num2 = "9";
        System.out.println(bigAdd(num1, num2));;
    }
}
