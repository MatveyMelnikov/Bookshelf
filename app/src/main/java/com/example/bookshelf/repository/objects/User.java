package com.example.bookshelf.repository.objects;

public class User implements RepositoryObject {
    private final int id;
    private final String name;
    private final String hash;
    private final Boolean isChild;

    private Integer familyId;

    public User(int id, String name, String hash, Boolean isChild, Integer familyId) {
        this.id = id;
        this.name = name;
        this.hash = hash;
        this.isChild = isChild;
        this.familyId = familyId;
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

    public Boolean isChild() {
        return isChild;
    }

    public Integer getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Integer familyId) {
        this.familyId = familyId;
    }
}
