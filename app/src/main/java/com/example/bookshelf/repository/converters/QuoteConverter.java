package com.example.bookshelf.repository.converters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.bookshelf.repository.objects.Quote;
import com.example.bookshelf.repository.objects.RepositoryObject;

import java.util.Formatter;

public class QuoteConverter implements RepositoryConverter {
    static final String selectRequest = "SELECT * FROM quotes WHERE id = %d;";
    static final String selectRequestWithBookId = "SELECT * FROM quotes WHERE bookId = %d;";
    static final String insertRequest = "INSERT INTO quotes (bookId, content) VALUES (%d, '%s');";
    static final String insertRequestWithId = "INSERT INTO quotes VALUES (%d, %d, '%s');";
    static final String updateRequestWithId = "UPDATE quotes SET bookId = %d, content = '%s' " +
            "where id = %d;";
    static final String deleteRequestWithId = "DELETE FROM quotes WHERE id = %d;";

    @Nullable
    @Override
    public RepositoryObject getObject(SQLiteDatabase database, Integer id) {
        Cursor cursor = database.rawQuery(getSelectRequestString(id), null);

        Quote quote = null;
        if (cursor.moveToFirst())
            quote = fillObject(cursor);

        cursor.close();
        return quote;
    }

    @Nullable
    @Override
    public RepositoryObject getObject(SQLiteDatabase database, RepositoryObject criteria) {
        if (!(criteria instanceof Quote))
            return null;
        Quote castedCriteria = (Quote)criteria;

        Cursor cursor = database.rawQuery(
                getSelectRequestStringWithBookId(castedCriteria), null
        );

        Quote quote = null;
        if (cursor.moveToFirst())
            quote = fillObject(cursor);

        cursor.close();
        return quote;
    }

    @Override
    public void insertExistObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Quote))
            return;

        Quote quote = (Quote)object;
        database.execSQL(getInsertRequestStringWithId(quote));
    }

    @Override
    public void insertNewObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Quote))
            return;

        Quote quote = (Quote)object;
        database.execSQL(getInsertRequestString(quote));
    }

    @Override
    public void updateObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Quote))
            return;

        Quote quote = (Quote)object;
        database.execSQL(getUpdateRequestString(quote));
    }

    @Override
    public void deleteObject(SQLiteDatabase database, Integer id) {
        database.execSQL(getDeleteRequestString(id));
    }

    private String getSelectRequestString(Integer id) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequest, id).toString();
    }

    private String getSelectRequestStringWithBookId(Quote object) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequestWithBookId, object.getBookId()).toString();
    }

    private String getInsertRequestString(Quote object) {
        Formatter formatter = new Formatter();
        return formatter.format(insertRequest, object.getBookId(), object.getContent()).toString();
    }

    private String getInsertRequestStringWithId(Quote object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                insertRequestWithId,
                object.getId(),
                object.getBookId(),
                object.getContent()
        ).toString();
    }

    private String getUpdateRequestString(Quote object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                updateRequestWithId,
                object.getBookId(),
                object.getContent(),
                object.getId()
        ).toString();
    }

    private Quote fillObject(Cursor cursor) {
        int id = cursor.getInt(0);
        int bookId = cursor.getInt(1);
        String content = cursor.getString(2);
        return new Quote(id, bookId, content);
    }

    private String getDeleteRequestString(int id) {
        Formatter formatter = new Formatter();
        return formatter.format(deleteRequestWithId, id).toString();
    }
}
