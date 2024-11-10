package com.blazing.vault.database.model.entity.client;

import com.blazing.vault.database.model.entity.actor.UserActor;
import com.blazing.vault.database.model.entity.client.meta.ClientDiscordDetails;
import com.blazing.vault.database.model.entity.client.meta.ClientMinecraftDetails;
import com.blazing.vault.database.model.entity.client.username.DNameHistory;
import com.blazing.vault.database.model.entity.client.username.NameHistoryType;
import com.blazing.vault.discord.system.log.DiscordLog;
import java.util.List;
import java.util.function.Function;
import net.dv8tion.jda.api.entities.User;

public interface ClientAccess {

    DClient getEntity();

    default String getEffectiveName() {
        if (getEntity().getDisplayName() != null) return getEntity().getDisplayName();
        String minecraft = getMinecraft(ClientMinecraftDetails::getUsername);
        if (minecraft != null) return minecraft;
        String discord = getDiscord(ClientDiscordDetails::getUsername);
        if (discord != null) return discord;
        return "Not Found!";
    }


    default <T> T getMinecraft(Function<ClientMinecraftDetails, T> apply) {
        ClientMinecraftDetails minecraft = getEntity().getMinecraft();
        if (minecraft == null) return null;
        return apply.apply(minecraft);
    }

    default <T> T getDiscord(Function<ClientDiscordDetails, T> apply) {
        ClientDiscordDetails discord = getEntity().getDiscord();
        if (discord == null) return null;
        return apply.apply(discord);
    }

    default boolean isUser(User user) {
        Long discord = getDiscord(ClientDiscordDetails::getDiscordId);
        return discord != null && discord == user.getIdLong();
    }

    default DNameHistory getNameNow(NameHistoryType nameType) {
        List<DNameHistory> history = getEntity().getNameHistory(nameType);
        if (!history.isEmpty()) {
            DNameHistory now = history.getFirst();
            if (!now.isCurrent()) {
                String msg = "First entry in name history is not currently in use!";
                DiscordLog.error(msg, UserActor.of(getEntity()));
            }
            return now;
        }
        DNameHistory firstName = nameType.createFromClient(getEntity());
        firstName.save();
        getEntity().refresh();
        return firstName;
    }
}
