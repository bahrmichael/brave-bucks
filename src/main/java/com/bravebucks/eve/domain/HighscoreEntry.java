package com.bravebucks.eve.domain;

public class HighscoreEntry {
    private final String name;
    private final double amount;

    public HighscoreEntry(final String name, final double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }
}
