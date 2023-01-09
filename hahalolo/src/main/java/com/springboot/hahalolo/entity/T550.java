package com.springboot.hahalolo.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "T550")
public class T550 {
	private String id;
	private List<T551> ft551;
	private T551 t551;
}
