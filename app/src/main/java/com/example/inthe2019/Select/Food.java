package com.example.inthe2019.Select;

public class Food {
    int recipecode;
    String name;
    String countrycode;
    String categorycode;
    String time;
    int kcal;
    String amount;
    String img;
    int weight;

    public Food(int recipecode, String name, String countrycode, String categorycode,
                String time, int kcal, String amount, String img, int weight) {
        this.recipecode = recipecode;
        this.name = name;
        this.countrycode = countrycode;
        this.categorycode = categorycode;
        this.time = time;
        this.kcal = kcal;
        this.amount = amount;
        this.img = img;
        this.weight = weight;
    }

    public int getRecipecode() { return recipecode; }

    public String getName() {
        return name;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public String getCategorycode() {
        return categorycode;
    }

    public String getTime() { return time; }

    public int getKcal() {
        return kcal;
    }

    public String getAmount() {
        return amount;
    }

    public String getImg() {
        return img;
    }

    public int getWeight() { return weight; }
}