package demo;

public class Solution3 {

    public static void main(String[] args) {
        String data = "[1, 2, 3, null, null, 4, 5]";
        String[] nodes = data.substring(1, data.length()-1).split(", ");
        for(String str : nodes){
            System.out.println("a" + str);
        }
        System.out.println(!"null".equals("null"));
    }
}
