package com.example.bookshelf.repository.objects;

public class Quote implements RepositoryObject {
    private final int id;
    private final int bookId;
    private final String content;

    public Quote(int id, int bookId, String content) {
        this.id = id;
        this.bookId = bookId;
        this.content = content;
    }

    public int getBookId() {
        return bookId;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int getId() {
        return id;
    }
}
