package com.blazing.vault.discord.staff.item;

import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.entity.staff.DStaffConductor;
import com.blazing.vault.database.model.item.DItem;
import com.blazing.vault.database.model.item.ItemApi;
import com.blazing.vault.database.model.item.ItemStatus;
import com.blazing.vault.discord.base.command.option.CommandOption;
import com.blazing.vault.discord.base.command.option.CommandOptionList;
import com.blazing.vault.discord.base.command.staff.BaseStaffSubCommand;
import com.blazing.vault.util.emerald.Emeralds;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.Nullable;

public class ItemCreateSubCommand extends BaseStaffSubCommand {

    @Override
    protected void onStaffCommand(SlashCommandInteractionEvent event, DStaffConductor staff) {
        DClient owner = CommandOption.OWNER.getRequired(event);
        if (owner == null) return;
        Emeralds price = CommandOption.PRICE.getRequired(event);
        if (price == null) return;
        Attachment attachment = CommandOption.ITEM_IMAGE.getRequired(event);
        if (attachment == null) return;
        String name = event.getOption("name", OptionMapping::getAsString);
        if (name == null) return;
        @Nullable String description = event.getOption("description", OptionMapping::getAsString);

        if (!attachment.isImage()) {
            replyError(event, "Attachment is not a valid image");
            return;
        }
        File tempFile;
        try {
            tempFile = File.createTempFile("blazing_item_", UUID.randomUUID().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CompletableFuture<File> downloadAction = attachment.getProxy().downloadToFile(tempFile);
        String extension = attachment.getFileExtension();
        Function<File, DItem> createItem = image -> ItemApi.createItem(owner, name, extension, description,
            ItemStatus.IN_VAULT, price, image);
        event.deferReply().queue(
            defer -> onDefer(defer, downloadAction, createItem, owner, name)
        );
    }

    private void onDefer(InteractionHook defer, CompletableFuture<File> downloadAction, Function<File, DItem> createItem,
        DClient owner, String name) {
        downloadAction.whenComplete(
            (image, err) -> {
                if (err != null) {
                    handleError(defer);
                    return;
                }
                createItem.apply(image);
                String msg = "Successfully added item %s's %s".formatted(owner.getEffectiveName(), name);
                System.out.println(msg);
                MessageEmbed embed = success()
                    .appendDescription(msg)
                    .build();
                defer.editOriginalEmbeds(embed).queue();
            }
        );
    }

    private void handleError(InteractionHook defer) {
        defer.editOriginalEmbeds(
            error().appendDescription("Unable to download image!").build()
        ).queue();
    }

    @Override
    public SubcommandData getData() {
        SubcommandData command = new SubcommandData("create", "Create and add an item to the vault");
        command.addOption(OptionType.STRING, "name", "The name of the item", true);

        CommandOptionList.of(
            List.of(CommandOption.OWNER, CommandOption.PRICE, CommandOption.ITEM_IMAGE)
        ).addToCommand(command);

        command.addOption(OptionType.STRING, "description", "Any description of the item");

        return command;
    }
}
