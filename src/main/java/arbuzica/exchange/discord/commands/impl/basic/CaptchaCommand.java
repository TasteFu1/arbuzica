package arbuzica.exchange.discord.commands.impl.basic;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import arbuzica.exchange.database.entities.Account;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.MessageHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import arbuzica.exchange.discord.override.Callback;
import arbuzica.exchange.utilities.discord.HandlerUtility;

public class CaptchaCommand extends Command {
    /*** constructor
     * @param commandHandler
     * @param data***/
    public CaptchaCommand(CommandHandler commandHandler, SlashCommandData data) {
        super(commandHandler, data);
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        SlashCommandInteractionEvent event = handler.getEvent();

        Account account = accountRepository.findByDiscordId(discordId(handler));
        Callback callback = Callback.builder(handler);

        if (account == null) {
            callback.addEmbeds(error().setDescription("Account not found!\nRun `/user` to create or restore account.").build()).queue();
            return;
        }

        if (account.getCaptcha() == null) {
            callback.addEmbeds(warning().setDescription("Your account has already been verified.").build()).queue();
            return;
        }

        String solution = event.getOption("solution").getAsString();

        if (account.getCaptcha().equals(solution)) {
            UserCommand userCommand = ((UserCommand) commandHandler.getCommand(UserCommand.class));

            account.setCaptcha("verified");
            accountRepository.save(account);

            callback.addEmbeds(success().setDescription("Verification passed!").build()).queue();
            userCommand.getUserPanel().accept(handler);
            return;
        }

        isNotVerified("Try again.", handler);
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
