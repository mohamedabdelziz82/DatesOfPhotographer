package com.example.mohamedabdelazizhamad.datesofphotographer.models;

public class WorksModel {
    private String work_name,work_price;

    public WorksModel() {
    }

    public WorksModel(String work_name, String work_price) {
        this.work_name = work_name;
        this.work_price = work_price;
    }

    public String getWork_name() {
        return work_name;
    }

    public void setWork_name(String work_name) {
        this.work_name = work_name;
    }

    public String getWork_price() {
        return work_price;
    }

    public void setWork_price(String work_price) {
        this.work_price = work_price;
    }
}
