package com.example.bookshelf.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.repository.converters.RepositoryConverter;
import com.example.bookshelf.repository.objects.Book;
import com.example.bookshelf.repository.objects.Quote;
import com.example.bookshelf.repository.objects.RepositoryObject;
import com.example.bookshelf.repository.objects.User;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Formatter;

public class Repository {
    //public static int currentBookId = 0;
    static SQLiteDatabase database;
    static WeakReference<Context> contextRef;
    static final String createUsersTableQuery =
            "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL UNIQUE, " +
                "hash TEXT NOT NULL," +
                "isChild INTEGER NOT NULL DEFAULT 0," +
                "familyId INTEGER" +
            ");";
    static final String createBooksTableQuery =
            "CREATE TABLE IF NOT EXISTS books\n" +
            "(\n" +
            "id INTEGER PRIMARY KEY,\n" +
            "userId INTEGER NOT NULL,\n" +
            "name TEXT NOT NULL,\n" +
            "author TEXT NOT NULL,\n" +
            "pdf TEXT NOT NULL,\n" +
            "bookmark INTEGER,\n" +
            "cover TEXT\n" +
            ");";
    static final String createQuotesTableQuery =
            "CREATE TABLE IF NOT EXISTS quotes\n" +
                    "(\n" +
                    "id INTEGER PRIMARY KEY,\n" +
                    "bookId INTEGER NOT NULL,\n" +
                    "content TEXT NOT NULL\n" +
                    ");";
    static final String createFamilyTableQuery =
            "CREATE TABLE IF NOT EXISTS families\n" +
                    "(\n" +
                    "id INTEGER PRIMARY KEY,\n" +
                    "creatorId INTEGER NOT NULL\n" +
                    ");";
    static final String getAllBooksForUser = "SELECT * FROM books WHERE userId = %d;";
    static final String getQuotesOfBook = "SELECT * FROM quotes WHERE bookId = %d;";
    static final String getAllBooksIdForUser = "SELECT id FROM books WHERE userId = %d;";
    static final String getAllUsersInFamily = "SELECT * FROM users WHERE familyId = %d;";

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
        database.execSQL(createQuotesTableQuery);
        database.execSQL(createFamilyTableQuery);
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

    public static void updateObject(RepositoryObject object, RepositoryConverter converter) {
        if (database == null)
            return;

        converter.updateObject(database, object);
    }

    public static void deleteObject(int id, RepositoryConverter converter) {
        if (database == null)
            return;

        converter.deleteObject(database, id);
    }

    public static ArrayList<Book> getArrayOfBookModels(int userId) {
        Formatter formatter = new Formatter();
        Cursor cursor = database.rawQuery(
                formatter.format(getAllBooksForUser, userId).toString(),
                null
        );

        ArrayList<Book> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(2);
            String author = cursor.getString(3);
            String pdf = cursor.getString(4);
            int bookmark = cursor.getInt(5);
            String cover = cursor.getString(6);

            Book book = new Book(id, userId, name, author, pdf);
            book.setBookmark(bookmark);
            book.setCover(cover);

            result.add(book);
        }

        cursor.close();
        return result;
    }

    public static ArrayList<Quote> getArrayOfQuotes(int bookId) {
        Formatter formatter = new Formatter();
        Cursor cursor = database.rawQuery(
                formatter.format(getQuotesOfBook, bookId).toString(),
                null
        );

        ArrayList<Quote> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(
                    new Quote(cursor.getInt(0), cursor.getInt(1), cursor.getString(2))
            );
        }

        cursor.close();
        return result;
    }

    public static ArrayList<Quote> getArrayOfAllUserQuotes() {
        Cursor cursor = database.rawQuery(
                new Formatter().format(
                        getAllBooksIdForUser, EntryController.getLoggedUser().getId()
                ).toString(),
                null
        );

        ArrayList<Integer> booksId = new ArrayList<>();
        while (cursor.moveToNext()) {
            booksId.add(cursor.getInt(0));
        }

        cursor.close();

        ArrayList<Quote> result = new ArrayList<>();
        for (Integer bookId : booksId) {
            Cursor quoteCursor = database.rawQuery(
                    new Formatter().format(getQuotesOfBook, bookId).toString(),
                    null
            );

            while (quoteCursor.moveToNext()) {
                result.add(
                        new Quote(
                                quoteCursor.getInt(0),
                                quoteCursor.getInt(1),
                                quoteCursor.getString(2)
                        )
                );
            }
            quoteCursor.close();
        }

        return result;
    }

    public static ArrayList<User> getArrayOfAllFamilyMembers() {
        ArrayList<User> result = new ArrayList<>();
        Integer familyId = EntryController.getLoggedUser().getFamilyId();
        if (familyId == 0)
            return result;

        Cursor cursor = database.rawQuery(
                new Formatter().format(
                        getAllUsersInFamily, EntryController.getLoggedUser().getFamilyId()
                ).toString(),
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String hash = cursor.getString(2);
            Boolean isChild = cursor.getInt(3) != 0;

            result.add(new User(id, name, hash, isChild, familyId));
        }
        cursor.close();

        return result;
    }

    public static void close() {
        database.close();
    }
}
