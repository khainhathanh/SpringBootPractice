package com.example.demo.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAPI {

	@ExceptionHandler(InternalServerException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse internalServerException(InternalServerException ex) {
		 return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()); 
	}
	
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorResponse badRequestException(BadRequestException ex) {
		 return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()); 
	}
	
	@ExceptionHandler(InterruptedsException.class)
	@ResponseStatus(value = HttpStatus.LOOP_DETECTED)
	public ErrorResponse interruptedException(BadRequestException ex) {
		 return new ErrorResponse(HttpStatus.LOOP_DETECTED, ex.getMessage()); 
	}
	
	@ExceptionHandler(ExecutionsException.class)
	@ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
	public ErrorResponse executionException(BadRequestException ex) {
		 return new ErrorResponse(HttpStatus.GATEWAY_TIMEOUT, ex.getMessage()); 
	}
}
