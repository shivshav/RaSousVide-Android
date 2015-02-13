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
    private final static Entree INITIAL_ENTREES[] = {
        new Entree("Chicken"),
        new Entree("Steak"),
        new Entree("Eggs")

    };
    private final static Meal INITIAL_MEALS[] = {
        new Meal(INITIAL_ENTREES[0], null, 146.0),
        new Meal(INITIAL_ENTREES[1], "Medium-Rare", 134.0),
        new Meal(INITIAL_ENTREES[1], "Medium", 140.0),
        new Meal(INITIAL_ENTREES[1], "Medium-Well", 150.0),
        new Meal(INITIAL_ENTREES[2], "Soft Cooked", 146, (long)(Meal.HOUR*.75)),
        new Meal(INITIAL_ENTREES[2], "Hard Cooked", 160, (long)(Meal.HOUR*.75))

    };

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
        for (int i = 0; i < INITIAL_ENTREES.length; i++) {
            INITIAL_ENTREES[i].save();
        }
        for(int i = 0; i < Entree.INITIAL_MEALS.length; i++) {
            INITIAL_MEALS[i].save();
        }
    }
}
