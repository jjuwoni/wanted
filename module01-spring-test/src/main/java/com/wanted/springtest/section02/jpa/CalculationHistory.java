package com.wanted.springtest.section02.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_history")
public class CalculationHistory {

    /* 설명. 기본 키(Primary Key)이다. IDENTITY 전략을 사용하여 데이터베이스가 자동으로 값을 생성한다. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 설명. 연산 종류를 저장한다. (예: ADD, SUBTRACT, MULTIPLY, DIVIDE) */
    @Column(nullable = false)
    private String operation;

    /* 설명. 첫 번째 피연산자를 저장한다. */
    @Column(nullable = false)
    private Double operand1;

    /* 설명. 두 번째 피연산자를 저장한다. */
    @Column(nullable = false)
    private Double operand2;

    /* 설명. 연산 결과를 저장한다. */
    @Column(nullable = false)
    private Double result;

    /* 설명. 계산 기록이 생성된 시간을 저장한다. */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /* 설명. JPA는 엔티티를 생성할 때 기본 생성자를 필요로 한다. */
    public CalculationHistory() {
        this.createdAt = LocalDateTime.now();
    }

    /* 설명.
     *  테스트에서 편리하게 사용할 수 있는 생성자이다.
     *  필요한 필드들을 한 번에 초기화할 수 있다.
     * */
    public CalculationHistory(String operation, Double operand1, Double operand2, Double result) {
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }

    /* 설명. Getter와 Setter 메서드들이다. */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Double getOperand1() {
        return operand1;
    }

    public void setOperand1(Double operand1) {
        this.operand1 = operand1;
    }

    public Double getOperand2() {
        return operand2;
    }

    public void setOperand2(Double operand2) {
        this.operand2 = operand2;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CalculationHistory{" +
                "id=" + id +
                ", operation='" + operation + '\'' +
                ", operand1=" + operand1 +
                ", operand2=" + operand2 +
                ", result=" + result +
                ", createdAt=" + createdAt +
                '}';
    }
}
