package com.blazing.vault.discord.base.command.staff;

import com.blazing.vault.database.model.entity.staff.DStaffConductor;
import com.blazing.vault.database.model.entity.staff.StaffConductorApi;
import com.blazing.vault.database.system.exception.InvalidStaffConductorException;
import com.blazing.vault.util.theme.BlazingMessages.ErrorMessages;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface StaffCommandUtil {

    static DStaffConductor getOrMakeStaff(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        try {
            return StaffConductorApi.findByDiscordOrConvert(
                user.getEffectiveName(),
                user.getIdLong());
        } catch (InvalidStaffConductorException e) {
            ErrorMessages.registerWithStaff().replyError(event);
            return null;
        }
    }

}
