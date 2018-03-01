package com.example.kitstasher.objects;

/**
 * Created by Алексей on 07.07.2017. For AlertDialogs
 */

public class Item {
    private String   itemTitle;
    private String   itemBoxartUrl;

    public Item(String boxart, String title) {
        this.itemBoxartUrl = boxart;
        this.itemTitle = title;
    }
    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemBoxartUrl() {
        return itemBoxartUrl;
    }
}
