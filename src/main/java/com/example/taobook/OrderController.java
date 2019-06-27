package com.example.taobook;


import com.example.taobook.datasour.AccountEntity;
import com.example.taobook.datasour.ItemEntity;
import com.example.taobook.datasour.OrderlistEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@RestController
public class OrderController {
    @RequestMapping(value = "/order/submit", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String SubmitOrder(@RequestBody OrderInfo orderInfo){
        String order_id = (new Date().toString().replaceAll(" ","")) + "_o" ;
        String acc_id = orderInfo.accId;
        String item_id = orderInfo.itemId;
        int count = orderInfo.count;
        int payType = orderInfo.payType;
        int status = 1; // 初始状态是默认已经提交订单
        java.sql.Timestamp sub_time = new Timestamp(new Date().getTime());
        OrderlistEntity orderlistEntity = new OrderlistEntity();
        try {
            orderlistEntity.setId(order_id);
            orderlistEntity.setCount(count);
            orderlistEntity.setPayType(payType);
            orderlistEntity.setStatus(status);
            orderlistEntity.setSubTime(sub_time);
            Session session = TaoBookApplication.sessionFactory.openSession();
            ItemEntity it = session.get(ItemEntity.class, item_id);
            if(it == null) throw new ClassNotFoundException("Invalid item: " + item_id);
            AccountEntity account = session.get(AccountEntity.class, acc_id);
            if(account == null) throw new ClassNotFoundException("Invalid wallet account:" + acc_id);
            orderlistEntity.setAccount(account);
            orderlistEntity.setItem(it);
            orderlistEntity.setPay(it.getPrice().multiply(BigDecimal.valueOf(count))); // 商品的合计价格
            Transaction tran = session.beginTransaction();
            session.save(orderlistEntity);
            tran.commit();
            session.close();
            return "SUCCESS";
        }catch(ClassNotFoundException e){
          e.printStackTrace();
          return "INVALID ITEM OR ACCOUNT";
        } catch (Exception e){
            e.printStackTrace();
            return "INTERNAL ERROR";
        }
    }
    @RequestMapping(value = "/order/confirm", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String ComfirmOrder(@RequestBody ConfirmOrderInfo confirmOrderInfo){
        String user_id = confirmOrderInfo.userId;
        String order_id = confirmOrderInfo.orderId;
        try {
            Session session = TaoBookApplication.sessionFactory.openSession();
            OrderlistEntity order = session.get(OrderlistEntity.class, order_id);
            if(order == null) throw new ClassNotFoundException("No such order: "+ order_id);
            String itemOwner = order.getItem().getPublisher().getId();
            if (!itemOwner.equals(user_id)) throw new ClassNotFoundException("Publisher does not match: " + user_id);
            if(order.getStatus() > 1) throw new ClassNotFoundException("This order has already been confirmed");
            if(confirmOrderInfo.isAccepted == 0) order.setStatus(3); // 订单被拒绝
            else order.setStatus(2); // 订单被确认,但未发货
            order.setAccTime(new Timestamp(new Date().getTime()));
            Transaction tran = session.beginTransaction();
            session.update(order);
            tran.commit();
            session.close();
            return "SUCCESS";
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            return e.getMessage();
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    @RequestMapping(value = "/order/delivery", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String DeliverOrder(@RequestBody DeliveryInfo deliveryInfo){
        String user_id = deliveryInfo.userId;
        String order_id = deliveryInfo.orderId;
        try{
            Session session = TaoBookApplication.sessionFactory.openSession();
            OrderlistEntity order = session.get(OrderlistEntity.class, order_id);
            if(order == null) throw new ClassNotFoundException("No such order: "+ order_id);
            String itemOwner = order.getItem().getPublisher().getId();
            if(!itemOwner.equals(user_id)) throw new ClassNotFoundException("Publisher does not match: " + user_id);
            if(order.getStatus() == 3) throw new ClassNotFoundException("This order has been aborted");
            if(order.getStatus() > 3) throw new ClassNotFoundException("This order has already been delivered");
            order.setStatus(4);
            order.setSedTime(new Timestamp(new Date().getTime()));
            Transaction tran = session.beginTransaction();
            session.update(order);
            tran.commit();
            session.close();
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    @RequestMapping(value = "/order/signing", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String SigningOrder(@RequestBody SigningInfo signingInfo){
        String user_id = signingInfo.userId;
        String order_id = signingInfo.orderId;
        
    }
}

class OrderInfo{
    @JsonProperty(value="acc_id")
    String accId;
    @JsonProperty(value="item_id")
    String itemId;
    @JsonProperty(value="count")
    int count;
    @JsonProperty(value="pay_type")
    int payType;
}

class ConfirmOrderInfo{
    @JsonProperty(value="user_id")
    String userId;
    @JsonProperty(value="order_id")
    String orderId;
    @JsonProperty(value = "is_accepted")
    int isAccepted;
}

class DeliveryInfo{
    @JsonProperty(value="user_id")
    String userId;
    @JsonProperty(value="order_id")
    String orderId;
}

class SigningInfo{
    @JsonProperty(value="user_id")
    String userId;
    @JsonProperty(value="order_id")
    String orderId;
    @JsonProperty(value="comment")
    String comment;
}