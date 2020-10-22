package com.nowcoder.work.community.entity;

import java.util.*;

public class Solution1 {

    public static void main(String[] args) {
        // 读取文件
        Scanner in = new Scanner(System.in);
        Map<Integer, Integer> map = new HashMap<>();
        // 把每个数据都存入一个map中计数排序
        int a, b;
        int n = 0;
        while(in.hasNextInt()){
            a = in.nextInt();
            b = in.nextInt();
            if(map.containsKey(a)){
                map.put(a, map.get(a) + 1);
            }else{
                map.put(a, 1);
            }

            if(map.containsKey(b)){
                map.put(b, map.get(b) + 1);
            }else{
                map.put(b, 1);
            }
            n++;
        }

        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        // 对数组进行堆排序
        PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for(Map.Entry<Integer, Integer> en : set){
            minHeap.offer(en);
            if(minHeap.size() > n){
                minHeap.poll();
            }
        }
        // 输出堆中的100个数
        for(int i = 0; i < minHeap.size(); i++){
            Map.Entry<Integer, Integer> en = minHeap.poll();
            System.out.println(en.getKey());
        }

//        PriorityQueue<Integer> minHeap = new PriorityQueue<>(new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o1-o2;
//            }
//        });
//        minHeap.offer(1);
//        minHeap.offer(2);
//        minHeap.offer(3);
//        for(int i = 0; i < 3; i++){
//            System.out.println(minHeap.poll());
//        }
    }
}
