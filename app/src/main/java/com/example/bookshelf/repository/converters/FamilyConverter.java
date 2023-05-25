package com.example.bookshelf.repository.converters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.bookshelf.repository.objects.Family;
import com.example.bookshelf.repository.objects.RepositoryObject;

import java.util.Formatter;

public class FamilyConverter implements RepositoryConverter {
    static final String selectRequest = "SELECT * FROM families WHERE id = %d;";
    static final String selectRequestWithCreatorId = "SELECT * FROM families WHERE creatorId = %d;";
    static final String insertRequest = "INSERT INTO families (creatorId) VALUES (%d);";
    static final String insertRequestWithId = "INSERT INTO families VALUES (%d, %d);";
    static final String updateRequestWithId = "UPDATE families SET creatorId = %d " +
            "where id = %d;";
    static final String deleteRequestWithId = "DELETE FROM families WHERE id = %d;";
    @Nullable
    @Override
    public RepositoryObject getObject(SQLiteDatabase database, Integer id) {
        Cursor cursor = database.rawQuery(getSelectRequestString(id), null);

        Family family = null;
        if (cursor.moveToFirst()) {
            int creatorId = cursor.getInt(1);
            family = new Family(id, creatorId);
        }

        cursor.close();
        return family;
    }

    @Nullable
    @Override
    public RepositoryObject getObject(SQLiteDatabase database, RepositoryObject criteria) {
        if (!(criteria instanceof Family))
            return null;
        Family castedCriteria = (Family)criteria;

        Cursor cursor = database.rawQuery(
                getSelectRequestStringWithCreatorId(castedCriteria), null
        );

        Family family = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int creatorId = cursor.getInt(1);
            family = new Family(id, creatorId);
        }

        cursor.close();
        return family;
    }

    @Override
    public void insertExistObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Family))
            return;

        Family family = (Family)object;
        database.execSQL(getInsertRequestStringWithId(family));
    }

    @Override
    public void insertNewObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Family))
            return;

        Family family = (Family)object;
        database.execSQL(getInsertRequestString(family));
    }

    @Override
    public void updateObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Family))
            return;

        Family family = (Family)object;
        database.execSQL(getUpdateRequestString(family));
    }

    @Override
    public void deleteObject(SQLiteDatabase database, Integer id) {
        database.execSQL(getDeleteRequestString(id));
    }

    private String getSelectRequestString(Integer id) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequest, id).toString();
    }

    private String getSelectRequestStringWithCreatorId(Family object) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequestWithCreatorId, object.getCreatorId()).toString();
    }

    private String getInsertRequestString(Family object) {
        Formatter formatter = new Formatter();
        return formatter.format(insertRequest, object.getCreatorId()).toString();
    }

    private String getInsertRequestStringWithId(Family object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                insertRequestWithId,
                object.getId(),
                object.getCreatorId()
        ).toString();
    }

    private String getUpdateRequestString(Family object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                updateRequestWithId,
                object.getCreatorId(),
                object.getId()
        ).toString();
    }

    private String getDeleteRequestString(int id) {
        Formatter formatter = new Formatter();
        return formatter.format(deleteRequestWithId, id).toString();
    }
}
