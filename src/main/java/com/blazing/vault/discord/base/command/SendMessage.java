package com.blazing.vault.discord.base.command;

import com.blazing.vault.util.theme.BlazingColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface SendMessage {

    static SendMessage get() {
        return new SendMessage() {
        };
    }


    default String title(String title) {
        return title;
    }

    default String title(String title, int page, int maxPage) {
        return "%s (%d/%d)".formatted(title, page + 1, maxPage + 1);
    }

    default EmbedBuilder success() {
        return new EmbedBuilder()
            .setColor(BlazingColor.GREEN)
            .setAuthor("Success!", null, null);
    }

    default MessageEmbed success(String msg) {
        return success().setDescription(msg).build();
    }

    default void replySuccess(CommandInteraction event, String msg) {
        event.replyEmbeds(success(msg)).queue();
    }

    default EmbedBuilder error() {
        return new EmbedBuilder()
            .setAuthor("Error!", null, null)
            .setColor(BlazingColor.RED);
    }

    default MessageEmbed error(String msg) {
        return error().setDescription(msg).build();
    }

    default void replyError(CommandInteraction event, String msg) {
        event.replyEmbeds(error(msg))
            .setEphemeral(true)
            .queue();
    }

    default void replyError(CommandInteraction event, MessageCreateData msg) {
        event.reply(msg)
            .setEphemeral(true)
            .queue();
    }

    default EmbedBuilder warning() {
        return new EmbedBuilder()
            .setAuthor("Completed with warnings..", null, null)
            .setColor(BlazingColor.YELLOW);
    }

    default MessageEmbed warning(String msg) {
        return warning().setDescription(msg).build();
    }

    default void replyWarning(CommandInteraction event, String msg) {
        event.replyEmbeds(warning(msg)).queue();
    }

    default void replyWarning(CommandInteraction event, MessageCreateData msg) {
        event.reply(msg).queue();
    }
}
