package com.blazing.vault.database.model.entity.client.meta;

import com.blazing.vault.Blazing;
import com.blazing.vault.config.discord.DiscordConfig;
import com.blazing.vault.database.DatabaseModule;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.entity.client.username.DNameHistory;
import com.blazing.vault.database.model.entity.client.username.NameHistoryType;
import com.blazing.vault.discord.DiscordBot;
import com.blazing.vault.discord.DiscordModule;
import com.blazing.vault.discord.system.log.DiscordLog;
import io.ebean.DB;
import io.ebean.Transaction;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class UpdateClientDiscordHook {

    private static final Duration HOURS_TILL_UPDATE = Duration.ofHours(1);

    public static Future<Void> discordUpdate(DClient client) {
        ClientDiscordDetails discord = client.getDiscord(false);
        if (discord == null) return null;
        Long discordId = discord.getDiscordId();
        if (discordId == null) return null;

        Duration between = Duration.between(discord.getLastUpdated(), Instant.now());
        if (between.compareTo(HOURS_TILL_UPDATE) < 0) return null;

        DatabaseModule.get().logger().info("Updating discord {}{{}}", discord.getUsername(), discordId);

        CompletableFuture<Void> task = new CompletableFuture<>();
        Member cachedMember = DiscordConfig.getMainServer().getMemberById(discordId);
        if (cachedMember != null) {
            updateBlazingMember(client, cachedMember, task);
            return task;
        }

        DiscordConfig.getMainServer().retrieveMemberById(discordId)
            .queue(member -> updateBlazingMember(client, member, task),
                fail -> updateBlazingMemberFailed(client, discordId, task));
        return task;
    }

    private static void updateBlazingMember(DClient client, Member cachedMember, CompletableFuture<Void> task) {
        Blazing.get().submit(() -> {
            ClientDiscordDetails disc = ClientDiscordDetails.fromMember(cachedMember);
            updateDiscord(client, disc, task);
        });
    }

    private static void updateBlazingMemberFailed(DClient client, long discordId, CompletableFuture<Void> task) {
        DiscordBot.jda().retrieveUserById(discordId).queue(
            user -> updateFromUser(client, user, task),
            e -> retrieveUserFailed(client, discordId, task));
    }

    private static void retrieveUserFailed(DClient client, long discordId, CompletableFuture<Void> task) {
        try {
            DiscordModule.get().logger().error("Could not update discord for: client {} discord{{}}",
                client.getEffectiveName(), discordId);
            ClientDiscordDetails discord = client.getDiscord(false);
            client.setDiscord(discord.updated());
            client.save();
        } catch (Exception e) {
            Blazing.get().logger().error("", e);
            DiscordLog.errorSystem("Cannot save Discord");
        } finally {
            task.complete(null);
        }
    }

    private static void updateFromUser(DClient client, User user, CompletableFuture<Void> task) {
        Blazing.get().submit(() -> {
            ClientDiscordDetails disc = ClientDiscordDetails.fromUser(user);
            updateDiscord(client, disc, task);
        });
    }

    private static void updateDiscord(DClient client, ClientDiscordDetails disc, CompletableFuture<Void> task) {
        try (Transaction transaction = DB.beginTransaction()) {
            client.refresh();
            boolean isNewName = client.getDiscord(false).isNewName(disc);
            if (isNewName) {
                DNameHistory lastName = client.getNameNow(NameHistoryType.DISCORD_USER);
                client.setDiscord(disc);
                DNameHistory newName = NameHistoryType.DISCORD_USER.updateName(client, lastName, transaction);
                DiscordLog.updateName(lastName, newName);
            }
            client.setDiscord(disc);
            client.save(transaction);
            transaction.commit();
            client.refresh();
        } catch (Exception e) {
            Blazing.get().logger().error("", e);
            DiscordLog.errorSystem("Cannot save Discord");
        } finally {
            task.complete(null);
        }
    }
}
