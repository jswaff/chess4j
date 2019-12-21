package com.jamesswafford.chess4j.hash;


public class DecimalToBinaryString {

    public static String longToBinary(long num) {
        String s = Long.toBinaryString(num);
        int padLength = 64-s.length();
        StringBuilder pad = new StringBuilder();
        for (int i=0;i<padLength;i++) {
            pad.append("0");
        }
        return pad + s;
    }

    public static String integerToBinary(int num) {
        String s = Integer.toBinaryString(num);
        int padLength=32-s.length();
        StringBuilder pad = new StringBuilder();
        for (int i=0;i<padLength;i++) {
            pad.append("0");
        }
        return pad + s;
    }

    public static void main(String[] args) {
        int number = 0xFFFFF;
        System.out.print("Integer " + number + " = ");
        System.out.println(integerToBinary(number));
    }
} 
