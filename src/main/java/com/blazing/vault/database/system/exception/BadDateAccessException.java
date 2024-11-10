package com.blazing.vault.database.system.exception;

import java.time.Instant;

public class BadDateAccessException extends Exception {

    private final Instant date;

    public BadDateAccessException(Instant date) {
        this.date = date;
    }

    public Instant getDate() {
        return this.date;
    }
}
