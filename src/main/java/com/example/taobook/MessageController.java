package com.example.taobook;


import com.example.taobook.datasour.MessageEntity;
import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
            if(from == null) throw new Exception("Invalid Sender - User not found: " + messageInfo.fromUid);
            if(to == null) throw new Exception("Invalid Receiver - User not found: " + messageInfo.toUid);
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setId(id);
            messageEntity.setType(messageInfo.type);
            messageEntity.setSender(from);
            messageEntity.setReceiver(to);
            messageEntity.setTime(time);
            messageEntity.setContent(messageInfo.content);
            messageEntity.setStatus(status);
            Transaction t = session.beginTransaction();
            session.save(messageEntity);
            t.commit();
            session.close();
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    @RequestMapping(value = "/message/records", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAllRecords(@RequestBody GetRecordsInfo getRecordsInfo){
        




        return "xxx";
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

class GetRecordsInfo{
    @JsonProperty(value="user_id")
    String user_id;
}
