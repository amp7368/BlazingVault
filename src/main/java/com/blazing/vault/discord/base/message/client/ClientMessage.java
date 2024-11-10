package com.blazing.vault.discord.base.message.client;

import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.entity.client.meta.ClientDiscordDetails;
import com.blazing.vault.database.model.entity.client.meta.ClientMinecraftDetails;
import java.util.Objects;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.Nullable;

public interface ClientMessage {

    static ClientMessageBuilder of(DClient client) {
        return new ClientMessageBuilder(client);
    }

    DClient getClient();

    default void clientAuthor(EmbedBuilder embed) {
        DClient client = getClient();

        @Nullable String display = client.getDisplayName();
        @Nullable String mcName = client.getMinecraft(ClientMinecraftDetails::getUsername);
        @Nullable String discordName = client.getDiscord(ClientDiscordDetails::getUsername);
        Optional<String> minecraft = Optional.ofNullable(mcName);
        Optional<String> discord = Optional.ofNullable(discordName);

        String author;
        if (minecraft.isPresent() && discord.isPresent()) {
            if (display == null)
                author = minecraft.get() + "\n@" + discord.get();
            else
                author = "%s (%s)\n@%s"
                    .formatted(minecraft.get(), display, discord.get());
        } else if (discord.isEmpty() && minecraft.isEmpty()) {
            author = display;
        } else {
            author = discord.map(s -> "@" + s)
                .orElseGet(minecraft::get);
            if (display != null)
                author += " (%s)".formatted(display);
        }

        String url = minecraft.map(mc -> "https://wynncraft.com/stats/player/" + mc).orElse(null);

        String discordAvatar = client.getDiscord(ClientDiscordDetails::getAvatarUrl);
        String minecraftAvatar = client.getMinecraft(ClientMinecraftDetails::skinUrl);

        embed.setThumbnail(Objects.requireNonNullElse(minecraftAvatar, discordAvatar));
        embed.setAuthor(author, url, discordAvatar);
    }
}
