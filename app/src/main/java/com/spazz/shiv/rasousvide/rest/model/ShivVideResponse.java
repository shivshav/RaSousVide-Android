package com.spazz.shiv.rasousvide.rest.model;

/**
 * Created by pula on 1/18/15.
 *
 * replace() is used on the strings to take any newline characters and replace them with escape
 * characters to avoid showing the string literal "\n"
 *
 * Reference: http://stackoverflow.com/questions/11380633/android-textview-not-supporting-line-break
 */
public class ShivVideResponse {     // realworld data
    private float cycle_time;       // 2.0
    private float duty_cycle;       // 100.0
    private float elapsed;          // 1.35
    private float d_param;          // 8.0
    private float i_param;          // 165.0
    private float k_param;          // 44.0
    private String mode;            // auto
    private int myTempSensorNum;    // 0
    private int numTempSensors;     // 1
    private float set_point;        // 146.0
    private float temp;             // 145.40
    private String tempUnits;       // "F"

    public float getCycle_time() {
        return cycle_time;
    }

    public void setCycle_time(float cycle_time) {
        this.cycle_time = cycle_time;
    }

    public float getDuty_cycle() {
        return duty_cycle;
    }

    public void setDuty_cycle(float duty_cycle) {
        this.duty_cycle = duty_cycle;
    }

    public float getElapsed() {
        return elapsed;
    }

    public void setElapsed(float elapsed) {
        this.elapsed = elapsed;
    }

    public float getD_param() {
        return d_param;
    }

    public void setD_param(float d_param) {
        this.d_param = d_param;
    }

    public float getI_param() {
        return i_param;
    }

    public void setI_param(float i_param) {
        this.i_param = i_param;
    }

    public float getK_param() {
        return k_param;
    }

    public void setK_param(float k_param) {
        this.k_param = k_param;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getMyTempSensorNum() {
        return myTempSensorNum;
    }

    public void setMyTempSensorNum(int myTempSensorNum) {
        this.myTempSensorNum = myTempSensorNum;
    }

    public int getNumTempSensors() {
        return numTempSensors;
    }

    public void setNumTempSensors(int numTempSensors) {
        this.numTempSensors = numTempSensors;
    }

    public float getSet_point() {
        return set_point;
    }

    public void setSet_point(float set_point) {
        this.set_point = set_point;
    }

    public String getTemp() {
        return Float.toString(temp);
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public String getTempUnits() {
        return tempUnits;
    }

    public void setTempUnits(String tempUnits) {
        this.tempUnits = tempUnits;
    }
}
