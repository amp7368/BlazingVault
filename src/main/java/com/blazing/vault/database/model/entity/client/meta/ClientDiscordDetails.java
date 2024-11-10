package com.blazing.vault.database.model.entity.client.meta;

import com.blazing.vault.Blazing;
import com.blazing.vault.database.model.entity.actor.UserActor;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.discord.DiscordBot;
import com.blazing.vault.discord.system.log.DiscordLog;
import io.ebean.annotation.Index;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.jetbrains.annotations.Nullable;

@Embeddable
public class ClientDiscordDetails {

    @Index
    @EmbeddedId
    @Column(unique = true)
    public Long id;
    @Column
    public String avatarUrl;
    @Index
    @Column
    public String username;
    @Column
    private Timestamp lastUpdated;
    private transient DClient client;

    private ClientDiscordDetails(Long id, String avatarUrl, String username) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.lastUpdated = Timestamp.from(Instant.now());
    }

    public static ClientDiscordDetails fromMember(Member member) {
        long discordId = member.getIdLong();
        String avatarUrl = member.getEffectiveAvatarUrl();
        String username = member.getEffectiveName();
        return new ClientDiscordDetails(discordId, avatarUrl, username);
    }

    public static ClientDiscordDetails fromUser(User user) {
        long discordId = user.getIdLong();
        String avatarUrl = user.getEffectiveAvatarUrl();
        String username = user.getEffectiveName();
        return new ClientDiscordDetails(discordId, avatarUrl, username);
    }

    public static ClientDiscordDetails fromManual(Long discordId, String avatarUrl, String username) {
        return new ClientDiscordDetails(discordId, avatarUrl, username);
    }

    public ClientDiscordDetails setClient(DClient client) {
        this.client = client;
        return this;
    }

    public boolean isNewName(ClientDiscordDetails other) {
        return !Objects.equals(this.username, other.username);
    }

    public String getUsername() {
        return this.username;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public Long getDiscordId() {
        return id;
    }


    public CompletableFuture<PrivateChannel> tryOpenDirectMessages() {
        CompletableFuture<PrivateChannel> future = new CompletableFuture<>();

        if (getDiscordId() == null) {
            String msg = "Cannot open DMs with @%s. No discord id registered.".formatted(getUsername());
            IllegalStateException err = new IllegalStateException(msg);
            future.completeExceptionally(err);
            return future;
        }

        DiscordBot.jda().openPrivateChannelById(getDiscordId()).queue(
            (chan) -> Blazing.get().futureComplete(future, chan),
            (e) -> Blazing.get().futureException(future, e)
        );
        return future;
    }

    public CompletableFuture<Message> sendDm(MessageCreateData message) {
        return sendDm(message, null, null);
    }

    public CompletableFuture<Message> sendDm(MessageCreateData message,
        @Nullable Consumer<Message> onSuccess,
        @Nullable Consumer<Throwable> onError) {
        CompletableFuture<Message> futureMsg = DiscordBot.jda().openPrivateChannelById(getDiscordId())
            .flatMap(chan -> chan.sendMessage(message))
            .submit();

        futureMsg.whenCompleteAsync((msg, err) -> {
            if (err != null) {
                if (onError != null)
                    onError.accept(err);
            } else if (onSuccess != null) onSuccess.accept(msg);
        }, Blazing.get().executor());
        return futureMsg;
    }

    private void sendDmError(Throwable e) {
        ParameterizedMessage msg = new ParameterizedMessage("Failed to send message to @{}.", getUsername());
        Blazing.get().logger().error(msg, e);
        DiscordLog.error(msg.getFormattedMessage(), UserActor.system());
    }

    public Instant getLastUpdated() {
        return lastUpdated.toInstant();
    }

    public ClientDiscordDetails updated() {
        return new ClientDiscordDetails(this.id, this.avatarUrl, this.username);
    }

    public Object json() {
        return Map.of(
            "id", id,
            "username", username,
            "avatarUrl", avatarUrl
        );
    }

    public void setAll(ClientDiscordDetails other) {
        this.id = other.id;
        this.avatarUrl = other.avatarUrl;
        this.username = other.username;
        this.lastUpdated = other.lastUpdated;
    }
}
