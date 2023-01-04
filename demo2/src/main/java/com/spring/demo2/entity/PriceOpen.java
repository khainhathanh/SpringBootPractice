package com.spring.demo2.entity;

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
@Document(collection = "PriceOpen")
public class PriceOpen {
	private String tourID;
	private List<String> dateOpen;
	private String currency;
	private Integer price;
}
