package com.example.mohamedabdelazizhamad.datesofphotographer.models;

public class ClientDetails {
    private String client_name,client_phone,date,place,type_work,total_price,paid,residual;

    public ClientDetails() {
    }

    public ClientDetails(String client_name, String client_phone, String date, String place, String type_work, String total_price, String paid, String residual) {
        this.client_name = client_name;
        this.client_phone = client_phone;
        this.date = date;
        this.place = place;
        this.type_work = type_work;
        this.total_price = total_price;
        this.paid = paid;
        this.residual = residual;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public void setClient_phone(String client_phone) {
        this.client_phone = client_phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getType_work() {
        return type_work;
    }

    public void setType_work(String type_work) {
        this.type_work = type_work;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getResidual() {
        return residual;
    }

    public void setResidual(String residual) {
        this.residual = residual;
    }
}
