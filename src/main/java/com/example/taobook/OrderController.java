package com.example.taobook;


import com.example.taobook.datasour.AccountEntity;
import com.example.taobook.datasour.ItemEntity;
import com.example.taobook.datasour.OrderlistEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            if(it.getStock() <= 0) throw new Exception("This item has sold out");
            orderlistEntity.setAccount(account);
            orderlistEntity.setItem(it);
            orderlistEntity.setPay(it.getPrice().multiply(BigDecimal.valueOf(count))); // 商品的合计价格
            orderlistEntity.setType(orderInfo.type);
            Transaction tran = session.beginTransaction();
            session.save(orderlistEntity);
            tran.commit();
            // 更新库存事务
            Transaction stockUpdateTran = session.beginTransaction();
            it.setStock(it.getStock() - 1);
            session.update(it);
            stockUpdateTran.commit();
            session.close();
            return "SUCCESS";
        }catch(ClassNotFoundException e){
          e.printStackTrace();
            return e.getMessage();
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
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
            if(order.getStatus() == 1) throw new ClassNotFoundException("This order has not been confirmed yet");
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
        String comment = signingInfo.comment;
        try{
            Session session = TaoBookApplication.sessionFactory.openSession();
            OrderlistEntity order = session.get(OrderlistEntity.class, order_id);
            if(order == null) throw new ClassNotFoundException("No such order: "+ order_id);
            String realId = order.getAccount().getUser().getId();
            if(!realId.equals(user_id)) throw new ClassNotFoundException("Buyer does not match: " + user_id);
            if(order.getStatus() != 4)  throw new ClassNotFoundException("This order can't be signed. status="+order.getStatus());
            order.setStatus(5);
            order.setRecTime(new Timestamp(new Date().getTime()));
            order.setComment(comment);
            Transaction tran_1 = session.beginTransaction();
            session.update(order);
            tran_1.commit();
            // 更新账户余额
            AccountEntity account = order.getAccount();
            account.setBalance(account.getBalance().subtract(order.getPay()));
            Transaction tran_2 = session.beginTransaction();
            session.update(account);
            tran_2.commit();
            session.close();
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
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
    @JsonProperty(value="type")
    int type;
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