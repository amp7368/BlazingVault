package com.blazing.vault.database.model.entity.actor;

import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.entity.staff.DStaffConductor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

public interface UserActor {

    static UserActor of(DStaffConductor conductor) {
        return new UserActorImpl(conductor);
    }

    static UserActor of(DClient client) {
        return new UserActorImpl(client);
    }

    static UserActor of(Future<User> discord) {
        return new UserActorImpl(discord);
    }

    static UserActor of(User discord) {
        return new UserActorImpl(CompletableFuture.completedFuture(discord));
    }

    static UserActor system() {
        return of(DStaffConductor.SYSTEM);
    }

    void fetch();

    @Nullable
    Long getDiscordIdLong();

    @Nullable
    String getName();

    @Nullable
    DClient getClient();

    String getActorUrl();
}
