package com.mrcl.store1;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class Store1Application {



	public static void main(String[] args) {
		SpringApplication.run(Store1Application.class, args);

		System.out.println("MySql:");
		System.out.println("DB MySql username: " + System.getenv("DB_USERNAME_MYSQL"));
		System.out.println("DB MySql password: " + System.getenv("DB_PASSWORD_MYSQL"));




	}




}
