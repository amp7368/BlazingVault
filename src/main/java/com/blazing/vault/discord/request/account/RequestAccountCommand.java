package com.blazing.vault.discord.request.account;

import com.blazing.vault.database.model.entity.actor.UserActor;
import com.blazing.vault.database.model.entity.client.ClientApi.ClientCreateApi;
import com.blazing.vault.database.model.entity.client.ClientApi.ClientQueryApi;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.system.exception.CreateEntityException;
import com.blazing.vault.discord.base.command.BaseSubCommand;
import com.blazing.vault.discord.base.command.option.CommandOption;
import com.blazing.vault.discord.system.log.DiscordLog;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class RequestAccountCommand extends BaseSubCommand {

    private static final String OPTION_MINECRAFT = "minecraft";
    private static final String OPTION_DISPLAY_NAME = "display_name";

    @Override
    public boolean requiresMember() {
        return true;
    }

    @Override
    protected void onCheckedCommand(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        String minecraft = CommandOption.MINECRAFT.getRequired(event);
        String displayNameOption = CommandOption.DISPLAY_NAME.getOptional(event);
        event.deferReply().queue((reply) -> {
            try {
                DClient client = ClientQueryApi.findByDiscord(event.getUser().getIdLong());
                if (client != null) {
                    event.replyEmbeds(error("Your account is already registered")).queue();
                    return;
                }
                String displayName = displayNameOption;
                if (displayName == null) displayName = minecraft;
                if (displayName == null) displayName = member.getEffectiveName();
                client = ClientCreateApi.createClient(displayName, minecraft, member);
                MessageEmbed msg = success("Successfully registered as %s".formatted(client.getEffectiveName()));
                reply.editOriginalEmbeds(msg).queue();
                DiscordLog.createAccount(client, UserActor.of(event.getUser()));
            } catch (CreateEntityException e) {
                reply.editOriginalEmbeds(error(e.getMessage())).queue();
            }
        });
    }

    @Override
    public SubcommandData getData() {
        SubcommandData command = new SubcommandData("account", "Register an account");
        command.addOption(OptionType.STRING, OPTION_MINECRAFT, "Your minecraft in-game name", true);
        command.addOption(OptionType.STRING, OPTION_DISPLAY_NAME, "Your profile display name");
        return command;
    }
}
