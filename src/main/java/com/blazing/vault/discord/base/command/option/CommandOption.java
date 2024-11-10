package com.blazing.vault.discord.base.command.option;

import apple.utilities.util.Pretty;
import com.blazing.vault.database.model.entity.client.ClientApi.ClientQueryApi;
import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.discord.system.help.HelpCommandListType;
import com.blazing.vault.util.emerald.Emeralds;
import com.blazing.vault.util.theme.BlazingMessage;
import com.blazing.vault.util.theme.BlazingMessages.ErrorMessages;
import java.util.function.Function;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommandOption<R> {

    // client
    CommandOptionMulti<String, DClient> CLIENT = multi("client", "Client associated with this action", OptionType.STRING,
        OptionMapping::getAsString, ClientQueryApi::findByName).setAutocomplete();
    CommandOption<Member> DISCORD = basic("discord", "The discord of the client", OptionType.MENTIONABLE,
        OptionMapping::getAsMember);
    CommandOption<String> MINECRAFT = basic("minecraft", "Your minecraft username", OptionType.STRING,
        OptionMapping::getAsString);
    CommandOption<String> DISPLAY_NAME = basic("display_name", "The name to display on the profile", OptionType.STRING,
        OptionMapping::getAsString);

    // common
    CommandOptionDate DATE = new CommandOptionDate();
    CommandOptionDate LOAN_START_DATE = new CommandOptionDate("start_date",
        "The start date (MM/DD/YY) for the loan. (Defaults to current date if not specified)");

    // request
//    CommandOptionMulti<Long, DCFStoredGui<?>> REQUEST = multi("request_id", "The id of the request", OptionType.INTEGER,
//        OptionMapping::getAsLong, ActiveRequestDatabase.get()::getRequest);
    CommandOption<Attachment> LOAN_COLLATERAL_IMAGE = basic("image", "Image of the collateral to add to the request",
        OptionType.ATTACHMENT, OptionMapping::getAsAttachment);
    CommandOption<Attachment> ITEM_IMAGE = basic("image", "Image of the item",
        OptionType.ATTACHMENT, OptionMapping::getAsAttachment);

    // help
    CommandOptionMulti<String, HelpCommandListType> HELP_LIST_TYPE = new CommandOptionMapEnum<>("help_list", "The type of help list",
        HelpCommandListType.class, HelpCommandListType.values());
    CommandOptionMulti<String, Emeralds> PRICE = CommandOption.emeraldsAmount("rent per week");


    @NotNull
    static CommandOptionMulti<String, Emeralds> emeraldsAmount(String type) {
        String desc = "The amount to %s. %s".formatted(type, ErrorMessages.emeraldsFormat());
        return new CommandOptionEmeralds("amount", desc, OptionType.STRING);
    }


    static <V, R> CommandOptionMulti<V, R> multi(String name, String description, OptionType type,
        Function<OptionMapping, V> mapping1, Function<V, R> mapping2) {
        return new CommandOptionMulti<>(name, description, type, mapping1, mapping2);
    }

    private static <R> CommandOptionBasic<R> basic(String name, String description, OptionType type,
        Function<OptionMapping, R> getOption) {
        return new CommandOptionBasic<>(name, description, type, getOption);
    }

    R getOptional(CommandInteraction event, R fallback);

    @Nullable
    default R getOptional(CommandInteraction event) {
        return this.getOptional(event, null);
    }

    default BlazingMessage getErrorMessage(CommandInteraction event) {
        return ErrorMessages.missingOption(getOptionName());
    }

    default R getRequired(CommandInteraction event) {
        return getRequired(event, getErrorMessage(event));
    }

    default R getRequired(CommandInteraction event, BlazingMessage errorMsg) {
        R result = getOptional(event);
        if (result == null) errorMsg.replyError(event);
        return result;
    }

    String getOptionName();

    default void addOption(SubcommandData command) {
        this.addOption(command, false);
    }

    default void addOption(SlashCommandData command) {
        this.addOption(command, false);
    }

    void addOption(SubcommandData command, boolean required);

    void addOption(SlashCommandData command, boolean required);

    class CommandOptionMapEnum<Enm extends Enum<Enm>> extends CommandOptionMulti<String, Enm> {

        CommandOptionMapEnum(String name, String description, Class<Enm> type, Enm[] values) {
            super(name, description, OptionType.STRING, OptionMapping::getAsString, s -> parseCollateral(type, s));
            addChoices(Stream.of(values)
                .map(Enm::name)
                .map(Pretty::spaceEnumWords)
                .map(c -> new Choice(c, c))
                .toList()
            );
        }

        private static <E extends Enum<E>> E parseCollateral(Class<E> type, String s) {
            try {
                return Enum.valueOf(type, s.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
