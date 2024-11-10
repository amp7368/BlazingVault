package com.blazing.vault.discord.base.message.client;

import com.blazing.vault.database.model.entity.client.DClient;

public class ClientMessageBuilder implements ClientMessage {

    private final DClient client;

    ClientMessageBuilder(DClient client) {
        this.client = client;
    }

    @Override
    public DClient getClient() {
        return client;
    }
}
