package com.example.bookshelf.repository.objects;

public class Family implements RepositoryObject {
    private final int id;
    private final int creatorId;

    public Family(int id, int creatorId) {
        this.id = id;
        this.creatorId = creatorId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    @Override
    public int getId() {
        return id;
    }
}
