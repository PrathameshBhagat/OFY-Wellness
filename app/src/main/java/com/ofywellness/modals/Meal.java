package com.ofywellness.modals;

import java.io.File;

// Meal stores data of the Meal
public class Meal {
    private File Image;
    private int Energy;
    private int Proteins;
    private int Fats;
    private int Carbohydrates;

    public File getImage() {
        return Image;
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

    public Meal(File image, int energy, int proteins, int fats, int carbohydrates) {
        Image = image;
        Energy = energy;
        Proteins = proteins;
        Fats = fats;
        Carbohydrates = carbohydrates;
    }
}
