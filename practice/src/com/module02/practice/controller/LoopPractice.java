package com.module02.practice.controller;

import java.util.Scanner;

public class LoopPractice {

    public void practice1() {
        Scanner sc = new Scanner(System.in);
        System.out.print("1이상의 숫자를 입력하세요 : ");
        int num = sc.nextInt();
        if (num < 1) {
            System.out.println("잘못 입력하셨습니다.");
        }
        for (int i = 1; i <= num; i++) {
            System.out.print(" " + i);
        }
    }

    public void practice2() {
        Scanner sc = new Scanner(System.in);
        System.out.print("1이상의 숫자를 입력하세요 : ");
        int num = sc.nextInt();
        while (true) {
            if (num < 1) {
                System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
                System.out.print("1이상의 숫자를 입력하세요 : ");
            } else {
                break;
            }
            int num2 = sc.nextInt();
            num = num2;
        }
        for (int i = 1; i <= num; i++) {
            System.out.print(" " + i);
        }
    }

    public void practice3() {
        Scanner sc = new Scanner(System.in);
        System.out.print("1이상의 숫자를 입력하세요 : ");
        int num = sc.nextInt();
        if (num < 0) {
            System.out.println("잘못 입력하셨습니다.");
        } else {
            for (int i = 0; i < num; num--) {
                System.out.print(" " + num);
            }
        }
    }

    public void practice4() {
        Scanner sc = new Scanner(System.in);
        System.out.print("1이상의 숫자를 입력하세요 : ");
        int num = sc.nextInt();
        while (true) {
            if (num < 1) {
                System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
                System.out.print("1이상의 숫자를 입력하세요 : ");
            } else {
                break;
            }
            int num2 = sc.nextInt();
            num = num2;
        }
        for (int i = num; i > 0; i--) {
            System.out.print(" " + i);
        }
    }

    public void practice5() {
        Scanner sc = new Scanner(System.in);
        System.out.print("정수를 하나 입력하세요 : ");
        int num = sc.nextInt();
        int sum = 0;
        for (int i = 1; i < num; i++) {
            System.out.print(i + " + ");
            sum = sum + i;
            if (i + 1 == num) {
                sum = sum + num;
                System.out.println((i + 1) + " = " + sum);
            }
        }


    }

    public void practice6() {
        Scanner sc = new Scanner(System.in);
        System.out.print("첫 번째 숫자 : ");
        int n1 = sc.nextInt();
        System.out.print("두 번째 숫자 : ");
        int n2 = sc.nextInt();

        if (n1 == 0 || n2 == 0) {
            System.out.println("1이상의 숫자만을 입력해주세요.");
            System.exit(0);
        }
        if (n1 < n2) {
            for (int a = n1; a <= n2; a++) {
                System.out.print(" " + a);
            }
        } else if (n1 > n2) {
            for (int a = n2; a <= n1; a++) {
                System.out.print(" " + a);
            }
        }
    }

    public void practice7() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("첫 번째 숫자 : ");
            int num1 = sc.nextInt();
            System.out.print("두 번째 숫자 : ");
            int num2 = sc.nextInt();

            if (num1 <= 0 || num2 <= 0) {
                System.out.println("1이상의 숫자만을 입력해주세요.");
                continue;
            }

            if (num1 > num2) {
                for (int a = num2; a <= num1; a++) {
                    System.out.print(" " + a);
                }
                break;
            } else if (num1 < num2) {
                for (int a = num1; a <= num2; a++) {
                    System.out.print(" " + a);
                }
                break;
            }
            break;
        }
    }

    public void practice8() {
        Scanner sc = new Scanner(System.in);
        System.out.print("숫자 : ");
        int no = sc.nextInt();
        for (int i = 1; i <= 9; i++) {
            System.out.println(no + "*" + i + " = " + no * i);
        }
    }

    public void practice9() {
        Scanner sc = new Scanner(System.in);
        System.out.print("숫자 : ");
        int no = sc.nextInt();
        if (no < 2 || no > 9) {
            System.out.println("2~9 사이의 숫자만 입력해주세요.");
        }
        for (int j = no; j <= 9; j++) {
            System.out.println("=========" + j + "단========");
            for (int i = 1; i <= 9; i++) {
                System.out.println(j + "*" + i + " = " + j * i);
            }
        }
    }

    public void practice10() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("숫자 : ");
            int no = sc.nextInt();
            if (no < 2 || no > 9) {
                System.out.println("2~9 사이의 숫자만 입력해주세요.");
                continue;
            }
            for (int j = no; j <= 9; j++) {
                System.out.println("=========" + j + "단========");
                for (int i = 1; i <= 9; i++) {
                    System.out.println(j + "*" + i + " = " + j * i);
                }
            }
            break;
        }
    }

    public void practice11() {
        Scanner sc = new Scanner(System.in);
        System.out.print("시작 숫자 : ");
        int st = sc.nextInt();
        System.out.print("공차 : ");
        int tol = sc.nextInt();
        for (int i = 1; i <= 10; i++) {
            System.out.print(st + " ");
            st = st + tol;
        }

    }

    public void practice12() {
        Scanner sc = new Scanner(System.in);

        System.out.print("연산자(+,-,*,/,%) : ");
        String cal = sc.next();
        if (cal.equals("exit")) {
            System.out.println("프로그램을 종료합니다.");
            System.exit(0);
        }
        System.out.print("정수1 : ");
        int num1 = sc.nextInt();
        System.out.print("정수2 : ");
        int num2 = sc.nextInt();

        switch (cal) {
            case "+" :
                System.out.println(num1 + " + " + num2 + " = " + (num1 + num2));
                break;
            case "-" :
                System.out.println(num1 + " - " + num2 + " = " + (num1 - num2));
                break;
            case "*" :
                System.out.println(num1 + " * " + num2 + " = " + (num1 * num2));
                break;
            case "/" :
                if(num2 == 0) {
                    System.out.println("0으로 나눌 수 없습니다. 다시 입력해주세요.");
                    break;
                }
                System.out.println(num1 + " / " + num2 + " = " + (num1 / num2));
                break;
            case "%" :
                System.out.println(num1 + " % " + num2 + " = " + (num1 % num2));
                break;
            default :
                System.out.println("없는 연산자입니다. 다시 입력해주세요.");
                break;
        }
    }

    public void practice13() {
        Scanner sc = new Scanner(System.in);
        System.out.print("문자열 : ");
        String str = sc.nextLine();
        System.out.print("찾고자하는 문자 : ");
        char cr = sc.next().charAt(0);

        int count = 0;

        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == cr) {
                count++;
            }
        }
        System.out.print("포함된 갯수 : " + count);

    }

    public void practice14() {
        int money = 0;
        int i;
        for(i=1; money<10000; i++) {
            money += 70;
            System.out.println(i + "회 모금액 : " + money + "원");
        }

        System.out.println("총 모금횟수 : " + (i-1) + "회");

//        int money=0;
//        int i=0;
//        while(money<10000){
//            i += 1;
//            money += 70;
//            System.out.println(i + "회 모금액 : " + money + "원");
//        }
//        System.out.println("총 모금횟수 : " + i + "회");
    }
}


