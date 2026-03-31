package com.module03.practice.controller;

import java.util.Scanner;

public class ArrayPractice {

    public void practice1() {
        int[] arr = new int[10];

        for(int i = 0; i < arr.length; i++) {
            arr[i] = i+1;
        }

        for(int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    public void practice2() {
        int[] arr = new int[10];
        int len = arr.length;

        for(int i = 0; i < arr.length ; i++) {
            arr[i] = len;
            len--;
        }
        for(int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    public void practice3() {
        Scanner sc = new Scanner(System.in);
        System.out.print("양의 정수 : ");
        int[] arr = new int[sc.nextInt()];

        for(int i = 0; i < arr.length; i++) {
            arr[i] = i+1;
        }

        for(int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    public void practice4() {
        String[] arr = {"사과", "귤", "포도", "복숭아", "참외"};
        /*
        * String arr[] = {"사과", "귤", "포도", "복숭아", "참외"};
        *
        * String[] arr = new String[]{"사과", "귤", "포도", "복숭아", "참외"};
        *
        * String[] arr;
        * arr = new String[]{"사과", "귤", "포도", "복숭아", "참외"};
        *
        * String[] arr = new String[5];
        * arr[0] = "사과";
        * arr[1] = "귤";
        * arr[2] = "포도";
        * arr[3] = "복숭아";
        * arr[4] = "참외";
        *
        * */
        System.out.println(arr[1]);
    }

    public void practice5() {
        Scanner sc = new Scanner(System.in);
        System.out.print("문자열 : ");
        String str = sc.nextLine();

        System.out.print("문자 : ");
        char ch = sc.nextLine().charAt(0);

        char[] arr = new char[str.length()];

        for(int i = 0; i < str.length(); i++) {
            arr[i] = str.charAt(i);
        }

        int count = 0;

        for(int i = 0; i < arr.length; i++) {
            if(arr[i] == ch) {
                count++;
            }
        }
        System.out.println(ch + "개수 : " +count);
    }
}
