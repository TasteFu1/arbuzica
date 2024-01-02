package arbuzica.exchange.discord.handlers.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.function.Consumer;

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

        Command command = Instance.get().getCommandHandler().getCommandByName(eventIn.getName());

        if (command == null) {
            return;
        }

        this.event = eventIn;
        command.execute(this);
    }
}
