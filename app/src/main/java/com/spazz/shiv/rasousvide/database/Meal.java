package com.spazz.shiv.rasousvide.database;

import com.orm.SugarRecord;

import java.util.DuplicateFormatFlagsException;
import java.util.List;

/**
 * Created by Shivneil on 2/9/2015.
 * TODO: Add legal stuff
 * TODO: Add javadocs stuff
 */
public class Meal extends SugarRecord<Meal> {

    private static final Meal INITIAL_DATA[] = {
        new Meal("Chicken", 146.0),
        new Meal("Steak", "Medium-Rare", 134.0),
        new Meal("Eggs", "Hard-Boiled", 150.0)
    };

    String name;//This is the entree's name
    String subType;//This is the meal subtype (can be NULL)
    Double setPoint;//in Celsius (NOT NULL)
    Double cookTime;
    Double kParam;//This is the ideal K Parameter for this meal (can be NULL)
    Double dParam;//This is the ideal D Parameter for this meal (can be NULL)
    Double iParam;//This is the ideal I Parameter for this meal (can be NULL)

    public Meal() {

    }

    public Meal(String name, String subType, Double setPoint, Double kParam, Double dParam, Double iParam) {
        this.name = name;
        this.subType = subType;
        this.setPoint = setPoint;
        this.kParam = kParam;
        this.iParam = iParam;
        this.dParam = dParam;
    }

    public Meal(String name, Double setPoint) {
        this(name, null, setPoint, null, null, null);
    }

    public Meal(String name, String subType, Double setPoint) {
        this(name, subType, setPoint, null, null, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Double getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(Double setPoint) {
        this.setPoint = setPoint;
    }

    public Double getCookTime() {
        return cookTime;
    }

    public void setCookTime(Double cookTime) {
        this.cookTime = cookTime;
    }

    public Double getkParam() {
        return kParam;
    }

    public void setkParam(Double kParam) {
        this.kParam = kParam;
    }

    public Double getdParam() {
        return dParam;
    }

    public void setdParam(Double dParam) {
        this.dParam = dParam;
    }

    public Double getiParam() {
        return iParam;
    }

    public void setiParam(Double iParam) {
        this.iParam = iParam;
    }

    public static void firstTimeMealSetup() {
        for(int i = 0; i < Meal.INITIAL_DATA.length; i++) {
            Meal.INITIAL_DATA[i].save();
        }
    }

}
