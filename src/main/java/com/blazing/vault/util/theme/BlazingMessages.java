package com.blazing.vault.util.theme;

import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.discord.DiscordModule;
import com.blazing.vault.discord.base.command.SendMessage;
import com.blazing.vault.util.emerald.Emeralds;
import com.blazing.vault.util.theme.BlazingAssets.BlazingEmoji;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.helpers.CheckReturnValue;

public class BlazingMessages {

    public static final String NULL_MSG = "N/A";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("LLL dd yyyy")
        .withZone(DiscordModule.TIME_ZONE);

    public static String formatDate(Instant date) {
        return formatDate(date, false);
    }

    public static String formatDate(Instant date, boolean emoji) {
        String dateFormatted = DATE_FORMATTER.format(date);
        if (emoji) return BlazingEmoji.ANY_DATE + " " + dateFormatted;
        return dateFormatted;
    }

    public static String formatPercentage(double perc) {
        return "%.2f%%".formatted(perc * 100);
    }

    public static BlazingMessage stringMessage(String msg) {
        return new BlazingStringMessage(msg);
    }

    public static class ErrorMessages {

        @CheckReturnValue
        private static BlazingMessage error(String msg) {
            return new BlazingStringMessage(msg);
        }

        @CheckReturnValue
        private static BlazingCreateMessage error(MessageCreateData msg) {
            return new BlazingCreateMessage(msg);
        }

        @CheckReturnValue
        public static BlazingMessage invalidOption(String optionName, Object optionValue) {
            return error("Invalid %s. '%s' was provided".formatted(optionName, optionValue));
        }

        @CheckReturnValue
        public static BlazingMessage badRole(String requiredRole, CommandInteraction event) {
            String commandName = event.getFullCommandName();
            return error(String.format("You must be a %s to run '/%s'", requiredRole, commandName));
        }


        @CheckReturnValue
        public static BlazingMessage registerWithStaff() {
            return error("To register your account use **/request account** and fill in your Minecraft "
                + "username.");
        }

        @CheckReturnValue
        public static BlazingMessage missingOption(String option) {
            String msg = String.format("'%s' is required, but was not provided", option);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage rejectedTOSRequest(String type) {
            String desc =
                ("Since TOS was rejected, your %s request cannot be accepted"
                    + " and was deleted. Staff have not been notified of your request.")
                    .formatted(type);
            MessageEmbed embed = new EmbedBuilder()
                .setColor(BlazingColor.RED)
                .setTitle("Rejected TOS")
                .setDescription(desc)
                .build();
            return error(MessageCreateData.fromEmbeds(embed));
        }


        @CheckReturnValue
        public static BlazingMessage cannotDeleteProfile(DClient client) {
            String msg = String.format("Cannot delete %s's profile. There are entries associated with their account",
                client.getDisplayName());
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage noRequestWithId(Long requestId) {
            String msg = "There is no request with id '%d'!".formatted(requestId);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage noCollateralWithId(long requestId, long id) {
            String msg = "There is no collateral in request %s %d with id '%d'!"
                .formatted(BlazingEmoji.KEY_ID_CHANGES, requestId, id);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage badRequestType(String type, Long requestId) {
            String msg = "Request #%d is not a %s request".formatted(requestId, type);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage amountNotPositive(Emeralds amount) {
            String msg = "Provided amount: %s is not positive!".formatted(amount);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage paymentTooMuch(Emeralds balance, Emeralds payment) {
            String msg = "Cannot pay back %s. You only owe %s!".formatted(payment, balance);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage onlyLoans() {
            return error("You do not have any active loans!");
        }

        @CheckReturnValue
        public static BlazingMessage notCorrectClient(DClient client) {
            String msg = "This is %s's!".formatted(client.getEffectiveName());
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage onlyInvestments(Emeralds balance) {
            String msg = "You cannot make an investment since you owe %s!".formatted(balance.negative());
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage withdrawalTooMuch(Emeralds withdrawal, Emeralds balance) {
            String msg = "Cannot withdrawal back %s. You only have %s!".formatted(withdrawal, balance);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage blacklisted() {
            String msg = "You're blacklisted and can no longer interact with the bot";
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage alteredAlready(String action) {
            String msg = "Action is already %s".formatted(action);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage dateParseError(String given, String expected) {
            String msg = "Failed to parse date. %s is not in the format %s".formatted(given, expected);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage uploadSizeTooLarge(int maxMB, double actualUploadMB) {
            String msg = "Upload size too large! %.2fMB is greater than the %dMB limit!".formatted(actualUploadMB, maxMB);
            return error(msg);
        }

        @CheckReturnValue
        public static BlazingMessage onlyUploadImages() {
            String msg = "Images are the only filetype allowed!";
            return error(msg);
        }

        public static BlazingMessage textTooLong(int length, int maxLength, String arg) {
            String msg = "%s is too long! %d characters is greater than the %d limit".formatted(arg, length, maxLength);
            return error(msg);
        }

        public static BlazingMessage emeraldsFormat() {
            String msg = "Use the format \"23 STX 12 LE 8 EB 56 E\" or \"12.75 STX\".";
            return error(msg);
        }

    }

    private record BlazingStringMessage(String msg) implements BlazingMessage, SendMessage {

        @Override
        public void replyError(CommandInteraction event) {
            replyError(event, msg);
        }

        @Override
        public MessageCreateData createMsg() {
            return MessageCreateData.fromContent(msg);
        }

        @Override
        public String toString() {
            return this.msg;
        }
    }

    private record BlazingCreateMessage(MessageCreateData msg) implements BlazingMessage, SendMessage {

        @Override
        public void replyError(CommandInteraction event) {
            replyError(event, msg);
            msg.close();
        }

        @Override
        public MessageCreateData createMsg() {
            return msg;
        }

        @Override
        public String toString() {
            return this.msg.getContent();
        }
    }
}
