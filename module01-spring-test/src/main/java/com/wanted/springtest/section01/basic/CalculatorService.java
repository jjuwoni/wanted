package com.wanted.springtest.section01.basic;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    /**
     * 두 수를 더한다.
     *
     * @param a 첫 번째 숫자
     * @param b 두 번째 숫자
     * @return 덧셈 결과
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * 두 수를 뺀다.
     *
     * @param a 피감수 (minuend)
     * @param b 감수 (subtrahend)
     * @return 뺄셈 결과
     */
    public int subtract(int a, int b) {
        return a - b;
    }

    /**
     * 두 수를 곱한다.
     *
     * @param a 첫 번째 숫자
     * @param b 두 번째 숫자
     * @return 곱셈 결과
     */
    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * 두 수를 나눈다.
     *
     * 주의: 0으로 나누는 경우 ArithmeticException이 발생한다.
     * 이는 테스트에서 예외 처리 학습에 활용된다.
     *
     * @param a 피제수 (dividend)
     * @param b 제수 (divisor)
     * @return 나눗셈 결과
     * @throws ArithmeticException b가 0인 경우
     */
    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("0으로 나눌 수 없다");
        }
        return a / b;
    }

    /**
     * 주어진 숫자가 짝수인지 확인한다.
     *
     * 이 메서드는 조건 분기 테스트 학습에 활용된다.
     *
     * @param number 확인할 숫자
     * @return 짝수이면 true, 홀수이면 false
     */
    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    /**
     * 주어진 숫자가 양수인지 확인한다.
     *
     * 경계값 테스트 학습에 활용된다.
     * - 양수: number > 0
     * - 0: 양수가 아님
     * - 음수: 양수가 아님
     *
     * @param number 확인할 숫자
     * @return 양수이면 true, 0이거나 음수이면 false
     */
    public boolean isPositive(int number) {
        return number > 0;
    }

    /**
     * 배열의 합계를 구한다.
     *
     * 컬렉션 데이터를 활용한 테스트 학습에 활용된다.
     * null 또는 빈 배열인 경우 0을 반환한다.
     *
     * @param numbers 숫자 배열
     * @return 배열의 모든 원소의 합
     */
    public int sum(int[] numbers) {

        /* 설명. null 또는 빈 배열에 대한 방어적 처리이다. */
        if (numbers == null || numbers.length == 0) {
            return 0;
        }

        int total = 0;
        for (int number : numbers) {
            total += number;
        }

        return total;
    }

    /**
     * 팩토리얼을 계산한다.
     *
     * 재귀 함수 테스트 및 성능 테스트 학습에 활용된다.
     * 음수 입력에 대해서는 IllegalArgumentException을 발생시킨다.
     *
     * @param n 팩토리얼을 구할 숫자 (0 이상)
     * @return n의 팩토리얼 값
     * @throws IllegalArgumentException n이 음수인 경우
     */
    public long factorial(int n) {

        /* 설명. 음수에 대한 팩토리얼은 수학적으로 정의되지 않으므로 예외를 발생시킨다. */
        if (n < 0) {
            throw new IllegalArgumentException("팩토리얼은 음수에 대해 정의되지 않는다");
        }

        /* 설명. 0과 1의 팩토리얼은 모두 1이다. (수학적 정의) */
        if (n == 0 || n == 1) {
            return 1;
        }

        /* 설명. 2부터 n까지 반복하여 곱셈을 수행한다. */
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }

        return result;
    }


}
