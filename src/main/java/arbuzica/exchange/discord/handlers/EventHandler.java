package arbuzica.exchange.discord.handlers;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class EventHandler implements EventListener {
    private final List<IHandler> handlers = new ArrayList<>();

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        handlers.forEach(handler -> handler.onEvent(event));
    }
}
