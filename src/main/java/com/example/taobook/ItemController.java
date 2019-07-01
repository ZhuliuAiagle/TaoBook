package com.example.taobook;

import com.example.taobook.datasour.ItemEntity;
import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mysql.cj.xdevapi.JsonArray;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
            it.setLink(newItemInfo.link);
            // 获取当前插入用户的用户信息实
            Session session = TaoBookApplication.sessionFactory.openSession();
            UserEntity pb = session.get(UserEntity.class, userId);
            if(pb == null){
                System.out.println("User not found:" + userId);
                throw new ClassNotFoundException("User not found:" + userId);
            }
            it.setPublisher(pb);
            UserEntity rq = session.get(UserEntity.class, reqId);
            if(type == 2 && rq == null){
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
    @RequestMapping(value = "/item/getitemsbyclass", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getItemsByClass(@RequestBody GetByClazzInfo getByClazzInfo){
        String[] clazz = getByClazzInfo.clazz;
        Session session = TaoBookApplication.sessionFactory.openSession();
        String hql = "select it from ItemEntity it where it.clazz in(:clazz)";
        List<ItemEntity> results = session.createQuery(hql).setParameterList("clazz",clazz).list();
        JSONArray json = new JSONArray();
        JSONObject ret = new JSONObject();
        try {
            if(results == null) throw new Exception("No result");
            for (ItemEntity it : results) {
                JSONObject jo = new JSONObject();
                jo.put("name", it.getName());
                json.put(jo);
            }
            System.out.println(json.toString());
            ret.put("status","success");
            ret.put("data",json);
            return ret.toString();
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    @RequestMapping(value = "/item/search", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String Search(@RequestBody SearchInfo searchInfo){
        JSONObject joex = new JSONObject();
        try {
                joex.put("status","failed");
                String[] clazz = searchInfo.clazz;
                String keyword = searchInfo.keyword;
                String absKeyword = "%" + keyword + "%";
                Session session = TaoBookApplication.sessionFactory.openSession();
                List<ItemEntity> results;
                if(clazz != null && clazz.length > 0){
                    String hql = "select it from ItemEntity it where it.name like :keyword and it.clazz in (:clazz)";
                    results = session.createQuery(hql).setParameter("keyword",absKeyword).setParameterList("clazz",clazz).list();
                }  // 这种情况视为全部查找
                else{
                    String hql2 = "select it from ItemEntity it where it.name like :keyword";
                    results = session.createQuery(hql2).setParameter("keyword",absKeyword).list();
                }
                JSONArray json = new JSONArray();
                JSONObject ret = new JSONObject();
                for (ItemEntity it : results) {
                    JSONObject jo = new JSONObject();
                    jo.put("id",it.getId());
                    jo.put("name", it.getName());
                    jo.put("publisher_id", it.getPublisher().getId());
                    jo.put("publisher_name", it.getPublisher().getNickname());
                    jo.put("clazz", it.getClazz());
                    jo.put("type",it.getType());
                    if(it.getRequester() != null)
                        jo.put("req_id", it.getRequester().getId());
                    jo.put("description",it.getDescription());
                    jo.put("img",it.getImg());
                    jo.put("stock",it.getStock());
                    jo.put("price",it.getPrice());
                    jo.put("buy_count",it.getBuyCount());
                    jo.put("link",it.getLink());
                    json.put(jo);
                }
                System.out.println(json.toString());
                ret.put("status","success");
                ret.put("data",json);
                return ret.toString();
        }catch (Exception e){
            e.printStackTrace();
            try {
                joex.put("msg", e.getMessage());
            }catch (Exception ei){}
            return joex.toString();
        }
    }
}

class NewItemInfo{
    @JsonProperty(value="user_id")
        String userId; // id
    @JsonProperty(value = "req_id")
        String reqId; // 应求者id
    @JsonProperty(value="itemname")
        String name; // 名称
    @JsonProperty(value="clazz")
        String clazz; // 类别
    @JsonProperty(value="type")
        int type; // 类型
    @JsonProperty(value="description")
        String description; // 描述
    @JsonProperty(value="img")
        String img; // 图片链接
    @JsonProperty(value="stock")
        int stock; // 库存
    @JsonProperty(value="price")
        BigDecimal price; // 价格
    @JsonProperty(value="link")
        String link; // 外链（如果有）
}

// 删除的话，除非是管理员，必须保证user_id和item_id一致
class DeleteItemInfo{
    @JsonProperty(value="user_id")
    String userId;
    @JsonProperty(value="item_id")
    String itemId;
}

class GetByClazzInfo{
    @JsonProperty(value="clazz")
    String[] clazz;
}


class SearchInfo{
    @JsonProperty(value = "keyword")
    String keyword; // 关键词
    @JsonProperty(value = "clazz")
    String[] clazz; // 类别复选数组
}
