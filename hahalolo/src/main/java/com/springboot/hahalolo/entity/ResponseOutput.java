package com.springboot.hahalolo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ResponseOutput<T> {
	private Integer code;
	private T data;
	private String message = "";
	private String meta = "";
	private boolean success;
}
