package arbuzica.exchange;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import arbuzica.exchange.discord.commands.CommandHandler;
import arbuzica.exchange.discord.handlers.EventHandler;
import lombok.Getter;

@Getter
public class Instance {
    /*** discord api instance ***/
    private JDA jda;

    /*** discord bot values ***/
    private final String CLIENT_ID = "";
    private final String CLIENT_SECRET = "";
    private final String BOT_TOKEN = "";
    private final List<GatewayIntent> BOT_INTENTS = List.of(GatewayIntent.MESSAGE_CONTENT, //
            GatewayIntent.GUILD_MEMBERS, //
            GatewayIntent.GUILD_MODERATION, //
            GatewayIntent.GUILD_MESSAGES, //
            GatewayIntent.DIRECT_MESSAGES);

    /*** handlers ***/
    private CommandHandler commandHandler;

    public void initialize(ConfigurableApplicationContext context) throws InterruptedException {
        this.jda = JDABuilder.createDefault(BOT_TOKEN) //
                .enableIntents(BOT_INTENTS) //
                .addEventListeners(new EventHandler()) //
                .build() //
                .awaitReady();

        this.commandHandler = CommandHandler.builder() //
                .build(jda);
    }

    private enum Singleton {
        APPLICATION;

        private final Instance value;

        Singleton() {
            this.value = new Instance();
        }
    }

    public static Instance get() {
        return Singleton.APPLICATION.value;
    }
}
