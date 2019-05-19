package com.example.taobook;

import com.example.taobook.datasour.UserEntity;
import org.apache.catalina.User;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class TaoBookApplication {
	// 数据库配置
	private static Configuration config = new Configuration().configure();
	public static SessionFactory sessionFactory = config.buildSessionFactory();

	public static void main(String[] args) {
		SpringApplication.run(TaoBookApplication.class, args);
	}
}
