package arbuzica.exchange;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import arbuzica.exchange.database.repositories.AccountRepository;
import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.EventHandler;
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

    /*** handlers ***/
    private final CommandHandler commandHandler;

    /*** constructor ***/
    private Instance(ConfigurableApplicationContext context) throws InterruptedException {
        INSTANCE = this;

        this.accountRepository = context.getBean(AccountRepository.class);

        this.jda = JDABuilder.createDefault(BOT_TOKEN).enableIntents(BOT_INTENTS).addEventListeners(new EventHandler()).build().awaitReady();
        this.commandHandler = CommandHandler.init().upsert();
    }

    public static void start(ConfigurableApplicationContext context) throws InterruptedException {
        new Instance(context);
    }

    public static Instance get() {
        if (INSTANCE == null) {
            throw new RuntimeException("Main class hasn't ever been instantiated");
        }

        return INSTANCE;
    }
}
