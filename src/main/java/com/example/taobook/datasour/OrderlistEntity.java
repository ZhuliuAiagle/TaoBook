package com.example.taobook.datasour;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "ORDERLIST", schema = "BOOK_SELLING")
public class OrderlistEntity {
    private String id;
    private int count;
    private BigDecimal pay;
    private int payType;
    private int status;
    private Timestamp subTime;
    private Timestamp accTime;
    private Timestamp sedTime;
    private Timestamp recTime;
    private String comment;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="item_id", referencedColumnName = "id")
    private ItemEntity item;

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    @ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="buy_acid", referencedColumnName = "id")
    private AccountEntity account;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "count")
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Basic
    @Column(name = "pay")
    public BigDecimal getPay() {
        return pay;
    }

    public void setPay(BigDecimal pay) {
        this.pay = pay;
    }

    @Basic
    @Column(name = "pay_type")
    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    @Basic
    @Column(name = "status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Basic
    @Column(name = "sub_time")
    public Timestamp getSubTime() {
        return subTime;
    }

    public void setSubTime(Timestamp subTime) {
        this.subTime = subTime;
    }

    @Basic
    @Column(name = "acc_time")
    public Timestamp getAccTime() {
        return accTime;
    }

    public void setAccTime(Timestamp accTime) {
        this.accTime = accTime;
    }

    @Basic
    @Column(name = "sed_time")
    public Timestamp getSedTime() {
        return sedTime;
    }

    public void setSedTime(Timestamp sedTime) {
        this.sedTime = sedTime;
    }

    @Basic
    @Column(name = "rec_time")
    public Timestamp getRecTime() {
        return recTime;
    }

    public void setRecTime(Timestamp recTime) {
        this.recTime = recTime;
    }

    @Basic
    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderlistEntity that = (OrderlistEntity) o;
        return count == that.count &&
                payType == that.payType &&
                status == that.status &&
                Objects.equals(id, that.id) &&
                Objects.equals(pay, that.pay) &&
                Objects.equals(subTime, that.subTime) &&
                Objects.equals(accTime, that.accTime) &&
                Objects.equals(sedTime, that.sedTime) &&
                Objects.equals(recTime, that.recTime) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, count, pay, payType, status, subTime, accTime, sedTime, recTime, comment);
    }
}
