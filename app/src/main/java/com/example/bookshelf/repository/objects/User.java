package com.example.bookshelf.repository.objects;

public class User implements RepositoryObject {
    private final int id;
    private final String name;
    private final String hash;

    public User(int id, String name, String hash) {
        this.id = id;
        this.name = name;
        this.hash = hash;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }
}
