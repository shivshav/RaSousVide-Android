package com.spazz.shiv.rasousvide.database;

import android.text.format.Time;

import com.orm.SugarRecord;

/**
 * Created by Shivneil on 2/10/2015.
 * TODO: Add legal stuff
 * TODO: Add javadocs stuff
 */
public class Entree extends SugarRecord<Entree> {

    private final static Entree INITIAL_DATA[] = {
        new Entree("Chicken",new Meal[] {
            new Meal(null, 146.0)
        }),
        new Entree("Steak", new Meal[] {
            new Meal("Medium-Rare", 134.0),
            new Meal("Medium", 140.0),
            new Meal("Medium-Well", 150.0)
        }),
        new Entree("Eggs", new Meal[] {
            new Meal("Soft Cooked", 146, (long)(Time.HOUR*.75)),
            new Meal("Hard Cooked", 160, (long)(Time.HOUR*.75))
        })

    };


    private String entreeName;
    private Meal[] meals;//All entrees should have AT LEAST ONE related meal
    // Entree -> Meal = 1 -> Many relationship

    public Entree(String name, Meal[] meals) {
        this.entreeName = name;
        this.meals = meals;
    }

    //This stays here for compatibility with SugarORM
    public Entree() {
        this(null, null);
    }

    public String getEntreeName() {
        return entreeName;
    }

    public void setEntreeName(String entreeName) {
        this.entreeName = entreeName;
    }

    public Meal[] getMeals() {
        return meals;
    }

    public void setMeals(Meal[] meals) {
        this.meals = meals;
    }

    public static void firstTimeMealSetup() {
        for(int i = 0; i < Entree.INITIAL_DATA.length; i++) {
            Meal[] tmp = Entree.INITIAL_DATA[i].getMeals();
            for(int j = 0; j < tmp.length; j++) {
                tmp[j].save();
            }
            INITIAL_DATA[i].save();
        }
    }
}
