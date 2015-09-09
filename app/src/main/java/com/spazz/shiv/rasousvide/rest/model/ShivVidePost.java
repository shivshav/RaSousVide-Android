package com.spazz.shiv.rasousvide.rest.model;

public class ShivVidePost {     // realworld data
    private double cycle_time;       // 2.0
    private double duty_cycle;       // 100.0
    private double d_param;          // 8.0
    private double i_param;          // 165.0
    private double k_param;          // 44.0
    private String mode;             // auto
    private double set_point;        // 146.0
    private double boilManageTemp;   // 145.40
    private int numPointsSmooth;

    public ShivVidePost() {
        this.mode = "Off";
        this.set_point = 146.0;
        this.k_param = 44.0;
        this.i_param = 165.0;
        this.d_param = 8.0;
        this.boilManageTemp = 145.40;
        this.cycle_time = 2.0;
        this.duty_cycle = 100.0;
        this.numPointsSmooth = 5;
    }

    public ShivVidePost(String mode) {
        super();
        this.mode = mode;
    }

    public double getCycle_time() {
        return cycle_time;
    }

    public void setCycle_time(double cycle_time) {
        this.cycle_time = cycle_time;
    }

    public double getDuty_cycle() {
        return duty_cycle;
    }

    public void setDuty_cycle(double duty_cycle) {
        this.duty_cycle = duty_cycle;
    }

    public double getD_param() {
        return d_param;
    }

    public void setD_param(double d_param) {
        this.d_param = d_param;
    }

    public double getI_param() {
        return i_param;
    }

    public void setI_param(double i_param) {
        this.i_param = i_param;
    }

    public double getK_param() {
        return k_param;
    }

    public void setK_param(double k_param) {
        this.k_param = k_param;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public double getSet_point() {
        return set_point;
    }

    public void setSet_point(double set_point) {
        this.set_point = set_point;
    }

    public double getBoilManageTemp() {
        return boilManageTemp;
    }

    public void setBoilManageTemp(double boilManageTemp) {
        this.boilManageTemp = boilManageTemp;
    }

    public int getNumPointsSmooth() {
        return numPointsSmooth;
    }

    public void setNumPointsSmooth(int numPointsSmooth) {
        this.numPointsSmooth = numPointsSmooth;
    }
}
