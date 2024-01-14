package arbuzica.exchange.discord.commands.impl.dev;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.MessageHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import arbuzica.exchange.discord.override.Callback;

public class AdminCommand extends Command {
    /*** constructor  ***/
    public AdminCommand(CommandHandler commandHandler, SlashCommandData data) {
        super(commandHandler, data);
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        SlashCommandInteractionEvent event = handler.getEvent();
        Callback callback = Callback.builder(handler);

        long discordId = event.getUser().getIdLong();

        if (discordId != 1174237637700227154L && discordId != 1017719595546718259L && discordId != 1125786717526425640L) {
            callback.addEmbeds(error().setDescription("Insufficent permissions!").build()).queue();
            return;
        }

        JsonObject listUnspent = JsonParser.parseString(litecoin.listUnspent()).getAsJsonObject();
        StringBuilder address = new StringBuilder();

        for (JsonElement jsonElement : listUnspent.get("result").getAsJsonArray()) {
            address.append(jsonElement.getAsJsonObject().get("address").getAsString());
            address.append("\n");
        }

        double walletBalance = litecoin.getWalletBalance();

        MessageEmbed embed = info() //
                .setTitle("Admin Panel") //
                .setDescription(String.format("Hello %s.", event.getUser().getName())) //
                .addField("Address", address.toString(), false) //
                .addField("Balance", String.format("In LTC: %s%nIn USD: %s", walletBalance, litecoin.getRateToUSD() * walletBalance), false) //
                .build();

        callback.addEmbeds(embed).queue();
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
