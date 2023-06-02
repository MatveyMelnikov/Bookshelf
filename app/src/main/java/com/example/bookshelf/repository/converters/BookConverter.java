package com.example.bookshelf.repository.converters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.bookshelf.repository.objects.Book;
import com.example.bookshelf.repository.objects.RepositoryObject;

import java.util.Formatter;

public class BookConverter implements RepositoryConverter {
    static final String selectRequest = "SELECT * FROM books WHERE id = %d;";
    static final String selectRequestWithName = "SELECT * FROM books WHERE name = %s;";
    static final String insertRequest = "INSERT INTO books (" +
            "userId, name, author, pdf, bookmark, cover) VALUES (" +
            "%d, %s, %s, %s, %d, %s);";
    static final String insertRequestWithId = "INSERT INTO books VALUES (" +
            "%d, %d, %s, %s, %s, %d, %s);";
    static final String updateRequestWithId = "UPDATE books SET " +
            "userId = %d, name = %s, author = %s, pdf = %s, bookmark = %d, " +
            "cover = %s where id = %d;";
    static final String deleteRequestWithId = "DELETE FROM books WHERE id = %d;";

    @Nullable
    @Override
    public RepositoryObject getObject(SQLiteDatabase database, Integer id) {
        Cursor cursor = database.rawQuery(getSelectRequestString(id), null);

        Book book = null;
        if (cursor.moveToFirst()) {
            book = fillObject(cursor);
        }

        cursor.close();
        return book;
    }

    @Nullable
    @Override
    public RepositoryObject getObject(SQLiteDatabase database, RepositoryObject criteria) {
        if (!(criteria instanceof Book))
            return null;
        Book castedCriteria = (Book)criteria;

        Cursor cursor = database.rawQuery(
                getSelectRequestStringWithName(castedCriteria), null
        );

        Book book = null;
        if (cursor.moveToFirst()) {
            book = fillObject(cursor);
        }

        cursor.close();
        return book;
    }

    @Override
    public void insertExistObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Book))
            return;

        Book book = (Book)object;
        database.execSQL(getInsertRequestStringWithId(book));
    }

    @Override
    public void insertNewObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Book))
            return;

        Book user = (Book)object;
        database.execSQL(getInsertRequestString(user));
    }

    @Override
    public void updateObject(SQLiteDatabase database, RepositoryObject object) {
        if (!(object instanceof Book))
            return;

        Book book = (Book)object;
        database.execSQL(getUpdateRequestString(book));
    }

    @Override
    public void deleteObject(SQLiteDatabase database, Integer id) {
        database.execSQL(getDeleteRequestString(id));
    }

    private String getSelectRequestString(Integer id) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequest, id).toString();
    }

    private String getSelectRequestStringWithName(Book object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                selectRequestWithName,
                prepareStringField(object.getName())
        ).toString();
    }

    private String getInsertRequestString(Book object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                insertRequest,
                object.getUserId(),
                prepareStringField(object.getName()),
                prepareStringField(object.getAuthor()),
                prepareStringField(object.getPdf()),
                object.getBookmark(),
                prepareStringField(object.getStringCover())
        ).toString();
    }

    private String getInsertRequestStringWithId(Book object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                insertRequestWithId,
                object.getId(),
                object.getUserId(),
                prepareStringField(object.getName()),
                prepareStringField(object.getAuthor()),
                prepareStringField(object.getPdf()),
                object.getBookmark(),
                prepareStringField(object.getStringCover())
        ).toString();
    }

    private String getUpdateRequestString(Book object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                updateRequestWithId,
                object.getUserId(),
                prepareStringField(object.getName()),
                prepareStringField(object.getAuthor()),
                prepareStringField(object.getPdf()),
                object.getBookmark(),
                prepareStringField(object.getStringCover()),
                object.getId()
        ).toString();
    }

    private String getDeleteRequestString(int id) {
        Formatter formatter = new Formatter();
        return formatter.format(deleteRequestWithId, id).toString();
    }

    private Book fillObject(Cursor cursor) {
        int id = cursor.getInt(0);
        int userId = cursor.getInt(1);
        String name = cursor.getString(2);
        String author = cursor.getString(3);
        String pdf = cursor.getString(4);
        int bookmark = cursor.getInt(5);
        String cover = cursor.getString(6);

        Book book = new Book(id, userId, name, author, pdf);
        book.setBookmark(bookmark);
        book.setCover(cover);

        return book;
    }

    private String prepareStringField(@Nullable String field) {
        if (field == null)
            return "NULL";
        else
            return "'" + field + "'";
    }
}
