package com.example.taobook;


import com.example.taobook.datasour.AccountEntity;
import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.Date;

@RestController
public class AccountController {
    @RequestMapping(value = "/account/add", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String AddAccount(@RequestBody AddAccountInfo addAccountInfo){
        String userId = addAccountInfo.userId;
        String id = addAccountInfo.id;
        int isBank = addAccountInfo.isBank;
        int bankType = addAccountInfo.bankType;
        String bankAccount = addAccountInfo.bankAccount;
        BigDecimal balance = new BigDecimal(0);
        int status = 1; // 正常账户状态
        try{
            Session session = TaoBookApplication.sessionFactory.openSession();
            UserEntity user = session.get(UserEntity.class, userId);
            if(user == null) throw new Exception("No such user: "+userId);
            AccountEntity newAccount = new AccountEntity();
            newAccount.setId(id);
            newAccount.setUser(user);
            newAccount.setStatus(status);
            newAccount.setBankType(bankType);
            newAccount.setIsBank(isBank);
            newAccount.setBankAccount(bankAccount);
            newAccount.setBalance(balance);
            Transaction tran = session.beginTransaction();
            session.save(newAccount);
            tran.commit();
            session.close();
            return "SUCCESS";

        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    @RequestMapping(value = "/account/recharge", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String Recharge(@RequestBody RechargeInfo rechargeInfo){
        String id = rechargeInfo.id;
        BigDecimal amount = rechargeInfo.amount;
        try{
            Session session = TaoBookApplication.sessionFactory.openSession();
            AccountEntity account = session.get(AccountEntity.class, id);
            if(account == null) throw new Exception("No such account: " + id);
            account.setBalance(account.getBalance().add(amount));
            Transaction tran = session.beginTransaction();
            session.update(account);
            tran.commit();
            session.close();
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}

class AddAccountInfo{
    @JsonProperty("id")
    String id; // 资金账户id
    @JsonProperty("user_id")
    String userId; // 用户id
    @JsonProperty("is_bank")
    int isBank; // 是否是银行账户
    @JsonProperty("bank_type")
    int bankType; // 银行种类
    @JsonProperty("bank_account")
    String bankAccount; // 账户信息（由手机通过支付宝获取（接口预留））
}

class RechargeInfo{
    @JsonProperty("id")
    String id;  // account id
    @JsonProperty("amount")
    BigDecimal amount; // the amount of recharged money
}

