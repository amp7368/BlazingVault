package com.blazing.vault.discord.misc.autocomplete;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class AutoCompleteListener extends ListenerAdapter {


    private final Map<String, BlazingAutoComplete<?>> autoCompletes = new HashMap<>();

    public AutoCompleteListener() {
        getAutoCompletes().forEach(auto -> autoCompletes.put(auto.getOptionName(), auto));
    }

    @NotNull
    private List<BlazingAutoComplete<?>> getAutoCompletes() {
        return List.of(new ClientAutoComplete("owner"), new ClientAutoComplete("client"));
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        String option = event.getFocusedOption().getName();
        BlazingAutoComplete<?> autoComplete;
        synchronized (autoCompletes) {
            autoComplete = autoCompletes.get(option);
        }
        if (autoComplete != null)
            autoComplete.autoCompleteChoices(event, event.getFocusedOption().getValue());
    }
}
