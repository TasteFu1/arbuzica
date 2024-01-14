package arbuzica.exchange.discord.commands.impl.admin;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.List;

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
import arbuzica.exchange.utilities.java.FileUtility;
import arbuzica.exchange.utilities.java.StringUtility;

public class StockCommand extends Command {
    /*** constructor
     * @param commandHandler
     * @param data***/
    public StockCommand(CommandHandler commandHandler, SlashCommandData data) {
        super(commandHandler, data);
    }

    private Callback sendStock(IHandler handler, int page, Boolean redeemed) {
        List<Serial> serialList = serialRepository.findAll();

        if (redeemed != null) {
            if (redeemed) {
                serialList = serialList.stream().filter(serial -> serial.getUsageDate() > 0).toList();
            } else {
                serialList = serialList.stream().filter(serial -> serial.getUsageDate() == 0).toList();
            }
        }

        StringBuilder serialString = new StringBuilder();
        StringBuilder redeemerIdString = new StringBuilder();
        StringBuilder usageDateString = new StringBuilder();

        StringBuilder codeString = new StringBuilder();
        StringBuilder detailedString = new StringBuilder();

        int step = 15;
        int maxPage = (int) Math.ceil(((double) serialList.size()) / step) - 1;

        for (Serial serial : serialList.subList(step * page, Math.min(step * page + step, serialList.size()))) {
            String redeemerId = serial.getRedeemerId().isEmpty() ? "unused" : serial.getRedeemerId();
            String usageDate = serial.getUsageDate() == 0 ? "unused" : StringUtility.timestamp(serial.getUsageDate());

            serialString.append(String.format("`%s [%s]`", serial.getCode(), serial.getCredits()));
            serialString.append("\n");

            redeemerIdString.append(String.format("`%s`", redeemerId));
            redeemerIdString.append("\n");

            usageDateString.append(String.format("`%s`", usageDate));
            usageDateString.append("\n");

            codeString.append(serial.getCode());
            codeString.append("\n");

            detailedString.append(String.format("%s - %s - %s - %s", serial.getCode(), serial.getCredits(), redeemerId, usageDate));
            detailedString.append("\n");
        }

        StringBuilder codeAllString = new StringBuilder();
        StringBuilder detailedAllString = new StringBuilder();

        for (Serial serial : serialList) {
            String redeemerId = serial.getRedeemerId().isEmpty() ? "unused" : serial.getRedeemerId();
            String usageDate = serial.getUsageDate() == 0 ? "unused" : StringUtility.timestamp(serial.getUsageDate());

            codeAllString.append(serial.getCode());
            codeAllString.append("\n");

            detailedAllString.append(String.format("%s - %s - %s - %s", serial.getCode(), serial.getCredits(), redeemerId, usageDate));
            detailedAllString.append("\n");
        }

        FileUtility.createFile("stock_current_code.txt", codeString.toString());
        FileUtility.createFile("stock_current_detailed.txt", detailedString.toString());
        FileUtility.createFile("stock_all_code.txt", codeAllString.toString());
        FileUtility.createFile("stock_all_detailed.txt", detailedAllString.toString());

        Callback callback = Callback.builder(handler);

        MessageEmbed embed = info().setTitle("Stock") //
                .addField("Serial [credits]", serialString.toString(), true) //
                .addField("Redeemer Id", redeemerIdString.toString(), true) //
                .addField("Usage Date", usageDateString.toString(), true) //
                .setFooter(String.format("%s/%s", page + 1, maxPage + 1)) //
                .build();

        Button prevPageButton = AdvancedButton.primary().id("stock_page_button").label("Prev Page").values(String.valueOf(page - 1), boolToString(redeemed)).disabled(page <= 0).build();
        Button nextPageButton = AdvancedButton.primary().id("stock_page_button").label("Next Page").values(String.valueOf(page + 1), boolToString(redeemed)).disabled(page >= maxPage).build();
        Button downloadButton = AdvancedButton.download().id("stock_download_button").build();

        callback.addActionRow(prevPageButton, nextPageButton);
        callback.addActionRow(downloadButton);
        callback.addEmbeds(embed);

        return callback;
    }

    private Boolean parseBool(String value) {
        return value == null || value.equals("null") ? null : Boolean.parseBoolean(value);
    }

    private String boolToString(Boolean bool) {
        return bool == null ? "null" : bool.toString();
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        SlashCommandInteractionEvent event = handler.getEvent();
        long discordId = event.getUser().getIdLong();

        if (discordId != 1174237637700227154L && discordId != 1017719595546718259L && discordId != 1125786717526425640L) {
            event.replyEmbeds(error().setDescription("Insufficent permissions!").build()).queue();
            return;
        }

        Boolean redeemed = null;
        OptionMapping redeemedOp = event.getOption("redeemed");

        if (redeemedOp != null) {
            redeemed = redeemedOp.getAsBoolean();
        }

        sendStock(handler, 0, redeemed).queue();
    }

    @Override
    public void execute(ButtonHandler handler) {
        ButtonInteractionEvent event = handler.getEvent();
        List<String> params = handler.getParams();

        switch (handler.getButtonId()) {
            case "stock_page_button" -> {
                sendStock(handler, Integer.parseInt(params.get(0)), parseBool(params.get(1))).queue();
            }

            case "stock_download_button" -> {
                if (params.isEmpty()) {
                    Button curPageButton = AdvancedButton.download().label("Current Page").id("stock_download_button").values("current").build();
                    Button allButton = AdvancedButton.download().label("All").id("stock_download_button").values("all").build();

                    event.replyEmbeds(info().setTitle("Stock").setDescription("Select a filter.").build()) //
                            .addActionRow(curPageButton, allButton) //
                            .queue();
                    return;
                }

                if (params.size() == 1) {
                    Button codeOnlyButton = AdvancedButton.download().label("Code Only").id("stock_download_button").values(params.get(0), "code").build();
                    Button detailedButton = AdvancedButton.download().label("Detailed").id("stock_download_button").values(params.get(0), "detailed").build();

                    event.replyEmbeds(info().setTitle("Stock").setDescription("Select a filter.").build()) //
                            .addActionRow(codeOnlyButton, detailedButton) //
                            .queue();
                    return;
                }

                String fileName = String.format("stock_%s_%s.txt", params.get(0), params.get(1));

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
