package com.blazing.vault.database.model.entity.client;

import com.blazing.vault.database.model.entity.client.meta.ClientDiscordDetails;
import com.blazing.vault.database.model.entity.client.meta.ClientMinecraftDetails;
import com.blazing.vault.database.model.entity.client.meta.UpdateClientMetaHook;
import com.blazing.vault.database.model.entity.client.username.DNameHistory;
import com.blazing.vault.database.model.entity.client.username.NameHistoryType;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.History;
import io.ebean.annotation.Identity;
import io.ebean.annotation.Index;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.jetbrains.annotations.Nullable;

@Cache(enableQueryCache = true, naturalKey = {"discord.id"})
@History
@Entity
@Table(name = "client")
public class DClient extends Model implements ClientAccess {

    @Id
    @Column
    @Identity(start = 100)
    private long id;
    @Column
    @Embedded(prefix = "minecraft_")
    private ClientMinecraftDetails minecraft;
    @Column
    @Embedded(prefix = "discord_")
    private ClientDiscordDetails discord;
    @Index
    @Column(unique = true)
    private String displayName;
    @Column(nullable = false)
    private Timestamp dateCreated = Timestamp.from(Instant.now());
    @OneToMany
    private List<DNameHistory> nameHistory = new ArrayList<>();

    public DClient(String displayName) {
        this.displayName = displayName;
    }

    public DClient(String displayName, ClientMinecraftDetails minecraft, ClientDiscordDetails discord) {
        this.displayName = displayName;
        this.minecraft = minecraft;
        this.discord = discord;
    }


    public List<DNameHistory> getNameHistory(@Nullable NameHistoryType nameType) {
        Stream<DNameHistory> stream = this.nameHistory.stream();
        if (nameType != null) stream = stream.filter(n -> n.getType() == nameType);

        return stream
            .sorted(Comparator.comparing(DNameHistory::getFirstUsed).reversed())
            .toList();
    }

    public long getId() {
        return id;
    }

    @Override
    public DClient getEntity() {
        return this;
    }


    public ClientMinecraftDetails getMinecraft() {
        return minecraft.setClient(this);
    }

    public DClient setMinecraft(ClientMinecraftDetails minecraft) {
        this.minecraft.setAll(minecraft);
        return this;
    }

    public ClientDiscordDetails getDiscord(boolean shouldUpdate) {
        if (discord == null) return null;
        discord.setClient(this);
        if (shouldUpdate) UpdateClientMetaHook.hookUpdate(this);
        return this.discord;
    }

    @Nullable
    public ClientDiscordDetails getDiscord() {
        return getDiscord(true);
    }

    public DClient setDiscord(ClientDiscordDetails discord) {
        this.discord.setAll(discord);
        return this;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Instant getDateCreated() {
        return this.dateCreated.toInstant();
    }

    @Override
    public String toString() {
        return getEffectiveName();
    }
}
