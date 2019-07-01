package com.example.taobook;


import com.example.taobook.datasour.ItemEntity;
import com.example.taobook.datasour.MessageEntity;
import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

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
        String userId = getRecordsInfo.user_id;
        JSONObject joex = new JSONObject();
        //
        Session session = TaoBookApplication.sessionFactory.openSession();
        JSONObject ret = new JSONObject();
        JSONArray jsonSend = new JSONArray();
        JSONArray jsonReceive = new JSONArray();
        try {
            // 发送者
            String hql = "select msg from MessageEntity msg where msg.sender.id = :sid";
            List<MessageEntity> result = session.createQuery(hql).setParameter("sid",userId).list();
            joex.put("status","failed");
            for (MessageEntity it : result) {
                JSONObject jo = new JSONObject();
                jo.put("sender", it.getSender().getId());
                jo.put("sender_nick", it.getSender().getNickname());
                jo.put("reciver", it.getReceiver().getId());
                jo.put("reciver_nick", it.getReceiver().getNickname());
                jo.put("content", it.getContent());
                jsonSend.put(jo);
            }
            // 接收者
            String hqlRev = "select msg from MessageEntity msg where msg.receiver.id = :sid";
            List<MessageEntity> resultRev = session.createQuery(hqlRev).setParameter("sid", userId).list();
            System.out.println(resultRev.size());
            for (MessageEntity it : resultRev) {
                JSONObject jo = new JSONObject();
                jo.put("reciver", it.getReceiver().getId());
                jo.put("reciver_nick", it.getReceiver().getNickname());
                jo.put("sender", it.getSender().getId());
                jo.put("sender_nick", it.getSender().getNickname());
                jo.put("content", it.getContent());
                jsonReceive.put(jo);
            }
            ret.put("status","success");
            ret.put("send",jsonSend);
            ret.put("receive",jsonReceive);
            return ret.toString();
        }catch (Exception e){
            e.printStackTrace();
            return joex.toString();
        }
    }
    @RequestMapping(value = "/message/msglist", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getRecordsByUser(@RequestBody GetRecordsInfo getRecordsInfo){
        String userId = getRecordsInfo.user_id;
        Session session = TaoBookApplication.sessionFactory.openSession();
        String hqlSed = "select msg from MessageEntity msg where msg.sender.id = :sid";
        List<MessageEntity> resultSed = session.createQuery(hqlSed).setParameter("sid",userId).list();
        String hqlRev = "select msg from MessageEntity msg where msg.receiver.id = :sid";
        List<MessageEntity> resultRev = session.createQuery(hqlRev).setParameter("sid", userId).list();
        // 首先，把所有交谈过的人都提取出来
        // 交谈过的人包括resultSed中的receiver和rev中的sender
        HashMap<UserEntity, List<MessageEntity>> pt = new HashMap<>();
        for(MessageEntity m:resultSed){
            UserEntity u = m.getReceiver();
            if(pt.get(u) == null){
                List<MessageEntity> l = new ArrayList<>();
                l.add(m);
                pt.put(u,l);
            }else{
                pt.get(u).add(m);
            }
        }
        for(MessageEntity m:resultRev){
            UserEntity u = m.getSender();
            if(pt.get(u) == null){
                List<MessageEntity> l = new ArrayList<>();
                l.add(m);
                pt.put(u,l);
            }else{
                pt.get(u).add(m);
            }
        }
        JSONObject joex = new JSONObject();
        try {
            joex.put("status","failed");
            JSONArray msgRecords = new JSONArray();
            for (Map.Entry<UserEntity, List<MessageEntity>> entry : pt.entrySet()) {
                UserEntity u = entry.getKey();
                List<MessageEntity> l = entry.getValue();
                Collections.sort(l);
                JSONObject msgRecordsByUser = new JSONObject();
                JSONArray msgRecordByUserArray = new JSONArray();
                for (MessageEntity m : l) {
                    JSONObject singleMsg = new JSONObject();
                    singleMsg.put("sender_id",m.getSender().getId());
                    singleMsg.put("receiver_id",m.getReceiver().getId());
                    singleMsg.put("sender_nick",m.getSender().getNickname());
                    singleMsg.put("receiver_nick",m.getReceiver().getNickname());
                    singleMsg.put("content",m.getContent());
                    singleMsg.put("time",m.getTime().toString());
                    msgRecordByUserArray.put(singleMsg);
                }
                msgRecordsByUser.put("partner_id",u.getId());
                msgRecordsByUser.put("partner_nick",u.getNickname());
                msgRecordsByUser.put("records",msgRecordByUserArray);
                msgRecords.put(msgRecordsByUser);
            }
            JSONObject ret = new JSONObject();
            ret.put("status","success");
            ret.put("data",msgRecords);
            return ret.toString();
        }catch(Exception e){
            e.printStackTrace();
            return joex.toString();
        }
    }
}



class MessageInfo{
    @JsonProperty(value="type")
    int type; // 消息类型
    @JsonProperty(value="from_uid")
    String fromUid; // 发送者
    @JsonProperty(value="to_uid")
    String toUid; // 接收者
    @JsonProperty(value = "content")
    String content; // 内容
}

class GetRecordsInfo{
    @JsonProperty(value="user_id")
    String user_id;
}
