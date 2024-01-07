package arbuzica.exchange.discord.commands.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;
import java.util.function.Consumer;

import arbuzica.exchange.database.entities.Account;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.handlers.IHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import arbuzica.exchange.discord.override.AdvancedButton;
import arbuzica.exchange.discord.override.Callback;
import arbuzica.exchange.discord.override.ValueModal;
import arbuzica.exchange.utilities.discord.HandlerUtility;
import arbuzica.exchange.utilities.java.StringUtility;

public class UserCommand extends Command {
    /*** constructor  ***/
    public UserCommand() {
        super(Commands.slash("user", "param"));
    }

    private final Consumer<IHandler> userPanel = handler -> {
        Callback callback = Callback.builder(handler);
        Account account = HandlerUtility.getAccount(handler);

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
            callback.addEmbeds(info() //
                    .setTitle("User Panel") //
                    .addField("Balance", String.format("%s credits", account.getBalance()), false) //
                    .setFooter("arbuzica.exchange") //
                    .build());

            callback.addActionRow( //
                    AdvancedButton.success().id("user_help_button").label("Help").build(), //
                    AdvancedButton.primary().id("user_recovery_button").label("Recovery").build() //
            );
        }

        callback.queue();
    };

    @Override
    public void execute(SlashCommandHandler handler) {
        userPanel.accept(handler);
    }

    private final Consumer<ButtonHandler> newAccountButton = handler -> {
        Account account = Account.builder() //
                .discordId(HandlerUtility.getDiscordId(handler)) //
                .build();

        accountRepository.save(account);
        userPanel.accept(handler);
    };

    private final Consumer<ButtonHandler> recoveryButton = handler -> {
        Callback callback = Callback.builder(handler);
        Account account = HandlerUtility.getAccount(handler);

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
                AdvancedButton.success().id("user_regen_recovery_code_button").label("Regenerate Recovery Code").build() //
        );

        callback.queue();
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

    @Override
    public void execute(ButtonHandler handler) {
        switch (handler.getButtonId()) {
            case "user_new_acc_button" -> newAccountButton.accept(handler);

            case "user_recovery_acc_button" -> recoveryAccountButton.accept(handler);

            case "user_user_panel_button" -> userPanel.accept(handler);

            case "user_recovery_button" -> recoveryButton.accept(handler);

            case "user_help_button" -> helpButton.accept(handler);
        }
    }

    private final Consumer<ModalHandler> recoveryAccountModal = handler -> {
        ModalInteractionEvent event = handler.getEvent();
        String value = event.getValues().get(0).getAsString();

        Callback callback = Callback.builder(handler);
        Account account = accountRepository.findByRecoveryCode(value);

        if (account == null) {
            callback.addEmbeds(error().setDescription("Account not found!").build()).queue();
            return;
        }

        account.setDiscordId(event.getUser().getId());
        account.setRecoveryCode(StringUtility.randomCode(4));

        accountRepository.save(account);
        userPanel.accept(handler);
    };

    @Override
    public void execute(ModalHandler handler) {
        switch (handler.getModalId()) {
            case "user_recovery_acc_modal" -> recoveryAccountModal.accept(handler);
        }
    }

    @Override
    public void execute(SelectMenuHandler handler) {

    }
}
