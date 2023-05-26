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
    static final String insertRequest = "INSERT INTO users " +
            "(name, hash, isChild, familyId) " +
            "VALUES ('%s', '%s', %d, %d);";
    static final String insertRequestWithId = "INSERT INTO users " +
            "VALUES (%d, '%s', '%s', %d, %d);";
    static final String updateRequestWithId = "UPDATE users SET " +
            "name = '%s', " +
            "hash = '%s', " +
            "isChild = %d, " +
            "familyId = %d " +
            "where id = %d;";
    static final String deleteRequestWithId = "DELETE FROM users WHERE id = %d;";
    @Override
    @Nullable
    public RepositoryObject getObject(SQLiteDatabase database, Integer id) {
        Cursor cursor = database.rawQuery(getSelectRequestString(id), null);

        User user = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String hash = cursor.getString(2);
            Boolean isChild = cursor.getInt(3) != 0;
            int familyId = cursor.getInt(4);
            user = new User(id, name, hash, isChild, familyId);
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
            Boolean isChild = cursor.getInt(3) != 0;
            int familyId = cursor.getInt(4);
            user = new User(id, name, hash, isChild, familyId);
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

    @Override
    public void updateObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof User))
            return;

        User user = (User)object;

        database.execSQL(getUpdateRequestString(user));
    }

    @Override
    public void deleteObject(SQLiteDatabase database, Integer id) {
        database.execSQL(getDeleteRequestString(id));
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
        return formatter.format(
                insertRequest,
                object.getName(),
                object.getHash(),
                object.isChild() ? 1 : 0,
                object.getFamilyId()
        ).toString();
    }

    private String getInsertRequestStringWithId(User object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                insertRequestWithId,
                object.getId(),
                object.getName(),
                object.getHash(),
                object.isChild() ? 1 : 0,
                object.getFamilyId()
        ).toString();
    }

    private String getUpdateRequestString(User object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                updateRequestWithId,
                object.getName(),
                object.getHash(),
                object.isChild() ? 1 : 0,
                object.getFamilyId(),
                object.getId()
        ).toString();
    }

    private String getDeleteRequestString(int id) {
        Formatter formatter = new Formatter();
        return formatter.format(deleteRequestWithId, id).toString();
    }
}
