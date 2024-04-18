package com.ofywellness.modals;

// Meal stores data of the Meal has getters and constructors for Database use
public class Meal {
    private String Image;
    private String Name;
    private int Energy;
    private int Proteins;
    private int Fats;
    private int Carbohydrates;

    public String getImage() {
        return Image;
    }

    public String getName() {
        return Name;
    }

    public int getEnergy() {
        return Energy;
    }

    public int getProteins() {
        return Proteins;
    }

    public int getFats() {
        return Fats;
    }

    public int getCarbohydrates() {
        return Carbohydrates;
    }

    public Meal(String image, String name, int energy, int proteins, int fats, int carbohydrates) {
        Image = image;
        Name = name;
        Energy = energy;
        Proteins = proteins;
        Fats = fats;
        Carbohydrates = carbohydrates;
    }
}
