package com.example.taobook;

import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taobook.datasour.*;


@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String say(){
        Session session = TaoBookApplication.sessionFactory.openSession();
        UserEntity u = session.get(UserEntity.class,"nm00001");
        session.close();
        return "hello, your email is: " + u.getEmail();
    }
}


