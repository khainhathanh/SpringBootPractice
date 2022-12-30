package com.spring.demo2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Configuration
public class DBConfig extends AbstractMongoClientConfiguration{
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
}
