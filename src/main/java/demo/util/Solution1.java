package demo.util;

public class Solution1 {

    public static void main(String[] args) {
        String text = "";
        String[] lines = text.split("<p[.]+>");
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
