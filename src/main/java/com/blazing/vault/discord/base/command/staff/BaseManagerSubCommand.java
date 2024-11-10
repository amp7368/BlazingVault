package com.blazing.vault.discord.base.command.staff;

public abstract class BaseManagerSubCommand extends BaseStaffSubCommand {

    @Override
    public boolean isOnlyManager() {
        return true;
    }
}
