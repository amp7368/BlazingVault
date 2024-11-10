package com.blazing.vault.discord.system.log.modifier;

import apple.utilities.util.Pretty;
import com.blazing.vault.discord.system.log.DiscordLogService;
import com.blazing.vault.util.theme.BlazingAssets.BlazingEmoji;
import io.ebean.DB;
import java.util.Comparator;

@FunctionalInterface
public interface DiscordLogModifier {

    Comparator<DiscordLogModifier> COMPARATOR = Comparator.comparing(DiscordLogModifier::getPriority);

    static DiscordLogModifier addEntity(String entityType, String entityId) {
        String msg = "%s %s %s".formatted(Pretty.spaceEnumWords(entityType), BlazingEmoji.KEY_ID, entityId);
        return log -> log.prependMsg(msg);
    }

    static DiscordLogModifier addEntity(String entityType, Object entity) {
        return addEntity(entityType, DB.beanId(entity).toString());
    }


    default int getPriority() {
        return 0;
    }

    default DiscordLogModifier withPriority(int priority) {
        return new DiscordLogModifierImpl(priority, this);
    }

    void modify(DiscordLogService log);

}
