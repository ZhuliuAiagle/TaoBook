package com.example.taobook;

import com.example.taobook.datasour.ItemEntity;
import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@RestController
public class ItemController {
    @RequestMapping(value = "/item/add", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String AddNewItem(@RequestBody NewItemInfo newItemInfo){
        ItemEntity it = new ItemEntity();
        // 需要插入数据库的数据
        // 利用时间戳自动生成物品id
        String itemId = new Date().toString().replaceAll(" ","");
        String userId = newItemInfo.userId;
        String reqId = newItemInfo.reqId;
        String name = newItemInfo.name;
        String clazz = newItemInfo.clazz;
        int type = newItemInfo.type;
        String description = newItemInfo.description;
        String img = newItemInfo.img;
        int stock = newItemInfo.stock;
        BigDecimal price = newItemInfo.price;
        int buyCount = 0;
        Date date = new Date();
        // 添加时间戳
        java.sql.Timestamp ts;
        ts = new Timestamp(date.getTime());
        try{
            it.setId(itemId);
            it.setTime(ts);
            it.setName(name);
            System.out.println(it.getName());
            it.setClazz(clazz);
            it.setType(type);
            it.setDescription(description);
            it.setImg(img);
            it.setStock(stock);
            it.setPrice(price);
            it.setBuyCount(buyCount);
            // 获取当前插入用户的用户信息实
            Session session = TaoBookApplication.sessionFactory.openSession();
            UserEntity pb = session.get(UserEntity.class, userId);
            if(pb == null){
                System.out.println("User not found:" + userId);
                throw new ClassNotFoundException("User not found:" + userId);
            }
            it.setPublisher(pb);
            UserEntity rq = session.get(UserEntity.class, reqId);
            if(rq == null){
                System.out.println("User not found:" + reqId);
                throw new ClassNotFoundException("User not found:" + reqId);
            }
            it.setRequester(rq);
            Transaction tran = session.beginTransaction();
            session.save(it);
            tran.commit();
            session.close();
            return "SUCCESS";
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
            return e.getMessage();
        }catch(Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    @RequestMapping(value = "/item/delete", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String DeleteItem(@RequestBody DeleteItemInfo deleteItemInfo){
        String userId = deleteItemInfo.userId;
        String itemId = deleteItemInfo.itemId;
        try{
            Session session = TaoBookApplication.sessionFactory.openSession();
            UserEntity u = session.get(UserEntity.class, userId);
            if(u == null) throw new ClassNotFoundException("Invalid User " + userId);
            ItemEntity it = session.get(ItemEntity.class, itemId);
            if(it == null) throw new ClassNotFoundException("Invalid Item " + itemId);
            String truePublisher = it.getPublisher().getId();
            if(!truePublisher.equals(userId)) throw new UnsupportedOperationException("Username doesn't match this item");
            Transaction tran = session.beginTransaction();
            session.delete(it);
            tran.commit();
            session.close();
            return "SUCCESS";
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return e.getMessage();
        }catch(UnsupportedOperationException e){
            e.printStackTrace();
            return e.getMessage();
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }

    }
    @RequestMapping(value = "/item/test", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String TestAPI(){
        return "nmb";
    }
}

class NewItemInfo{
    @JsonProperty(value="user_id")
        String userId;
    @JsonProperty(value = "req_id")
        String reqId;
    @JsonProperty(value="itemname")
        String name;
    @JsonProperty(value="clazz")
        String clazz;
    @JsonProperty(value="type")
        int type;
    @JsonProperty(value="description")
        String description;
    @JsonProperty(value="img")
        String img;
    @JsonProperty(value="stock")
        int stock;
    @JsonProperty(value="price")
        BigDecimal price;
}

// 删除的话，除非是管理员，必须保证user_id和item_id一致
class DeleteItemInfo{
    @JsonProperty(value="user_id")
    String userId;
    @JsonProperty(value="item_id")
    String itemId;
}
