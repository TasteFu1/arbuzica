package arbuzica.exchange.discord.commands.impl.admin;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import arbuzica.exchange.database.entities.Serial;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.MessageHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import arbuzica.exchange.discord.override.AdvancedButton;
import arbuzica.exchange.utilities.java.FileUtility;

public class GenerateCommand extends Command {
    /*** constructor
     * @param commandHandler
     * @param data***/
    public GenerateCommand(CommandHandler commandHandler, SlashCommandData data) {
        super(commandHandler, data);
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        SlashCommandInteractionEvent event = handler.getEvent();
        long discordId = event.getUser().getIdLong();

        if (discordId != 1174237637700227154L && discordId != 1017719595546718259L && discordId != 1125786717526425640L) {
            event.replyEmbeds(error().setDescription("Insufficent permissions!").build()).queue();
            return;
        }

        int quantity = event.getOption("quantity").getAsInt();
        double credits = event.getOption("credits").getAsDouble();

        List<Serial> serialList = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            serialList.add(Serial.builder().credits(credits).build());
        }

        serialRepository.saveAll(serialList);

        StringBuilder content = new StringBuilder();

        for (Serial serial : serialList) {
            content.append(serial.getCode());
            content.append("\n");
        }

        File file = FileUtility.createFile(String.format("generated_keys_%s_credits_%s.txt", credits, Math.abs(UUID.randomUUID().getLeastSignificantBits())), content.toString());

        MessageEmbed embed = info() //
                .setTitle(String.format("Generated Keys [%s credits]", credits)) //
                .setDescription(String.format("```%n%s```", content)) //
                .setFooter("arbuzica.exchange") //
                .build();

        Button downloadGeneratedKeysButton = AdvancedButton.download() //
                .id("generate_download_generated_keys_button") //
                .values(file.getName()) //
                .build();

        event.replyEmbeds(embed).addActionRow(downloadGeneratedKeysButton).queue();
    }

    @Override
    public void execute(ButtonHandler handler) {
        ButtonInteractionEvent event = handler.getEvent();

        switch (handler.getButtonId()) {
            case "generate_download_generated_keys_button" -> {
                String fileName = handler.getParams().get(0);

                File file = new File(fileName);
                FileUpload fileUpload = FileUpload.fromData(file, fileName);

                event.replyFiles(fileUpload).queue();
            }
        }
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
