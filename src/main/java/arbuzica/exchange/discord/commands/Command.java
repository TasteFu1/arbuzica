package arbuzica.exchange.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.Color;
import java.util.function.Consumer;

import arbuzica.exchange.Instance;
import arbuzica.exchange.blockchain.chains.Litecoin;
import arbuzica.exchange.database.entities.Account;
import arbuzica.exchange.database.repositories.AccountRepository;
import arbuzica.exchange.database.repositories.SerialRepository;
import arbuzica.exchange.database.repositories.TransactionRepository;
import arbuzica.exchange.discord.handlers.IHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.MessageHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import arbuzica.exchange.discord.override.Callback;
import arbuzica.exchange.utilities.discord.HandlerUtility;
import arbuzica.exchange.utilities.java.StringUtility;
import arbuzica.exchange.utilities.misc.CaptchaUtility;
import lombok.Getter;

public abstract class Command {
    /*** application instance ***/
    protected final Instance instance = Instance.get();

    /*** jda instance ***/
    protected final JDA jda = instance.getJda();

    /*** repositories ***/
    protected final AccountRepository accountRepository = instance.getAccountRepository();
    protected final SerialRepository serialRepository = instance.getSerialRepository();
    protected final TransactionRepository transactionRepository = instance.getTransactionRepository();

    /*** colors ***/
    protected final Color INFO_COLOR = new Color(0x80ff);
    protected final Color ERROR_COLOR = new Color(0xFF0042);
    protected final Color SUCCESS_COLOR = new Color(0xff97);
    protected final Color WARNING_COLOR = new Color(0xffab00);

    /*** constants ***/
    @Getter
    private final SlashCommandData data;

    /*** litecoin node ***/
    @Getter
    protected final Litecoin litecoin = new Litecoin();

    protected final CommandHandler commandHandler;

    /*** constructor ***/
    public Command(CommandHandler commandHandler, SlashCommandData data) {
        this.commandHandler = commandHandler;
        this.data = data;
    }

    /*** embeds ***/
    public EmbedBuilder embed() {
        return new EmbedBuilder();
    }

    public EmbedBuilder info() {
        return embed().setTitle("Info").setColor(INFO_COLOR);
    }

    public EmbedBuilder error() {
        return embed().setTitle("Error").setColor(ERROR_COLOR);
    }

    public EmbedBuilder success() {
        return embed().setTitle("Success").setColor(SUCCESS_COLOR);
    }

    public EmbedBuilder warning() {
        return embed().setTitle("Warning").setColor(WARNING_COLOR);
    }

    public Emoji custom(String name, long id) {
        return Emoji.fromCustom(name, id, false);
    }

    public User user(IHandler handler) {
        return HandlerUtility.getUser(handler);
    }

    public String discordId(IHandler handler) {
        return HandlerUtility.getUser(handler).getId();
    }

    public boolean isNotVerified(String message, IHandler handler) {
        Account account = accountRepository.findByDiscordId(discordId(handler));

        if (account.getCaptcha().equals("verified")) {
            return false;
        }

        account.setCaptcha(StringUtility.random(7).toLowerCase());
        accountRepository.save(account);

        Callback callback = Callback.builder(handler);

        FileUpload fileUpload = CaptchaUtility.newCaptcha(String.format("%s.png", account.getId()), account.getCaptcha());
        MessageEmbed embed = warning() //
                .setTitle("Verification") //
                .appendDescription(message) //
                .appendDescription("\n") //
                .appendDescription("/captcha `solution`") //
                .appendDescription("\n") //
                .build();

        callback.addFile(fileUpload);
        callback.addEmbeds(embed);

        callback.queue();
        return true;
    }

    public boolean isNotVerified(IHandler handler) {
        return isNotVerified("Go through the captcha.", handler);
    }


    /*** methods ***/
    public abstract void execute(SlashCommandHandler handler);

    public abstract void execute(ButtonHandler handler);

    public abstract void execute(ModalHandler handler);

    public abstract void execute(SelectMenuHandler handler);

    public abstract void execute(MessageHandler handler);
}
