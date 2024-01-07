package arbuzica.exchange.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.Color;
import java.util.function.Consumer;

import arbuzica.exchange.Instance;
import arbuzica.exchange.database.repositories.AccountRepository;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import lombok.Getter;

public abstract class Command {
    /*** application instance ***/
    protected final Instance instance = Instance.get();

    /*** jda instance ***/
    protected final JDA jda = instance.getJda();

    /*** repositories ***/
    protected final AccountRepository accountRepository = instance.getAccountRepository();

    /*** colors ***/
    protected final Color INFO_COLOR = new Color(0x80ff);
    protected final Color ERROR_COLOR = new Color(0xFF0042);
    protected final Color SUCCESS_COLOR = new Color(0xff97);
    protected final Color WARNING_COLOR = new Color(0xffab00);

    /*** constants ***/
    @Getter
    private final SlashCommandData data;

    /*** constructor ***/
    public Command(SlashCommandData data) {
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

    /*** methods ***/
    public abstract void execute(SlashCommandHandler handler);

    public abstract void execute(ButtonHandler handler);

    public abstract void execute(ModalHandler handler);

    public abstract void execute(SelectMenuHandler handler);
}
