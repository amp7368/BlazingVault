package com.blazing.vault.database.model.entity.client.meta;

import com.blazing.vault.Blazing;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.entity.client.username.NameHistoryType;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public record UpdateClientMetaHook(long id, Instant startedUpdateAt) {

    public static final Duration TIME_TO_OLD = Duration.ofMinutes(5);
    private static final Map<Long, UpdateClientMetaHook> ACTIVELY_UPDATING = new HashMap<>();

    public static void hookUpdate(DClient client) {
        Blazing.get().schedule(() -> hookUpdateTask(client), 50);
    }

    private static void hookUpdateTask(DClient client) {
        UpdateClientMetaHook task;
        synchronized (ACTIVELY_UPDATING) {
            UpdateClientMetaHook oldTask = ACTIVELY_UPDATING.get(client.getId());
            if (oldTask != null && !oldTask.isTaskOld())
                return;
            task = new UpdateClientMetaHook(client.getId(), Instant.now());
            ACTIVELY_UPDATING.put(client.getId(), task);
        }
        try {
            client.refresh();
            Future<Void> mcTask = UpdateClientMinecraftHook.minecraftUpdate(client);
            Future<Void> dcTask = UpdateClientDiscordHook.discordUpdate(client);
            client.getNameNow(NameHistoryType.DISCORD_USER);
            client.getNameNow(NameHistoryType.MINECRAFT);
            client.getNameNow(NameHistoryType.DISPLAY_NAME);
            if (mcTask != null) mcTask.get();
            if (dcTask != null) dcTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            task.scheduleRemove();
        }
    }

    private void scheduleRemove() {
        Instant expireAt = startedUpdateAt.plus(TIME_TO_OLD);
        Duration timeToExpire = Duration.between(Instant.now(), expireAt);

        Blazing.get().schedule(this::remove, timeToExpire);
    }

    private void remove() {
        synchronized (ACTIVELY_UPDATING) {
            ACTIVELY_UPDATING.remove(id());
        }
    }

    private boolean isTaskOld() {
        Duration timeTaken = Duration.between(startedUpdateAt, Instant.now());
        return timeTaken.compareTo(TIME_TO_OLD) > 0;
    }

}
