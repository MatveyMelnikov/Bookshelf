package com.example.bookshelf.repository.converters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.bookshelf.repository.objects.RepositoryObject;
import com.example.bookshelf.repository.objects.User;

import java.util.Formatter;

public class UserConverter implements RepositoryConverter {
    static final String selectRequest = "SELECT * FROM users WHERE id = %d;";
    static final String selectRequestWithName = "SELECT * FROM users WHERE name = '%s';";
    static final String insertRequest = "INSERT INTO users (name, hash) VALUES ('%s', '%s');";
    static final String insertRequestWithId = "INSERT INTO users VALUES (%d, '%s', '%s');";
    @Override
    @Nullable
    public RepositoryObject getObject(SQLiteDatabase database, Integer id) {
        Cursor cursor = database.rawQuery(getSelectRequestString(id), null);

        User user = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String hash = cursor.getString(2);
            user = new User(id, name, hash);
        }

        cursor.close();
        return user;
    }

    @Nullable
    @Override
    public RepositoryObject getObject(SQLiteDatabase database, RepositoryObject criteria) {
        if (!(criteria instanceof User))
            return null;
        User castedCriteria = (User)criteria;

        Cursor cursor = database.rawQuery(
                getSelectRequestStringWithName(castedCriteria), null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String hash = cursor.getString(2);
            user = new User(id, name, hash);
        }

        cursor.close();
        return user;
    }

    @Override
    public void insertExistObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof User))
            return;

        User user = (User)object;
        database.execSQL(getInsertRequestStringWithId(user));
    }

    @Override
    public void insertNewObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof User))
            return;

        User user = (User)object;
        database.execSQL(getInsertRequestString(user));
    }

    private String getSelectRequestString(Integer id) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequest, id).toString();
    }

    private String getSelectRequestStringWithName(User object) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequestWithName, object.getName()).toString();
    }

    private String getInsertRequestString(User object) {
        Formatter formatter = new Formatter();
        return formatter.format(insertRequest, object.getName(), object.getHash()).toString();
    }

    private String getInsertRequestStringWithId(User object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                insertRequestWithId,
                object.getId(),
                object.getName(),
                object.getHash()
        ).toString();
    }
}
