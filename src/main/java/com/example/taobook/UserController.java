package com.example.taobook;

import com.example.taobook.datasour.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.sql.Date;

@RestController
public class UserController {
    // useful global varibles
    // minimum password length
    public static int MINI_PW_LENGTH = 8;

    @RequestMapping(value = "/login", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String Login(@RequestBody LoginInfo loginInfo){
        Session session = TaoBookApplication.sessionFactory.openSession();
        UserEntity u = session.get(UserEntity.class,loginInfo.username);
        session.close();
        if(u == null)
            return "No such username";
        String truePassword = u.getPassword();
        if(truePassword.equals(loginInfo.password))
            return "Login Successfully";
        else
            return "Wrong Password";
    }
    @RequestMapping(value = "/register",method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String Register(@RequestBody RegisterInfo registerInfo){
        UserEntity u = new UserEntity();
        try{
            verifyUserDataFormats(registerInfo);  // throw assertion error
            updateAllUserData(u, registerInfo);
            Session session = TaoBookApplication.sessionFactory.openSession();
            Transaction tran = session.beginTransaction();
            session.save(u);
            tran.commit();
            session.close();
        }catch(AssertionError e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }catch(NullPointerException e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return "200 OK";
    }
    @RequestMapping(value = "/modify",method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String Modify(@RequestBody ModifyInfo modifyInfo){
        try{
            verifyUserDataFormats(modifyInfo);  // throw assertion error
            Session session = TaoBookApplication.sessionFactory.openSession();
            UserEntity u = session.get(UserEntity.class,modifyInfo.username);
            session.close();
            if(u != null){
                updateAllUserData(u,modifyInfo);
                session = TaoBookApplication.sessionFactory.openSession();
                Transaction tran = session.beginTransaction();
                session.update(u);
                tran.commit();
                session.close();

            }else{
                throw new NullPointerException("No Such Id");
            }
        }catch(AssertionError e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }catch(NullPointerException e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return "200 OK";
    }

    // justify the formats of data
    private void verifyUserDataFormats(RegisterInfo registerInfo)
            throws AssertionError{
        assert registerInfo.username.length() != 0;
        assert registerInfo.nickname.length() != 0;
        assert registerInfo.password.length() >= MINI_PW_LENGTH;
        assert registerInfo.email.length() != 0;
        assert registerInfo.phone.length() != 0;
        assert registerInfo.role == 0 || registerInfo.role == 1;
        assert registerInfo.sex == 0 || registerInfo.sex == 1;
    }
    private void updateAllUserData(UserEntity u, RegisterInfo r)
            throws IllegalArgumentException, NullPointerException{
        if(u == null) throw new NullPointerException("UserEntity Not Initialized");
        u.setId(r.username);
        u.setNickname(r.nickname);
        u.setPassword(r.password);
        u.setRole(r.role);
        u.setSex(r.sex);
        u.setBirthday(Date.valueOf(r.birthday));
        u.setEmail(r.email);
        u.setPhone(r.phone);
    }
}

// pojo class
// logininfo:for login
class LoginInfo{
    @JsonProperty(value="username")
    String username;
    @JsonProperty(value="password")
    String password;
}

//registerinfo:for register
class RegisterInfo{
    @JsonProperty(value="username")
    String username;
    @JsonProperty(value="role")
    int role;
    @JsonProperty(value="nickname")
    String nickname;
    @JsonProperty(value="password")
    String password;
    @JsonProperty(value="sex")
    int sex;
    @JsonProperty(value="birthday")
    String birthday;
    @JsonProperty(value="phone")
    String phone;
    @JsonProperty(value="email")
    String email;
}

// modifyinfo: for the modification of infromation
class ModifyInfo extends RegisterInfo {}