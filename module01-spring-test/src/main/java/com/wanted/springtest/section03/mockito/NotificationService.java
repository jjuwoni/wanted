package com.wanted.springtest.section03.mockito;

public interface NotificationService {

    /**
     * 이메일을 발송한다.
     *
     * @param email 이메일 주소
     * @param message 메시지 내용
     */
    void sendEmail(String email, String message);

    /**
     * SMS를 발송한다.
     *
     * @param phoneNumber 전화번호
     * @param message 메시지 내용
     * @return 발송 성공 여부
     */
    boolean sendSms(String phoneNumber, String message);

}
