package com.blazing.vault.database;

import apple.lib.ebean.database.AppleEbeanDatabase;
import apple.lib.ebean.database.config.AppleEbeanDatabaseConfig;
import apple.lib.ebean.database.config.AppleEbeanPostgresConfig;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.entity.client.meta.ClientDiscordDetails;
import com.blazing.vault.database.model.entity.client.meta.ClientMinecraftDetails;
import com.blazing.vault.database.model.entity.staff.DStaffConductor;
import com.blazing.vault.database.model.image.DImage;
import com.blazing.vault.database.model.item.DItem;
import com.blazing.vault.database.model.item.rent.DRentingAction;
import com.blazing.vault.database.model.item.rent.DRentingPrice;
import com.blazing.vault.database.model.message.log.DLog;
import java.util.Collection;
import java.util.List;

public class BlazingDatabase extends AppleEbeanDatabase {

    @Override
    protected void addEntities(List<Class<?>> entities) {
        // client
        entities.addAll(List.of(ClientDiscordDetails.class, ClientMinecraftDetails.class));
        entities.add(DClient.class);
        // staff
        entities.add(DStaffConductor.class);

        // item
        entities.addAll(List.of(DItem.class, DRentingAction.class, DRentingPrice.class));

        // misc
        entities.add(DImage.class);
        entities.add(DLog.class);
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
    protected boolean isDefault() {
        return true;
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
