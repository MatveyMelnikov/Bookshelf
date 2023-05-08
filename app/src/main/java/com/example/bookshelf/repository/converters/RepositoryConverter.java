package com.example.bookshelf.repository.converters;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.bookshelf.repository.objects.RepositoryObject;

public interface RepositoryConverter {
    @Nullable
    RepositoryObject getObject(SQLiteDatabase database, Integer id);

    @Nullable
    RepositoryObject getObject(SQLiteDatabase database, RepositoryObject criteria);

    void insertExistObject(SQLiteDatabase database, RepositoryObject object);
    void insertNewObject(SQLiteDatabase database, RepositoryObject object);
}
