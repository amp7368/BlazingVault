package com.blazing.vault.database.model.entity.staff;

import com.blazing.vault.database.model.entity.client.ClientApi.ClientQueryApi;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.entity.staff.query.QDStaffConductor;
import com.blazing.vault.database.system.exception.InvalidStaffConductorException;

public class StaffConductorApi {

    public static DStaffConductor findByDiscord(long discordId) {
        return new QDStaffConductor().where()
            .client.discord.id.eq(discordId)
            .findOne();
    }

    public static DStaffConductor create(DClient client) {
        DStaffConductor conductor = new DStaffConductor(client);
        conductor.save();
        return conductor;
    }

    public static DStaffConductor findByDiscordOrConvert(String staffUsername, long staffId) throws InvalidStaffConductorException {
        DStaffConductor conductor = StaffConductorApi.findByDiscord(staffId);
        if (conductor != null) return conductor;
        DClient client = ClientQueryApi.findByDiscord(staffId);
        if (client == null) throw new InvalidStaffConductorException(staffUsername, staffId);
        return create(client);
    }
}
