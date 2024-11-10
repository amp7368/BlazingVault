package com.blazing.vault.database.model.item;

import io.ebean.annotation.DbEnumValue;

public enum ItemStatus {
    NOT_COLLECTED,
    IN_VAULT,
    AVAILABLE,
    RENTED;

    @DbEnumValue
    public String id() {
        return name();
    }

}
