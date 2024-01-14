package arbuzica.exchange;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import arbuzica.exchange.database.repositories.AccountRepository;
import arbuzica.exchange.database.repositories.SerialRepository;
import arbuzica.exchange.database.repositories.TransactionRepository;
import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.EventHandler;
import arbuzica.exchange.utilities.LitecoinUtility;
import arbuzica.exchange.utilities.task.TaskManager;
import arbuzica.exchange.utilities.task.impl.RequestRateTask;
import arbuzica.exchange.utilities.task.impl.RequestSatoshiTask;
import lombok.Getter;

@Getter
public class Instance {
    /*** instance ***/
    private static Instance INSTANCE;

    /*** discord api ***/
    private final JDA jda;

    /*** discord bot values ***/
    private final String CLIENT_ID = "1191747188389335111";
    private final String CLIENT_SECRET = "MoE2Ejzi3yT6ENxGgsIHuJWOi4lB39h0";
    private final String BOT_TOKEN = "MTE5MTc0NzE4ODM4OTMzNTExMQ.Ge-f5i.tL0XgqsQ0dSfbR_1klVedSm6LY4z0ZSHlMP--s";
    private final List<GatewayIntent> BOT_INTENTS = List.of(GatewayIntent.MESSAGE_CONTENT, //
            GatewayIntent.GUILD_MEMBERS, //
            GatewayIntent.GUILD_MODERATION, //
            GatewayIntent.GUILD_MESSAGES, //
            GatewayIntent.DIRECT_MESSAGES);

    /*** repositories ***/
    private final AccountRepository accountRepository;
    private final SerialRepository serialRepository;
    private final TransactionRepository transactionRepository;

    /*** handlers ***/
    private final CommandHandler commandHandler;

    /*** task manager ***/
    private final TaskManager taskManager;

    /*** constructor ***/
    public Instance(ConfigurableApplicationContext context) throws InterruptedException {
        INSTANCE = this;

        this.accountRepository = context.getBean(AccountRepository.class);
        this.serialRepository = context.getBean(SerialRepository.class);
        this.transactionRepository = context.getBean(TransactionRepository.class);

        this.jda = JDABuilder.createDefault(BOT_TOKEN).enableIntents(BOT_INTENTS).addEventListeners(new EventHandler()).build().awaitReady();
        this.commandHandler = new CommandHandler(this.jda);

        this.taskManager = new TaskManager() //
                .newTask(new RequestSatoshiTask(TimeUnit.MINUTES.toMillis(5))) //
                .newTask(new RequestRateTask(TimeUnit.MINUTES.toMillis(5))) //
                .tick();
    }

    public static Instance get() {
        if (INSTANCE == null) {
            throw new RuntimeException("Main class hasn't ever been instantiated");
        }

        return INSTANCE;
    }
}
