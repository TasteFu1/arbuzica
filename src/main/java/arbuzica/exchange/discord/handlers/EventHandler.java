package arbuzica.exchange.discord.handlers;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;
import lombok.Getter;

@Getter
public class EventHandler implements EventListener {
    private final List<IHandler> handlers = new ArrayList<>();

    public EventHandler() {
        handlers.add(new SlashCommandHandler());
        handlers.add(new ButtonHandler());
        handlers.add(new ModalHandler());
        handlers.add(new SelectMenuHandler());
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        handlers.forEach(handler -> handler.onEvent(event));
    }
}
