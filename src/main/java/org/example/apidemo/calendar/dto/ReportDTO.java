package org.example.apidemo.calendar.dto;

public class ReportDTO {
    private String date;
    private float totTrans;
    private float charge;
    private float discharge;

    public ReportDTO(String date, float totTrans, float charge, float discharge) {
        this.date = date;
        this.totTrans = totTrans;
        this.charge = charge;
        this.discharge = discharge;
    }

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

