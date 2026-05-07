package com.wanted.springtest.section03.mockito;

public interface PaymentService {

    /**
     * 결제를 처리한다.
     *
     * @param userId 사용자 ID
     * @param amount 결제 금액
     * @return 결제 성공 여부
     */
    boolean processPayment(Long userId, double amount);

    /**
     * 결제 수수료를 계산한다.
     *
     * @param amount 결제 금액
     * @return 수수료
     */
    double calculateFee(double amount);

    /**
     * 결제 내역을 조회한다.
     *
     * @param userId 사용자 ID
     * @return 결제 내역 수
     */
    int getPaymentCount(Long userId);

}
