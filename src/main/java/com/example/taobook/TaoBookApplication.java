package com.example.taobook;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaoBookApplication {
	// 数据库配置
	private static Configuration config = new Configuration().configure();
	public static SessionFactory sessionFactory = config.buildSessionFactory();

	public static void main(String[] args) {
		SpringApplication.run(TaoBookApplication.class, args);
	}
}
