package com.blazing.vault.discord.base.command.staff;

import com.blazing.vault.database.model.entity.staff.DStaffConductor;
import com.blazing.vault.discord.base.command.BaseSubCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class BaseStaffSubCommand extends BaseSubCommand {

    @Override
    protected void onCheckedCommand(SlashCommandInteractionEvent event) {
        DStaffConductor staff = StaffCommandUtil.getOrMakeStaff(event);
        if (staff == null) return;
        this.onStaffCommand(event, staff);
    }

    @Override
    public boolean isOnlyEmployee() {
        return true;
    }

    protected abstract void onStaffCommand(SlashCommandInteractionEvent event, DStaffConductor staff);
}
