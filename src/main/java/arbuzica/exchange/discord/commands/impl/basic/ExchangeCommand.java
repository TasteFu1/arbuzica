package arbuzica.exchange.discord.commands.impl.basic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import arbuzica.exchange.database.entities.Account;
import arbuzica.exchange.database.entities.Transaction;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.MessageHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import arbuzica.exchange.discord.override.Callback;
import arbuzica.exchange.utilities.LitecoinUtility;
import arbuzica.exchange.utilities.discord.HandlerUtility;
import arbuzica.exchange.utilities.java.StringUtility;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExchangeCommand extends Command {
    /*** constructor
     * @param commandHandler
     * @param data***/
    public ExchangeCommand(CommandHandler commandHandler, SlashCommandData data) {
        super(commandHandler, data);
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

        SlashCommandInteractionEvent event = handler.getEvent();

        String address = event.getOption("address").getAsString();
        double credits = event.getOption("credits").getAsDouble();

        double rate = litecoin.getRateToUSD();
        double satoshiPerByte = litecoin.getSatoshiPerByte();

        double dynamicFeePercent = 10;

        double fee = 144 * satoshiPerByte * 0.00000001;
        double amount = (1.0 / rate) * (credits - fee * rate - credits * (dynamicFeePercent / 100));

        String send = litecoin.send(address, amount, satoshiPerByte);
        String txId = JsonParser.parseString(send).getAsJsonObject().get("result").getAsJsonObject().get("txid").getAsString();

        Transaction transaction = transactionRepository.save(Transaction.builder() //
                .accountId(account.getId()) //
                .date(System.currentTimeMillis()) //
                .fee(fee) //
                .address(address) //
                .transactionId(txId) //
                .amount(amount) //
                .rate(rate) //
                .build());

        callback.addEmbeds(info() //
                .setTitle("Transaction Sent") //
                .addField("Date", StringUtility.timestamp("MM/dd/yyyy h:mm a", transaction.getDate()), true) //
                .addField("Amount", String.format("%s | %.2f$", transaction.getAmount(), transaction.getAmount() * transaction.getRate()), true) //
                .addField("Rate", String.format("1 LTC | %.2f", transaction.getRate()), true)
                .addBlankField(true) //
                .addField("To", transaction.getAddress(), false) //
                .addField("Transaction ID", String.format("[%s](https://blockchair.com/litecoin/transaction/%s)", transaction.getTransactionId(), transaction.getTransactionId()), false) //
                .build());

        Message message = callback.complete();

        ScheduledExecutorService scheduledThreadPoolExecutor = Executors.newSingleThreadScheduledExecutor();

        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            JsonObject verbose = JsonParser.parseString(litecoin.getTransaction(transaction.getTransactionId())).getAsJsonObject();
            int confirmations = verbose.get("result").getAsJsonObject().get("confirmations").getAsInt(); // кол-во подтверждений у транзакции

            if(confirmations > 0){
                // message ... обновить кол-во подтверждений в сообщении
                scheduledThreadPoolExecutor.shutdown(); // перестать смотреть за транзакцией
            }
        }, 0, 10, TimeUnit.SECONDS); // смотреть за транзакцией каждые 10 секунд
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
