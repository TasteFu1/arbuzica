package arbuzica.exchange.discord.commands.impl.temp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import arbuzica.exchange.database.entities.Serial;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.MessageHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import arbuzica.exchange.discord.override.AdvancedButton;
import arbuzica.exchange.discord.override.Callback;
import arbuzica.exchange.utilities.LitecoinUtility;
import arbuzica.exchange.utilities.misc.CaptchaUtility;
import arbuzica.exchange.utilities.java.StringUtility;

public class TestCommand extends Command {
    /*** constructor  ***/
    public TestCommand(CommandHandler commandHandler, SlashCommandData data) {
        super(commandHandler, data);
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        accountRepository.deleteAll();
        serialRepository.deleteAll();

        handler.getEvent().reply("test").queue();
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
