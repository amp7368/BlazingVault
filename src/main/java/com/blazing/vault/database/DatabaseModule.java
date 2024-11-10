package com.blazing.vault.database;

import apple.lib.ebean.database.AppleEbeanDatabaseMetaConfig;
import apple.lib.modules.AppleModule;
import apple.lib.modules.configs.factory.AppleConfigLike;
import com.blazing.vault.Blazing;
import com.blazing.vault.database.BlazingDatabase.BlazingDatabaseConfig;
import com.blazing.vault.database.system.InitDatabase;
import java.util.List;

public class DatabaseModule extends AppleModule {


    private static DatabaseModule instance;

    public DatabaseModule() {
        instance = this;
    }

    public static DatabaseModule get() {
        return instance;
    }

    @Override
    public void onLoad() {
        AppleEbeanDatabaseMetaConfig.configureMeta(
            Blazing.class,
            Blazing.get().getDataFolder(),
            logger()::error,
            logger()::info);

        new BlazingDatabase();
        InitDatabase.init();
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(configJson(BlazingDatabaseConfig.class, "DatabaseConfig"));
    }

    @Override
    public String getName() {
        return "Database";
    }
}
