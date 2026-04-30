package com.ohgiraffers.stability.section03.validation.validator;

import com.ohgiraffers.stability.section03.validation.annotation.ValidRating;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 평점 유효성을 검증하는 Validator 클래스
 * 
 * <p>ValidRating 어노테이션과 함께 사용되어 평점이 올바른 범위와 단위인지 검증한다.
 * 1.0부터 5.0까지 0.5 단위로만 허용한다.</p>
 * 
 * <p>사용 시나리오:</p>
 * <ul>
 *   <li>@NotNull과 함께 사용: null 값은 @NotNull에서 차단되므로 여기서는 범위/단위 검증만 수행</li>
 *   <li>단독 사용: null 값을 허용하고 null이 아닌 경우에만 범위/단위 검증 수행</li>
 * </ul>
 */
public class RatingValidator implements ConstraintValidator<ValidRating, Double> {

    @Override
    public void initialize(ValidRating constraintAnnotation) {
        // 초기화 로직이 필요한 경우 여기에 구현
    }

    @Override
    public boolean isValid(Double rating, ConstraintValidatorContext context) {

        /* rating 필드에 @NotNull이 함께 사용되므로 null은 들어올 수 없음
         * 하지만 안전성을 위해 null 체크는 유지 (다른 곳에서 사용될 가능성 고려) */
        if (rating == null) {
            return true;
        }

        /* 범위 검증: 1.0 ~ 5.0 */
        if (rating < 1.0 || rating > 5.0) {
            return false;
        }

        /* 0.5 단위 검증
         * 예: 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0 */
        double remainder = (rating * 10) % 5;

        // 부동소수점 오차 고려
        return Math.abs(remainder) < 0.001;
    }
} 