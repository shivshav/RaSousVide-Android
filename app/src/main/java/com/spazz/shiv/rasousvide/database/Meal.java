package com.spazz.shiv.rasousvide.database;

import android.text.format.Time;

import com.orm.SugarRecord;

/**
 * Created by Shivneil on 2/9/2015.
 * TODO: Add legal stuff
 * TODO: Add javadocs stuff
 */
public class Meal extends SugarRecord {
    public static final long HOUR = 60*60*1;

    Entree entree;
    String mealType;//This is the meal type (if NULL, then there's only one)
    double setPoint;//in Celsius (NOT NULL)
    long cookTime;
    double kParam;//This is the ideal K Parameter for this meal (can be NULL)
    double dParam;//This is the ideal D Parameter for this meal (can be NULL)
    double iParam;//This is the ideal I Parameter for this meal (can be NULL)

    public Meal() {

    }

    public Meal(Entree entree, String mealType, Double setPoint, Long cookTime, Double kParam, Double dParam, Double iParam) {
        this.entree = entree;
        this.mealType = mealType;
        this.setPoint = (setPoint == null? -1: setPoint);
        this.cookTime = (cookTime == null? HOUR: cookTime);
        this.kParam = (kParam == null? 44: kParam);
        this.iParam = (iParam == null? 165: iParam);
        this.dParam = (dParam == null? 4: dParam);
    }

    public Meal(Entree entree, String name, double setPoint) {
        this(entree, name, setPoint, null, null, null, null);
    }

    public Meal(Entree entree, String mealType, double setPoint, long cookTime) {
        this(entree, mealType, setPoint, cookTime, null, null, null);
    }

    public Entree getEntree() {return entree;}

    public void setEntree(Entree e) {this.entree = e;}

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public double getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(double setPoint) {
        this.setPoint = setPoint;
    }

    public long getCookTime() {
        return cookTime;
    }

    public void setCookTime(long cookTime) {
        this.cookTime = cookTime;
    }

    public double getkParam() {
        return kParam;
    }

    public void setkParam(double kParam) {
        this.kParam = kParam;
    }

    public double getdParam() {
        return dParam;
    }

    public void setdParam(double dParam) {
        this.dParam = dParam;
    }

    public double getiParam() {
        return iParam;
    }

    public void setiParam(double iParam) {
        this.iParam = iParam;
    }

    @Override
    public String toString(){
        return this.getMealType();
    }
}
