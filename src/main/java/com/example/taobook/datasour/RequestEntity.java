package com.example.taobook.datasour;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "REQUEST", schema = "BOOK_SELLING")
public class RequestEntity {
    private String id;
    private Timestamp time;
    private String name;
    private String clazz;
    private String description;
    private BigDecimal priceCeil;
    private BigDecimal priceFloor;
    private Timestamp deliTime;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "class")
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "price_ceil")
    public BigDecimal getPriceCeil() {
        return priceCeil;
    }

    public void setPriceCeil(BigDecimal priceCeil) {
        this.priceCeil = priceCeil;
    }

    @Basic
    @Column(name = "price_floor")
    public BigDecimal getPriceFloor() {
        return priceFloor;
    }

    public void setPriceFloor(BigDecimal priceFloor) {
        this.priceFloor = priceFloor;
    }

    @Basic
    @Column(name = "deli_time")
    public Timestamp getDeliTime() {
        return deliTime;
    }

    public void setDeliTime(Timestamp deliTime) {
        this.deliTime = deliTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestEntity that = (RequestEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(time, that.time) &&
                Objects.equals(name, that.name) &&
                Objects.equals(clazz, that.clazz) &&
                Objects.equals(description, that.description) &&
                Objects.equals(priceCeil, that.priceCeil) &&
                Objects.equals(priceFloor, that.priceFloor) &&
                Objects.equals(deliTime, that.deliTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, name, clazz, description, priceCeil, priceFloor, deliTime);
    }
}
