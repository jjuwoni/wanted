package com.ohgiraffers.jwtrestapi.exception;

public class TokenException extends RuntimeException{
	public TokenException(String message) {
		super(message);
	}
}
