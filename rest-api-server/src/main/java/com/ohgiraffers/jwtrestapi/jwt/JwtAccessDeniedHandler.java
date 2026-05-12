package com.ohgiraffers.jwtrestapi.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/* 설명.
 *  Spring Security Filter Chain에서 접근 거부 발생 시 실행되는 핸들러.
 *  AccessDeniedHandler 인터페이스를 구현해 사용자가 보호된 리소스에 접근할 권한이 없을 때
 *  HttpServletResponse를 사용하여 적절한 HTTP 응답을 보낸다.
 * */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
					   AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		/* 설명. 유효한 자격증명을 제공하지 않고 접근 시 401 상태코드 발생 */
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

}
