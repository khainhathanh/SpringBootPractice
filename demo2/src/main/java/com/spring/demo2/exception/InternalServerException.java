package com.spring.demo2.exception;

public class InternalServerException extends RuntimeException {
	public InternalServerException(String message) {
		super(message);
	}
}
