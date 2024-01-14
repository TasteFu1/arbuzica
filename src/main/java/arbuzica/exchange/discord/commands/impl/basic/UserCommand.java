package arbuzica.exchange.discord.commands.impl.basic;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.List;
import java.util.function.Consumer;

import arbuzica.exchange.database.entities.Account;
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
import lombok.Getter;

public class UserCommand extends Command {
    /*** constructor  ***/
    public UserCommand(CommandHandler commandHandler, SlashCommandData data) {
        super(commandHandler, data);
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        userPanel.accept(handler);
    }

    @Override
    public void execute(ButtonHandler handler) {
        switch (handler.getButtonId()) {
            case "user_new_acc_button" -> newAccountButton.accept(handler);

            case "user_recovery_acc_button" -> recoveryAccountButton.accept(handler);

            case "user_user_panel_button" -> userPanel.accept(handler);

            case "user_recovery_button" -> recoveryButton.accept(handler);

            case "user_help_button" -> helpButton.accept(handler);

            case "user_regen_recovery_code_button" -> regenRecoveryButton.accept(handler);

            case "user_download_recovery_code_button" -> downloadRecoveryCodeButton.accept(handler);
        }
    }

    @Override
    public void execute(ModalHandler handler) {
        switch (handler.getModalId()) {
            case "user_recovery_acc_modal" -> recoveryAccountModal.accept(handler);
        }
    }

    @Override
    public void execute(SelectMenuHandler handler) {

    }

    @Override
    public void execute(MessageHandler handler) {

    }

    @Getter
    private final Consumer<IHandler> userPanel = handler -> {
        Callback callback = Callback.builder(handler);
        Account account = accountRepository.findByDiscordId(discordId(handler));

        if (account == null) {
            callback.addEmbeds(info() //
                    .setTitle("Sign In") //
                    .setDescription("Select login method.") //
                    .addField("New Account", "Register a new account that will be linked to your discord account.", false) //
                    .addField("Recovery", "Link an existing account to your discord account.", false) //
                    .setFooter("arbuzica.exchange") //
                    .build());

            callback.addActionRow( //
                    AdvancedButton.success().id("user_new_acc_button").label("New Account").build(), //
                    AdvancedButton.primary().id("user_recovery_acc_button").label("Recovery").build() //
            );

        } else {
            if (isNotVerified(handler)) {
                return;
            }

            callback.addEmbeds(info() //
                    .setTitle("User Panel") //
                    .addField("Balance", String.format("%s credits", account.getCredits()), false) //
                    .setFooter("arbuzica.exchange") //
                    .build());

            callback.addActionRow( //
                    AdvancedButton.success().id("user_help_button").label("Help").build(), //
                    AdvancedButton.primary().id("user_recovery_button").label("Recovery").build() //
            );
        }

        callback.queue();
    };

    private final Consumer<ButtonHandler> newAccountButton = handler -> {
        String discordId = discordId(handler);

        Account account = accountRepository.findByDiscordId(discordId);
        Callback callback = Callback.builder(handler);

        if (account != null) {
            callback.addEmbeds(error().setDescription("Account is already exists!").build()).queue();
            return;
        }

        account = Account.builder().discordId(discordId).build();
        accountRepository.save(account);

        userPanel.accept(handler);
    };

    private final Consumer<ButtonHandler> recoveryAccountButton = handler -> {
        ButtonInteractionEvent event = handler.getEvent();

        TextInput textInput = TextInput.create("user_recovery_acc_input", "Code", TextInputStyle.SHORT) //
                .setPlaceholder("XXXXX-XXXXX-XXXXX-XXXXX") //
                .setRequiredRange(23, 23) //
                .build();

        Modal modal = ValueModal.create("user_recovery_acc_modal", "Recovery") //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    };

    private final Consumer<ButtonHandler> recoveryButton = handler -> {
        Callback callback = Callback.builder(handler);
        Account account = accountRepository.findByDiscordId(discordId(handler));

        callback.addEmbeds(info() //
                .setTitle("Recovery") //
                .setDescription("""
                        Use this code to restore your account or link to another discord account.
                        **Do not share this code with anyone!**""") //
                .addField("Recovery Code", String.format("||%s||", account.getRecoveryCode()), false) //
                .setFooter("arbuzica.exchange") //
                .build());

        callback.addActionRow( //
                AdvancedButton.back().id("user_user_panel_button").build(), //
                AdvancedButton.download().id("user_download_recovery_code_button").build(), //
                AdvancedButton.success().id("user_regen_recovery_code_button").label("Regenerate Recovery Code").build() //
        );

        callback.queue();
    };

    private final Consumer<ButtonHandler> helpButton = handler -> {
        Callback callback = Callback.builder(handler);

        callback.addEmbeds(info() //
                .setTitle("Help") //
                .addField("/user", "User Panel.", false) //
                .addField("/redeem", "Deposit funds by activating key.", false) //
                .addField("/exchange", "Withdraw credits to ltc.", false) //
                .setFooter("arbuzica.exchange") //
                .build());

        callback.addActionRow( //
                AdvancedButton.back().id("user_user_panel_button").build() //
        );

        callback.queue();
    };

    private final Consumer<ModalHandler> recoveryAccountModal = handler -> {
        ModalInteractionEvent event = handler.getEvent();
        String value = event.getValues().get(0).getAsString();

        Callback callback = Callback.builder(handler);
        Account account = accountRepository.findByRecoveryCode(value);

        if (account == null) {
            callback.addEmbeds(error().setDescription("Account not found!").build()).queue();
            return;
        }

        String discordId = event.getUser().getId();

        if (accountRepository.findByDiscordId(discordId) != null) {
            callback.addEmbeds(error().setDescription("Account is already linked!").build()).queue();
            return;
        }

        account.setDiscordId(discordId);
        account.setRecoveryCode(StringUtility.randomCode(4));

        accountRepository.save(account);
        userPanel.accept(handler);
    };

    private final Consumer<ButtonHandler> regenRecoveryButton = handler -> {
        Account account = accountRepository.findByDiscordId(discordId(handler));

        account.setRecoveryCode(StringUtility.randomCode(4));
        accountRepository.save(account);

        recoveryButton.accept(handler);
    };

    private final Consumer<ButtonHandler> downloadRecoveryCodeButton = handler -> {
        Account account = accountRepository.findByDiscordId(discordId(handler));
        FileUpload fileUpload = FileUpload.fromData(account.getRecoveryCode().getBytes(), "recovery.txt");

        Callback.builder(handler).addFile(fileUpload).queue();
    };
}
