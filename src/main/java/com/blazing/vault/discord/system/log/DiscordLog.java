package com.blazing.vault.discord.system.log;

import static com.blazing.vault.util.theme.BlazingMessages.formatDate;

import com.blazing.vault.Blazing;
import com.blazing.vault.database.model.entity.actor.UserActor;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.entity.client.meta.ClientDiscordDetails;
import com.blazing.vault.database.model.entity.client.meta.ClientMinecraftDetails;
import com.blazing.vault.database.model.entity.client.username.DNameHistory;
import com.blazing.vault.database.model.entity.client.username.NameHistoryType;
import com.blazing.vault.database.model.entity.staff.DStaffConductor;
import com.blazing.vault.util.theme.BlazingAssets.BlazingEmoji;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.local.LocalBucket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import net.dv8tion.jda.api.entities.User;

public interface DiscordLog {

    LocalBucket ERROR_RATE_LIMIT = Bucket.builder()
        .addLimit(limit -> limit.capacity(20)
            .refillIntervally(10, Duration.ofHours(3))
            .initialTokens(20))
        .build();

    static UserActor actor(User actor) {
        return UserActor.of(actor);
    }

    private static DiscordLogService account(DClient client, UserActor actor, String type, String message) {
        return new DiscordLogService(client, actor, "Account", type, message);
    }

    static void modifyDiscord(DClient client, UserActor actor) {
        futureLog(() -> modifyDiscord_(client, actor));
    }

    private static DiscordLogService modifyDiscord_(DClient client, UserActor actor) {
        String type = "Modify Discord";
        String message = "Set Discord to @{discord}";
        String discord = client.getDiscord(ClientDiscordDetails::getUsername);
        return account(client, actor, type, message)
            .addJson("discord", discord);
    }


    static void modifyMinecraft(DClient client, UserActor actor) {
        futureLog(() -> modifyMinecraft_(client, actor));
    }

    private static DiscordLogService modifyMinecraft_(DClient client, UserActor actor) {
        String type = "Modify Discord";
        String message = "Set Minecraft to @{minecraft}";
        String minecraft = client.getMinecraft(ClientMinecraftDetails::getUsername);
        return account(client, actor, type, message)
            .addJson("minecraft", minecraft);
    }

    static void createAccount(DClient client, UserActor actor) {
        futureLog(() -> createAccount_(client, actor));
    }

    private static DiscordLogService createAccount_(DClient client, UserActor actor) {
        String type = "Create Account";
        String message = "Account was created";
        return account(client, actor, type, message);
    }

    static void updateAccount(DClient client, UserActor actor) {
        futureLog(() -> updateAccount_(client, actor));
    }

    private static DiscordLogService updateAccount_(DClient client, UserActor actor) {
        String type = "Update Account";
        String message = "Account was updated";
        return account(client, actor, type, message);
    }


    static void updateName(DNameHistory lastName, DNameHistory newName) {
        futureLog(() -> updateName_(lastName, newName));
    }

    private static DiscordLogService updateName_(DNameHistory lastName, DNameHistory newName) {
        DClient client = lastName.getClient();
        UserActor actor = UserActor.of(DStaffConductor.SYSTEM);
        String category = "Name History";
        NameHistoryType nameHistoryType = lastName.getType();
        String logType = nameHistoryType.toString();
        String msg = """
            %s username updated
            %s **%s** => **%s**
            %s""".formatted(logType, BlazingEmoji.CLIENT_ACCOUNT, lastName.getName(), newName.getName(),
            formatDate(newName.getFirstUsed(), true));
        return new DiscordLogService(client, actor, category, logType, msg);
    }

    private static CompletableFuture<DiscordLogService> futureLog(Supplier<DiscordLogService> createLog) {
        CompletableFuture<DiscordLogService> future = new CompletableFuture<>();
        Blazing.get().execute(() -> {
            try {
                DiscordLogService log = createLog.get();
                future.complete(log.submit().get());
            } catch (InterruptedException | ExecutionException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    static void infoSystem(String msg) {
        futureLog(() -> infoSystem_(msg));
    }

    private static DiscordLogService infoSystem_(String msg) {
        return new DiscordLogService(null, UserActor.system(), "System", "Info", msg);
    }

    static void errorSystem(String msg) {
        error(msg, UserActor.of(DStaffConductor.SYSTEM));
    }

    static void error(String msg, UserActor actor) {
        boolean test = ERROR_RATE_LIMIT.tryConsume(1);
        if (!test) {
            Blazing.get().logger().error(msg);
            return;
        }
        futureLog(() -> error_(msg, actor));
    }

    private static DiscordLogService error_(String msg, UserActor of) {
        return new DiscordLogService(null, of, "System", "Error", msg);
    }
}
