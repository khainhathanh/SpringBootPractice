package com.springboot.hahalolo.config;

import java.sql.Timestamp;

import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


@Configuration
public class ConfigDB extends AbstractMongoClientConfiguration {
	@Value("${spring.data.mongodb.host}")
	private String host;

	@Value("${spring.data.mongodb.port}")
	private String port;


	@Value("${spring.data.mongodb.database}")
	private String db;

	@Bean
	public  MongoClient mongoClient() {
		return MongoClients.create();
	}

	@Bean
	public  MongoDatabase mongoDatabase() {
		return mongoClient().getDatabase(db);
	}

	@Override
	protected String getDatabaseName() {
		return db;
	}
	
	@Bean
	public JsonWriterSettings convert() {
		return JsonWriterSettings.builder()
		         .objectIdConverter((value, writer) -> writer.writeString(value.toString()))
		         .dateTimeConverter((value, writer) -> writer.writeString(new Timestamp(value).toString()))
		         .build();
	}
	
}

