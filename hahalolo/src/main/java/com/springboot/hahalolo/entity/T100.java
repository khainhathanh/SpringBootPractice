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
@Document(collection = "T100")
public class T100 {
	private String id;
	private String lang;
	private String currency;
	private String dl146;
	private String dl147;
	private String dl148;
	private String dl149;
	private List<T101> ft101;
	private T101 t101;
	private String pt550;
	private T102 t102;
	private T550 t550;
	private Integer tn120;
	private Integer tn123;
	private Integer tn127;
	private Integer tn130;
	private Integer tn131;
	private Integer tn133;
	private Integer tn134;
	private Integer tn135;
}
