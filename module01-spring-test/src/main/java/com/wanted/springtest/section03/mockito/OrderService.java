package com.wanted.springtest.section03.mockito;

import org.springframework.stereotype.Service;

public class OrderService {

    /* 설명. 결제 처리를 담당하는 서비스이다. */
    private final PaymentService paymentService;

    /* 설명. 알림 발송을 담당하는 서비스이다. */
    private final NotificationService notificationService;

    /* 설명. 생성자 주입 방식으로 의존성을 주입받는다. */
    public OrderService(PaymentService paymentService, NotificationService notificationService) {
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    /**
     * 주문을 처리한다.
     * 결제 처리 후 성공하면 알림을 발송한다.
     *
     * @param userId 사용자 ID
     * @param email 이메일 주소
     * @param amount 주문 금액
     * @return 주문 처리 결과
     */
    public OrderResult processOrder(Long userId, String email, double amount) {
        /* 설명. 입력값 검증 - 방어적 프로그래밍이다. */
        if (userId == null || email == null || amount <= 0) {
            throw new IllegalArgumentException("잘못된 주문 정보입니다.");
        }

        /* 설명. 결제 서비스를 호출하여 결제를 처리한다. */
        boolean paymentSuccess = paymentService.processPayment(userId, amount);

        if (paymentSuccess) {
            /* 설명. 결제 성공 시 이메일 알림을 발송한다. */
            String message = String.format("주문이 완료되었습니다. 결제 금액: %.2f원", amount);
            notificationService.sendEmail(email, message);

            return new OrderResult(true, "주문이 성공적으로 처리되었습니다.", amount);
        } else {
            return new OrderResult(false, "결제 처리에 실패했습니다.", amount);
        }
    }

    /**
     * 주문 금액에 따른 총 비용을 계산한다 (수수료 포함).
     *
     * @param amount 주문 금액
     * @return 총 비용 (주문 금액 + 수수료)
     */
    public double calculateTotalCost(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("주문 금액은 0보다 커야 합니다.");
        }

        double fee = paymentService.calculateFee(amount);
        return amount + fee;
    }

    /**
     * 사용자의 주문 이력을 확인한다.
     *
     * @param userId 사용자 ID
     * @return 이력 확인 결과
     */
    public String checkOrderHistory(Long userId) {
        if (userId == null) {
            return "유효하지 않은 사용자 ID입니다.";
        }

        int paymentCount = paymentService.getPaymentCount(userId);

        if (paymentCount == 0) {
            return "주문 이력이 없습니다.";
        } else if (paymentCount < 5) {
            return String.format("일반 고객입니다. (주문 횟수: %d회)", paymentCount);
        } else {
            return String.format("VIP 고객입니다. (주문 횟수: %d회)", paymentCount);
        }
    }

    /**
     * 주문 처리 결과를 나타내는 내부 클래스
     */
    public static class OrderResult {
        private final boolean success;
        private final String message;
        private final double amount;

        public OrderResult(boolean success, String message, double amount) {
            this.success = success;
            this.message = message;
            this.amount = amount;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public double getAmount() {
            return amount;
        }
    }
}
