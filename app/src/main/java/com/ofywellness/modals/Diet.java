package com.ofywellness.modals;

//Diet class is used to store meals of a particular day with day number
public class Diet {
    private int Day;
    private Meal Breakfast;
    private Meal Lunch;
    private Meal Dinner;

    public Diet(int day, Meal breakfast, Meal lunch, Meal dinner) {
        Day = day;
        Breakfast = breakfast;
        Lunch = lunch;
        Dinner = dinner;
    }

    public int getDay() {
        return Day;
    }

    public Meal getBreakfast() {
        return Breakfast;
    }

    public Meal getLunch() {
        return Lunch;
    }

    public Meal getDinner() {
        return Dinner;
    }
}
