package com.blazing.vault.discord.staff.item;

import com.blazing.vault.discord.base.command.staff.BaseStaffCommand;
import discord.util.dcf.slash.DCFSlashSubCommand;
import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ItemCommand extends BaseStaffCommand {

    @Override
    public List<DCFSlashSubCommand> getSubCommands() {
        return List.of(new ItemCreateSubCommand());
    }

    @Override
    public SlashCommandData getStaffData() {
        return Commands.slash("item", "Item related commands");
    }
}
