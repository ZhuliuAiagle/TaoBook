package com.example.taobook;


import com.example.taobook.datasour.RequestEntity;
import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
public class RequestController {
    @RequestMapping(value = "/request/add", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String AddNewRequest(@RequestBody RequestInfo requestInfo){
        String id = new Date().toString().replaceAll(" ","") + "_r";
        String userId = requestInfo.userId;
        Timestamp publishTime = new Timestamp(new Date().getTime());
        String name = requestInfo.name;
        String clazz = requestInfo.clazz;
        String description = requestInfo.description;
        System.out.println(description);
        BigDecimal priceCeil = requestInfo.priceCeil;
        BigDecimal priceFloor = requestInfo.priceFloor;
        System.out.println(requestInfo.deliTime);
        Date deliTime = java.sql.Date.valueOf(requestInfo.deliTime);
        Timestamp deliTime_2 = new Timestamp(deliTime.getTime());
        RequestEntity requestEntity = new RequestEntity();
        try{
            requestEntity.setId(id);
            Session session = TaoBookApplication.sessionFactory.openSession();
            UserEntity publisher = session.get(UserEntity.class, userId);
            if(publisher == null) throw new Exception("No such user: " + userId);
            if(priceCeil.compareTo(priceFloor) == -1) throw new Exception("Invalid price range: ceil smaller than floor");
            requestEntity.setPublisher(publisher);
            requestEntity.setName(name);
            requestEntity.setTime(publishTime);
            requestEntity.setClazz(clazz);
            requestEntity.setDescription(description);
            requestEntity.setPriceCeil(priceCeil);
            requestEntity.setPriceFloor(priceFloor);
            requestEntity.setDeliTime(deliTime_2);
            requestEntity.setImg(requestInfo.img);
            Transaction t = session.beginTransaction();
            session.save(requestEntity);
            t.commit();
            session.close();
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    @RequestMapping(value = "/request/timeline", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getTimeLine(){

        Session session = TaoBookApplication.sessionFactory.openSession();
        String hql = "select rq from RequestEntity rq";
        List<RequestEntity> l = session.createQuery(hql).list();
        Collections.sort(l);
        Collections.reverse(l);
        JSONObject joex = new JSONObject();
        JSONObject ret = new JSONObject();
        JSONArray result = new JSONArray();
        try{
            joex.put("status","failed");
            for(RequestEntity r: l){
                JSONObject j = new JSONObject();
                j.put("user_id",r.getPublisher().getId());
                j.put("time",r.getTime().toString());
                j.put("name",r.getName());
                j.put("clazz",r.getClazz());
                j.put("description",r.getDescription());
                result.put(j);
            }
            ret.put("status","success");
            ret.put("data",result);
            return ret.toString();

        }catch (Exception e){
            e.printStackTrace();
            return joex.toString();
        }
    }
}

class RequestInfo{
    @JsonProperty(value="user_id")
    String userId; // id
    @JsonProperty(value = "name")
    String name; // 标题
    @JsonProperty(value="clazz")
    String clazz; // 类别
    @JsonProperty(value="description")
    String description; // 描述
    @JsonProperty(value="price_ceil")
    BigDecimal priceCeil; // 价格参数（用户后端判断）
    @JsonProperty(value="price_floor")
    BigDecimal priceFloor;
    @JsonProperty(value="deli_time")
    String deliTime; // in 'yyyy-mm-dd hh:mm:ss' format
    @JsonProperty(value="img")
    String img;
}