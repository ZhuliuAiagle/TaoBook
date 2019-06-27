package com.example.taobook;


import com.example.taobook.datasour.RequestEntity;
import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

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
        Timestamp deliTime = Timestamp.valueOf(requestInfo.deliTime);
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
            requestEntity.setDeliTime(deliTime);
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
}

class RequestInfo{
    @JsonProperty(value="user_id")
    String userId;
    @JsonProperty(value = "name")
    String name;
    @JsonProperty(value="clazz")
    String clazz;
    @JsonProperty(value="description")
    String description;
    @JsonProperty(value="price_ceil")
    BigDecimal priceCeil;
    @JsonProperty(value="price_floor")
    BigDecimal priceFloor;
    @JsonProperty(value="deli_time")
    String deliTime; // in 'yyyy-mm-dd hh:mm:ss' format
    @JsonProperty(value="img")
    String img;
}