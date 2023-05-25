package com.example.bookshelf.repository.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class Book implements RepositoryObject {
    private final int id;
    private final int userId;
    private final String name;
    private final String author;
    private final String pdf;
    private int bookmark = 0;
    private Bitmap cover;

    public Book(int id, int userId, String name, String author, String pdf) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.author = author;
        this.pdf = pdf;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void setCover(String cover) {
        try {
            this.cover = decodeToBase64(cover);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookmark() {
        return bookmark;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Bitmap getCover() {
        return cover;
    }

    @Nullable
    public String getStringCover() {
        try {
            return encodeToBase64(cover);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPdf() {
        return pdf;
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
