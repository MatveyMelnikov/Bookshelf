package com.example.bookshelf.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.bookshelf.repository.converters.RepositoryConverter;
import com.example.bookshelf.repository.objects.RepositoryObject;

import java.lang.ref.WeakReference;

public class Repository {
    static SQLiteDatabase database;
    static WeakReference<Context> contextRef;
    static final String createUsersTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE, hash TEXT NOT NULL)";
    static final String createBooksTableQuery =
            "CREATE TABLE IF NOT EXISTS books\n" +
            "(\n" +
            "id INTEGER PRIMARY KEY,\n" +
            "userId INTEGER NOT NULL,\n" +
            "name TEXT NOT NULL,\n" +
            "author TEXT NOT NULL,\n" +
            "pdf TEXT NOT NULL,\n" +
            "bookmark INTEGER,\n" +
            "quoteId INTEGER,\n" +
            "cover TEXT NOT NULL\n" +
            ");";
    private Repository() {}

    public static void init(Context context) {
        contextRef = new WeakReference<>(context);
        database = context.openOrCreateDatabase(
                "app.db",
                Context.MODE_PRIVATE,
                null
        );

        database.execSQL(createUsersTableQuery);
        database.execSQL(createBooksTableQuery);
    }

    @Nullable
    public static RepositoryObject selectObject(Integer id, RepositoryConverter converter) {
        if (database == null)
            return null;

        return converter.getObject(database, id);
    }

    @Nullable
    public static RepositoryObject selectObject(RepositoryObject criterion, RepositoryConverter converter) {
        if (database == null)
            return null;

        return converter.getObject(database, criterion);
    }

    public static void insertNewObject(RepositoryObject object, RepositoryConverter converter) {
        if (database == null)
            return;

        converter.insertNewObject(database, object);
    }

    public static void insertExistObject(
            RepositoryObject object,
            RepositoryConverter converter
    ) {
        if (database == null)
            return;

        converter.insertExistObject(database, object);
    }

    public static void close() {
        database.close();
    }
}
