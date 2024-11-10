package com.blazing.vault.database;

import apple.lib.ebean.database.AppleEbeanDatabase;
import apple.lib.ebean.database.config.AppleEbeanDatabaseConfig;
import apple.lib.ebean.database.config.AppleEbeanPostgresConfig;
import java.util.Collection;
import java.util.List;

public class BlazingDatabase extends AppleEbeanDatabase {

    @Override
    protected void addEntities(List<Class<?>> list) {

    }

    @Override
    protected Collection<Class<?>> getQueryBeans() {
        return List.of();
    }

    @Override
    protected AppleEbeanDatabaseConfig getConfig() {
        return BlazingDatabaseConfig.get();
    }

    @Override
    protected String getName() {
        return "Blazing";
    }

    public static class BlazingDatabaseConfig extends AppleEbeanPostgresConfig {

        private static BlazingDatabaseConfig instance;


        public BlazingDatabaseConfig() {
            instance = this;
        }

        public static BlazingDatabaseConfig get() {
            return instance;
        }

    }
}
