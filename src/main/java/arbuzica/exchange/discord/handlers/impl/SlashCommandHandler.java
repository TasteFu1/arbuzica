package arbuzica.exchange.discord.handlers.impl;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import arbuzica.exchange.Instance;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.handlers.IHandler;
import lombok.Getter;


@Getter
public class SlashCommandHandler implements IHandler {
    private SlashCommandInteractionEvent event;

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof SlashCommandInteractionEvent eventIn)) {
            return;
        }

        Command command = Instance.get().getCommandHandler().getCommand(eventIn.getName());

        if (command == null) {
            return;
        }

        this.event = eventIn;
        command.execute(this);
    }
}
