package com.ohgiraffers.stability.section03.validation.annotation;

import com.ohgiraffers.stability.section03.validation.validator.RatingValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 평점 유효성을 검증하는 커스텀 어노테이션
 * 
 * <p>평점이 1-5 범위 내에 있고, 0.5 단위로만 입력되는지 검증한다.
 * 예: 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0만 허용</p>
 */
@Documented
@Constraint(validatedBy = RatingValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRating {

    String message() default "평점은 1.0부터 5.0까지 0.5 단위로만 입력 가능합니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
} 