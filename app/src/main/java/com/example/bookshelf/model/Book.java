package com.example.bookshelf.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Book implements Serializable {
    public int id;
    public String name;
    public String author;

    public Book(int id, String name, String author) {
        this.id = id;
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
