package arbuzica.exchange.discord.commands.impl.basic;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.function.Consumer;

import arbuzica.exchange.database.entities.Account;
import arbuzica.exchange.database.entities.Serial;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.IHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.MessageHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import arbuzica.exchange.discord.override.AdvancedButton;
import arbuzica.exchange.discord.override.Callback;
import arbuzica.exchange.discord.override.ValueModal;
import arbuzica.exchange.utilities.discord.HandlerUtility;
import arbuzica.exchange.utilities.java.StringUtility;
import arbuzica.exchange.utilities.misc.CaptchaUtility;

public class RedeemCommand extends Command {
    /*** constructor ***/
    public RedeemCommand(CommandHandler commandHandler, SlashCommandData data) {
        super(commandHandler, data);
    }

    private UserCommand userCommand() {
        return (UserCommand) commandHandler.getCommand(UserCommand.class);
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        Account account = accountRepository.findByDiscordId(discordId(handler));
        Callback callback = Callback.builder(handler);

        if (account == null) {
            callback.addEmbeds(error().setDescription("Account not found!\nRun `/user` to create or restore account.").build()).queue();
            return;
        }

        if (isNotVerified(handler)) {
            return;
        }

        String key = handler.getEvent().getOption("key").getAsString();
        Serial serial = serialRepository.findByCode(key);

        if (serial == null) {
            callback.addEmbeds(error().setDescription("Key not found!").build()).queue();
            return;
        }

        if (serial.isRedeemed()) {
            callback.addEmbeds(error().setDescription("Key has already been used!").build()).queue();
            return;
        }

        serial.setRedeemerId(account.getId());
        serial.setUsageDate(System.currentTimeMillis());
        serialRepository.save(serial);

        account.setCredits(account.getCredits() + serial.getCredits());
        accountRepository.save(account);

        callback.addEmbeds(success().setTitle("Redeem").setDescription(String.format("%s credits have been added to your account.", serial.getCredits())).build()).queue();
        userCommand().getUserPanel().accept(handler);
    }

    @Override
    public void execute(ButtonHandler handler) {

    }

    @Override
    public void execute(ModalHandler handler) {

    }

    @Override
    public void execute(SelectMenuHandler handler) {

    }

    @Override
    public void execute(MessageHandler handler) {

    }
}
