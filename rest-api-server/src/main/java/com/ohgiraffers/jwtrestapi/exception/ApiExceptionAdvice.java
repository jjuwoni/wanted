package com.ohgiraffers.jwtrestapi.exception;

import com.ohgiraffers.jwtrestapi.exception.dto.ApiExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/* 설명. 예외 처리 전용 어드바이스 추가
 *  @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *  요청을 처리하는데 있어서 모든 Controller단에서 발생하는 각 예외 상황을 한 번에 처리하기 위한 어노테이션.
 *  JSON 형태의 오류 응답을 매우 효율적으로 반환할 수 있다.
 *  아래 메소드 레벨에 선언된 @ExceptionHandler와 일종의 세트로 사용되고 있다는 점이 포인트다.
 * */
@RestControllerAdvice
public class ApiExceptionAdvice {

	/* 설명: @ExceptionHandler
	 *  Spring MVC에서 제공되는 기능으로, 해당 메소드가 지정된 예외를 처리할 것임을 나타내는 어노테이션.
	 *  (메소드 수준 또는 클래스 수준에서 사용할 수 있다)
	 *  하지만 @RestControllerAdvice와 함께 사용하면,
	 *  해당 어노테이션이 선언된 메소드는 어플리케이션의 모든 컨트롤러에서 발생하는 지정된 예외를 처리한다.
	 *  일반적으로, 사용자 정의 예외를 잡아내어 여러 곳에서 동일한 예외 행동을 처리하는 데 유용하다.
	 * */

	/* 설명. AuthService에서 비밀번호 불일치 시 발생하는 예외 상황 처리 */
	@ExceptionHandler(LoginFailedException.class)
	public ResponseEntity<ApiExceptionDTO> exceptionHandler(LoginFailedException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiExceptionDTO(HttpStatus.BAD_REQUEST, e.getMessage()));
	}
	
	/* 설명.
	 *  TokenProvider에서 토큰 유효성 검사용 메소드 정의 시 사용되며,
	 *  유효성 검사 메소드는 JwtFilter에서 토큰 유효성 검사 시 발생하는 예외 상황 처리
	 */
	@ExceptionHandler(TokenException.class)
	public ResponseEntity<ApiExceptionDTO> exceptionHandler(TokenException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ApiExceptionDTO(HttpStatus.UNAUTHORIZED, e.getMessage()));
				
	}
	
	/* 설명. 이메일이 중복됐을 때 발생하는 예외 상황 처리 */
	@ExceptionHandler(DuplicatedMemberEmailException.class)
	public ResponseEntity<ApiExceptionDTO> exceptionHandler(DuplicatedMemberEmailException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiExceptionDTO(HttpStatus.BAD_REQUEST, e.getMessage()));
	}
}




