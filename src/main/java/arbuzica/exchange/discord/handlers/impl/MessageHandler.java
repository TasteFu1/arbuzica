package arbuzica.exchange.discord.handlers.impl;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import arbuzica.exchange.Instance;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.handlers.IHandler;
import lombok.Getter;

@Getter
public class MessageHandler implements IHandler {
    private MessageReceivedEvent event;

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof MessageReceivedEvent eventIn)) {
            return;
        }

        this.event = eventIn;

        for (Command command : Instance.get().getCommandHandler().getCommandList()) {
            command.execute(this);
        }
    }
}
