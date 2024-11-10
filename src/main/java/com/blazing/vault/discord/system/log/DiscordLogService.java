package com.blazing.vault.discord.system.log;

import com.blazing.vault.Blazing;
import com.blazing.vault.config.discord.DiscordConfig;
import com.blazing.vault.database.model.entity.actor.UserActor;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.message.log.DLog;
import com.blazing.vault.discord.DiscordModule;
import com.blazing.vault.discord.base.message.client.ClientMessage;
import com.blazing.vault.discord.system.log.modifier.DiscordLogModifier;
import com.blazing.vault.util.theme.BlazingColor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.jetbrains.annotations.NotNull;

public class DiscordLogService {

    private static TextChannel channel;
    private final DClient client;
    private final UserActor actor;
    private final List<String> message = new ArrayList<>();
    private final String category;
    private final String logType;
    private final List<DiscordLogModifier> modifiers = new ArrayList<>();
    private Map<String, Object> json;
    private String finalizedMessage;

    public DiscordLogService(DClient client, UserActor actor, String category, String logType, String message) {
        this.client = client;
        this.actor = actor;
        this.category = category;
        this.logType = logType;
        this.message.add(message);
    }

    public static void load() {
        channel = DiscordConfig.get().getLogChannel();
        if (channel == null) {
            String msg = "Log dest{%d} is not a valid dest".formatted(DiscordConfig.get().getLogChannelId());
            throw new IllegalArgumentException(msg);
        }
    }

    private static void send(String log, MessageEmbed embed) {
        String msg = log.replace("\n", "  ").trim();
        DiscordModule.get().logger().info(msg);
        channel.sendMessageEmbeds(embed).queue();
    }

    private void handleModifiers() {
        modifiers.sort(DiscordLogModifier.COMPARATOR);
        modifiers.forEach(mod -> mod.modify(this));
    }

    private String finalizeMessage() {
        String msg = String.join("\n", this.message);
        if (this.json == null) return msg;
        Map<String, String> stringMap = this.json.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey,
                e -> Objects.toString(e.getValue())
            ));
        return StrSubstitutor.replace(msg, stringMap);
    }

    public final Future<DiscordLogService> submit() {
        return Blazing.get().submit(this::_run);
    }

    private void gatherData() {
        actor.fetch();
    }

    private DiscordLogService _run() {
        this.gatherData();
        this.handleModifiers();
        this.finalizedMessage = this.finalizeMessage();
        send(this.log(), embed().build());
        new DLog(this).save();
        return this;
    }

    public String log() {
        if (client != null)
            return new ParameterizedMessage(
                "{} - {} <= {}: \"{}\"",
                getTitle(),
                client.getEffectiveName(),
                getActor().getName(),
                getMessage()
            ).getFormattedMessage();

        return new ParameterizedMessage(
            "{} <= {}: \"{}\"",
            getTitle(),
            getActor().getName(),
            getMessage()
        ).getFormattedMessage();
    }

    @NotNull
    public EmbedBuilder embed() {
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle(getTitle())
            .appendDescription(this.getMessage())
            .setColor(BlazingColor.GREEN)
            .setFooter(getActor().getName(), getActor().getActorUrl())
            .setTimestamp(Instant.now());
        if (client != null) ClientMessage.of(client).clientAuthor(embed);
        return embed;
    }

    private @NotNull String getTitle() {
        return "[%s] %s".formatted(this.getCategory(), this.getLogType());
    }

    public DiscordLogService modify(DiscordLogModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public DiscordLogService addJson(String key, Object value) {
        if (this.json == null) this.json = new HashMap<>();
        this.json.put(key, value);
        return this;
    }

    public void prependMsg(String msg) {
        this.message.add(0, msg);
    }

    public DClient getClient() {
        return this.client;
    }

    public String getCategory() {
        return this.category;
    }

    public String getLogType() {
        return this.logType;
    }

    public String getMessage() {
        return this.finalizedMessage;
    }

    public Map<String, Object> getJson() {
        return this.json;
    }

    public UserActor getActor() {
        return this.actor;
    }
}
