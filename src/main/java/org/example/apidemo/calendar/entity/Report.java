package org.example.apidemo.calendar.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "ess_test")
public class Report {

    @Id
    private String date;

    @Column(name = "tot_trans")  // DB 컬럼 이름과 매핑
    private float totTrans;

    @Column(name = "charge")  // 필요시 컬럼 이름 명시
    private float charge;

    @Column(name = "discharge")  // 필요시 컬럼 이름 명시
    private float discharge;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getTotTrans() {
        return totTrans;
    }

    public void setTotTrans(float totTrans) {
        this.totTrans = totTrans;
    }

    public float getCharge() {
        return charge;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }

    public float getDischarge() {
        return discharge;
    }

    public void setDischarge(float discharge) {
        this.discharge = discharge;
    }
}
