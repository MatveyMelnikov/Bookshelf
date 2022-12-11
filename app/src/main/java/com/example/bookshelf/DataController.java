package com.example.bookshelf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class DataController {
    public static final String CARDS_STORAGE_NAME = "cards";
    public static final String BOOKS_STORAGE_NAME = "books";
    public static final String BOOKS_KEYS = "booksKeys";
    public static SharedPreferences cardsSettings;
    public static SharedPreferences booksSettings;
    public static ArrayList<Book> books = new ArrayList<>();

    public static void Init(Context context) {
        cardsSettings = context.getSharedPreferences(CARDS_STORAGE_NAME, Context.MODE_PRIVATE);
        booksSettings = context.getSharedPreferences(BOOKS_STORAGE_NAME, Context.MODE_PRIVATE);

        loadBooks();
    }

    public static void loadBooks() {
        if (!areFieldsInitialized())
            return;

        try {
            String data = booksSettings.getString(BOOKS_KEYS, "");

            books = getBooksArrayFromJSON(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap loadBookCard(String key) {
        return DataController.getBitmap(key);
    }

    public static void swapBooks(int oldIndex, int newIndex) {
        Collections.swap(books, oldIndex, newIndex);
    }


    public static void putBook(Book book) {
        if (!areFieldsInitialized())
            return;

        if (findBook(book.name + book.author) != null)
            return;

        books.add(book);

        saveBooks();
    }

    public static void deleteBook(String key) {
        Integer found = findBook(key);
        if (found == null)
            return;

        SharedPreferences.Editor cardsEditor = cardsSettings.edit();
        cardsEditor.remove(key); // remove card image
        cardsEditor.apply();

        books.remove(found.intValue());
        saveBooks();
    }

    public static JSONArray getBooksJSON(ArrayList<Book> data) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < data.size(); i++)
            jsonArray.put(data.get(i).convertToJson());

        return jsonArray;
    }

    public static ArrayList<Book> getBooksArrayFromJSON(String data) {
        ArrayList<Book> result = new ArrayList<>();

        try {
            JSONArray jsonArray = null;
            jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++)
                result.add(new Book(jsonArray.getJSONObject(i)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @SuppressLint("CommitPrefEdits")
    public static void putBitmap(String key, Bitmap bitmap) {
        if (!areFieldsInitialized())
            return;

        try {
            SharedPreferences.Editor editor = cardsSettings.edit();
            editor.putString(key, encodeToBase64(bitmap));
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap(String key) {
        if (!areFieldsInitialized())
            return null;

        try {
            String data = cardsSettings.getString(key, "");
            if (data.isEmpty())
                return null;

            return decodeToBase64(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveBooks() {
        SharedPreferences.Editor booksEditor = booksSettings.edit();
        booksEditor.putString(BOOKS_KEYS, getBooksJSON(books).toString());
        booksEditor.apply();
    }

    private static Integer findBook(String key) {
        for (int i = 0; i < books.size(); i++) {
            Book current = books.get(i);
            if ((current.name + current.author).equals(key)) {
                return i;
            }
        }
        return null;
    }

    private static Boolean areFieldsInitialized() {
        return (cardsSettings != null && booksSettings != null);
    }

    private static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] b = stream.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
