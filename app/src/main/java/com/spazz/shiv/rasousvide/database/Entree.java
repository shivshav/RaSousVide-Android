package com.spazz.shiv.rasousvide.database;

import android.text.format.Time;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by Shivneil on 2/10/2015.
 * TODO: Add legal stuff
 * TODO: Add javadocs stuff
 */
public class Entree extends SugarRecord<Entree> {

    //TODO:Figure out the structure of this when i get back to it
    private final static Entree INITIAL_DATA[] = {
        new Entree("Chicken"),
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
    private final static Meal INITIAL_MEALS[] = {
            new Meal(null, 146.0),

    }

    private String entreeName;//All entrees should have AT LEAST ONE related meal
    // Entree -> Meal = 1 -> Many relationship

    public Entree(String name) {
        this.entreeName = name;
    }

    //This stays here for compatibility with SugarORM
    public Entree() {

    }
//    public Entree() {
//        this(null, null);
//    }

    public String getEntreeName() {
        return entreeName;
    }

    public void setEntreeName(String entreeName) {
        this.entreeName = entreeName;
    }

    public List<Meal> getMeals() {
        return Meal.find(Meal.class, "entree = ?", this.getId().toString());
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
