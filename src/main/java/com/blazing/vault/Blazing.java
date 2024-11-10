package com.blazing.vault;

import apple.lib.modules.AppleModule;
import apple.lib.modules.ApplePlugin;
import apple.lib.modules.configs.factory.AppleConfigLike;
import com.blazing.vault.config.BlazingConfig;
import com.blazing.vault.config.BlazingStaffConfig;
import com.blazing.vault.database.DatabaseModule;
import com.blazing.vault.discord.DiscordModule;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class Blazing extends ApplePlugin {

    private static Blazing instance;

    public Blazing() {
        instance = this;
    }

    public static void main(String[] args) {
        new Blazing().start();
    }

    public static Blazing get() {
        return instance;
    }

    public <T> void futureComplete(CompletableFuture<T> future, T value) {
        execute(() -> future.complete(value));
    }

    public <T> void futureException(CompletableFuture<T> future, Throwable e) {
        execute(() -> future.completeExceptionally(e));
    }

    public <T> Consumer<T> futureComplete(CompletableFuture<T> sent) {
        return obj -> futureComplete(sent, obj);
    }

    public <E extends Throwable, T> Consumer<E> futureException(CompletableFuture<T> sent) {
        return obj -> futureException(sent, obj);
    }

    @Override
    protected ScheduledExecutorService makeExecutor() {
        return Executors.newScheduledThreadPool(5);
    }

    @Override
    public String getName() {
        return "BlazingVault";
    }

    @Override
    public List<AppleModule> createModules() {
        return List.of(new DiscordModule(), new DatabaseModule());
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(
            configJson(BlazingConfig.class, "BlazingConfig").setPretty(),
            configJson(BlazingStaffConfig.class, "BlazingStaffConfig").setPretty()
        );
    }
}
