package com.civilease.model;

public enum Department {
    PLSOFT("플솦"), SIMCOM("심컴");

    private final String name;
    Department(String name) { this.name = name; }
    public String getName() { return name; }

    @Override
    public String toString() { return name; }
}
