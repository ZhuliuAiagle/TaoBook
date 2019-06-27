package com.example.taobook;


import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;

@RestController
public class MessageController {
    @RequestMapping(value = "/message/send", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String SendMessage(@RequestBody MessageInfo messageInfo){
        String id = new Date().toString().replaceAll(" ","").replaceAll(":","") + "_m";
        Timestamp time = new Timestamp(new Date().getTime());
        int status = 1; // 未读
        try {
            Session session = TaoBookApplication.sessionFactory.openSession();
            UserEntity from = session.get(UserEntity.class, messageInfo.fromUid);
            UserEntity to = session.get(UserEntity.class, messageInfo.toUid);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}

class MessageInfo{
    @JsonProperty(value="type")
    int type;
    @JsonProperty(value="from_uid")
    String fromUid;
    @JsonProperty(value="to_uid")
    String toUid;
    @JsonProperty(value = "content")
    String content;
}
