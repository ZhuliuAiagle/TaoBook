package com.example.taobook;

import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taobook.datasour.*;

import java.sql.Timestamp;
import java.util.Date;


@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String say(){
        Session session = TaoBookApplication.sessionFactory.openSession();
        RequestEntity u = session.get(RequestEntity.class,"test");
        session.close();
        if(u == null) return "shit!";
        return "hello, your email is: " + u.getDescription();
    }
    @RequestMapping("/test")
    public String test(){
        return "xxx";
    }
}


