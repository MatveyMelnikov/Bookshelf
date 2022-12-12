package com.example.bookshelf;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Book {
    public String name;
    public String author;

    public Book(String name, String author) {
        this.name = name;
        this.author = author;
    }

    public Book(JSONObject jsonObject) {
        recoverFromJSON(jsonObject);
    }

    public JSONObject convertToJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("author", author);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void recoverFromJSON(JSONObject jsonObject) {
        try {
            name = jsonObject.getString("name");
            author = jsonObject.getString("author");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getKey() {
        return name + author;
    }
}
