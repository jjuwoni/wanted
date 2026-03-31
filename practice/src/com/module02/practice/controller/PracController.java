package com.module02.practice.controller;

import java.util.Scanner;

public class PracController {

    public void practice1() {
        Scanner sc = new Scanner(System.in);
        System.out.print("이름을 입력하세요 : ");
        String name = sc.nextLine();
        System.out.print("나이를 입력하세요 : ");
        int age = sc.nextInt();
        System.out.print("성별을 입력하세요(남/여) : ");
        char gen = sc.next().charAt(0);
        System.out.print("키를 입력하세요 : ");
        double height = sc.nextDouble();

        System.out.println("키" + height + "인 " + age + "살 " + gen + "자 " + name + "님 반갑습니다^^");
    }

    public void practice2() {
        Scanner sc = new Scanner(System.in);
        System.out.print("문자열을 입력하세요 : ");
        String str = sc.nextLine();

        System.out.println("첫 번째 문자 : " + str.charAt(0));
        System.out.println("두 번째 문자 : " + str.charAt(1));
        System.out.println("세 번째 문자 : " + str.charAt(2));
    }

    public void practice3() {
        Scanner sc = new Scanner(System.in);
        System.out.print("이름 : ");
        String name = sc.nextLine();
        System.out.print("학년(숫자만) : ");
        int grade = sc.nextInt();
        System.out.print("반(숫자만) : ");
        int room = sc.nextInt();
        System.out.print("번호(숫자만) : ");
        int num = sc.nextInt();
        System.out.print("성별(M/F) : ");
        char gen = sc.next().charAt(0);
        System.out.print("성적(소수점 아래 둘째 자리까지) : ");
        double score = sc.nextDouble();

        String student;
        if(gen == 'm' || gen == 'M') {
            student = "남학생";
        } else {
            student = "여학생";
        }
        System.out.println(grade + "학년 " + room + "반 " + num + "번 " + name + " " + student + "의 성적은 " + score + "이다.");
    }

    public void practice4() {
        Scanner sc = new Scanner(System.in);
        System.out.print("주민번호를 입력하세요(-포함) : ");

        char chr = sc.next().charAt(7);

        if(chr == '2') {
            System.out.println("여자");
        } else if(chr == '1') {
            System.out.println("남자");
        } else {
            System.out.println(chr);
        }
    }

    public void practice5() {
        Scanner sc = new Scanner(System.in);
        System.out.print("정수1 : ");
        int num1 = sc.nextInt();
        System.out.print("정수2 : ");
        int num2 = sc.nextInt();
        System.out.print("비교값 : ");
        int comp = sc.nextInt();

        if(comp <= num1 || comp > num2) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }

    public void practice6() {
        Scanner sc = new Scanner(System.in);
        System.out.print("정수1 : ");
        int num1 = sc.nextInt();
        System.out.print("정수2 : ");
        int num2 = sc.nextInt();
        System.out.print("정수3 : ");
        int num3 = sc.nextInt();

        if(num1 == num2 && num2 == num3) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }

    public void practice7() {
        Scanner sc = new Scanner(System.in);
        System.out.print("점수 : ");
        int score = sc.nextInt();
        if(score >=90 && score <=100) {
            System.out.println("축하합니다");
        } else {
            System.out.println("분발하세요");
        }
    }
}
