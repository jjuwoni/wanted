package com.wanted.restapi.section03.valid;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    /* 자주 사용되는 어노테이션
     * @Null       // null만 허용
     * @NotNull    // null을 불허 (단, "", " "는 허용)
     * @NotEmpty    // null, ""을 불허 (단, " "는 허용)
     * @NotBlank    // null, "", " " 모두 불허
     *
     * @Email            // 이메일 형식인지 검사 (단, ""은 허용)
     * @Pattern(regexp = )  // 정규식을 사용한 패턴 검사
     *
     * @Size(min=, max=)    // 문자열 또는 컬렉션의 길이를 제한
     * @Max(value = )      // 숫자의 최대값을 제한
     * @Min(value = )      // 숫자의 최소값을 제한
     *
     * @Positive       // 값을 양수로 제한.
     * @PositiveOrZero  // 값을 양수와 0만 가능하도록 제한.
     * @Negative       // 값을 음수로 제한.
     * @NegativeOrZero  // 값을 음수와 0만 가능하도록 제한.
     *
     * @Future     // 현재보다 미래의 날짜인지 검증
     * @Past       // 현재보다 과거의 날짜인지 검증
     *
     * @AssertFalse    // false만 허용 (단, null은 체크 안함)
     * @AssertTrue     // true만 허용 (단, null은 체크 안함)
     * */
    private int no;

    @NotNull(message = "아이디는 반드시 입력되어야 합니다.")
    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min=8, max=20, message="아이디는 8자 이상, 20자 이하로 입력해야 합니다.")
    @Pattern(regexp="^[a-zA-Z0-9_]+$", message="아이디는 영문자, 숫자, 밑줄(_)만 사용 가능합니다.")
    private String id;

    @Size(min=8, max=30, message="패스워드는 8자 이상, 30자 이하로 입력해야 합니다.")
    @Pattern(regexp="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$",
            message="패스워드는 대문자, 소문자, 숫자, 특수문자(@$!%*?&)가 각각 1자 이상 포함되어야 합니다.")
    private String pwd;

    @NotNull(message="이름은 반드시 입력되어야 합니다.")
    @Size(min=2, max=5, message="이름은 최소 2글자, 또는 최대 5글자를 입력해야 합니다.")
    private String name;

    @Past
    private Date enrollDate;
}
