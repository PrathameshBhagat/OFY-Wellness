package com.ofywellness.modals;


import java.util.List;

public class User {
    private String Email;
    private String FirstName;
    private String LastName;
    private String Phone;
    private String Gender;
    private int Age;
    private int Weight;
    private float Height;

    private List<Diet> DietRecord;

    public String getEmail() {
        return Email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getPhone() {
        return Phone;
    }

    public int getAge() {
        return Age;
    }

    public int getWeight() {
        return Weight;
    }

    public float getHeight() {
        return Height;
    }

    public String getGender() {
        return Gender;
    }

    public User(String email, String firstName, String lastName, String phone, String gender, int age, int weight, float height) {
        Email = email;
        FirstName = firstName;
        LastName = lastName;
        Phone = phone;
        Gender = gender;
        Age = age;
        Weight = weight;
        Height = height;
    }

}
