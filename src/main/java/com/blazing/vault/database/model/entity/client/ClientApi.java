package com.blazing.vault.database.model.entity.client;

import com.blazing.vault.database.model.entity.client.meta.ClientDiscordDetails;
import com.blazing.vault.database.model.entity.client.meta.ClientMinecraftDetails;
import com.blazing.vault.database.model.entity.client.query.QDClient;
import com.blazing.vault.database.system.exception.CreateEntityException;
import io.ebean.CacheMode;
import io.ebean.DuplicateKeyException;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Nullable;

public interface ClientApi {

    interface ClientQueryApi {

        static DClient findByName(String clientName) {
            DClient client = new QDClient().where()
                .displayName.ieq(clientName)
                .findOne();
            if (client != null) return client;

            client = new QDClient().where()
                .minecraft.username.ieq(clientName)
                .findOne();
            if (client != null) return client;

            client = new QDClient().where()
                .discord.username.ieq(clientName)
                .findOne();
            if (client != null) return client;
            try {
                long clientId = Long.parseLong(clientName);
                return new QDClient().where()
                    .id.eq(clientId)
                    .findOne();
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        static DClient findByDiscord(long discordId) {
            return new QDClient().where()
                .discord.id.eq(discordId)
                .setUseCache(true)
                .setBeanCacheMode(CacheMode.ON)
                .setReadOnly(false)
                .findOne();
        }

        static DClient findById(long id) {
            return new QDClient().where()
                .id.eq(id)
                .findOne();
        }

        static List<DClient> findAllReadOnly() {
            return new QDClient()
                .setUseQueryCache(true)
                .setReadOnly(true)
                .findList();
        }
    }

    interface ClientCreateApi {

        static DClient createClient(String clientName, String minecraftName, Member discordMember) throws CreateEntityException {
            if (ClientQueryApi.findByDiscord(discordMember.getIdLong()) != null) {
                throw new CreateEntityException("Your discord is already registered!");
            }
            if (ClientQueryApi.findByName(minecraftName) != null) {
                throw new CreateEntityException(
                    "That account already exists! If this is your account, it may just need to be linked to your discord");
            }

            @Nullable ClientMinecraftDetails minecraft = ClientMinecraftDetails.fromUsername(minecraftName);
            ClientDiscordDetails discord = ClientDiscordDetails.fromMember(discordMember);
            if (minecraft == null)
                throw new CreateEntityException("'%s' is not a valid minecraft username".formatted(minecraftName));

            DClient client = new DClient(clientName, minecraft, discord);
            try {
                client.save();
            } catch (DuplicateKeyException e) {
                throw new CreateEntityException(
                    "That account already exists! If this is your account, it may just need to be linked to your discord");
            }
            return client;
        }
    }
}
