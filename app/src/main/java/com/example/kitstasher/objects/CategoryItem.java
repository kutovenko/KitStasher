package com.example.kitstasher.objects;

public class CategoryItem {
    private int logoResource;
    private String name;
    private int quantity;

    public CategoryItem(){
//        this.logoResource = logoResource;
//        this.name = name;
//        this.quantity = quantity;
    }

    public int getLogoResource() {
        return logoResource;
    }

    public void setLogoResource(int logoResource) {
        this.logoResource = logoResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
