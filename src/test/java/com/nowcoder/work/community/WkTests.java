package com.nowcoder.work.community;

import java.io.IOException;

public class WkTests {

    public static void main(String[] args) {
        String cmd = "E:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltoimage --quality 75 https://www.nowcoder.com E:/javaweb/projects/workspace/wkhtmltox/to-image/4.png";

        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
