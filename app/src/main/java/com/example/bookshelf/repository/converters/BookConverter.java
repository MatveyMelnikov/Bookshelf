package com.example.bookshelf.repository.converters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.bookshelf.repository.objects.Book;
import com.example.bookshelf.repository.objects.RepositoryObject;

import java.util.Formatter;

public class BookConverter implements RepositoryConverter {
    static final String selectRequest = "SELECT * FROM books WHERE id = %d;";
    static final String selectRequestWithName = "SELECT * FROM books WHERE name = '%s';";
    static final String insertRequest = "INSERT INTO books (" +
            "userId, name, author, pdf, bookmark, quoteId, cover) VALUES (" +
            "%d, '%s', '%s', '%s', %d, %d, '%s');";
    static final String insertRequestWithId = "INSERT INTO books VALUES (" +
            "%d, %d, '%s', '%s', '%s', %d, %d, '%s');";

    @Nullable
    @Override
    public RepositoryObject getObject(SQLiteDatabase database, Integer id) {
        Cursor cursor = database.rawQuery(getSelectRequestString(id), null);

        Book book = null;
        if (cursor.moveToFirst()) {
//            int userId = cursor.getInt(1);
//            String name = cursor.getString(2);
//            String author = cursor.getString(3);
//            String pdf = cursor.getString(4);
//            int bookmark = cursor.getInt(5);
//            int quoteId = cursor.getInt(6);
//            String cover = cursor.getString(7);
//
//            book = new Book(id, userId, name, author, pdf);
//            book.setBookmark(bookmark);
//            book.setQuoteId(quoteId);
//            book.setCover(cover);
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

    private String getSelectRequestString(Integer id) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequest, id).toString();
    }

    private String getSelectRequestStringWithName(Book object) {
        Formatter formatter = new Formatter();
        return formatter.format(selectRequestWithName, object.getName()).toString();
    }

    private String getInsertRequestString(Book object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                insertRequest,
                object.getUserId(),
                object.getName(),
                object.getAuthor(),
                object.getPdf(),
                object.getBookmark(),
                object.getQuoteId(),
                object.getStringCover()
        ).toString();
    }

    private String getInsertRequestStringWithId(Book object) {
        Formatter formatter = new Formatter();
        return formatter.format(
                insertRequestWithId,
                object.getId(),
                object.getUserId(),
                object.getName(),
                object.getAuthor(),
                object.getPdf(),
                object.getBookmark(),
                object.getQuoteId(),
                object.getStringCover()
        ).toString();
    }

    private Book fillObject(Cursor cursor) {
        int id = cursor.getInt(0);
        int userId = cursor.getInt(1);
        String name = cursor.getString(2);
        String author = cursor.getString(3);
        String pdf = cursor.getString(4);
        int bookmark = cursor.getInt(5);
        int quoteId = cursor.getInt(6);
        String cover = cursor.getString(7);

        Book book = new Book(id, userId, name, author, pdf);
        book.setBookmark(bookmark);
        book.setQuoteId(quoteId);
        book.setCover(cover);

        return book;
    }
}
