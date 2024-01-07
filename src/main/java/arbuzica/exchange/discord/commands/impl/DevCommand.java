package arbuzica.exchange.discord.commands.impl;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;

public class DevCommand extends Command {
    /*** constructor  ***/
    public DevCommand() {
        super(Commands.slash("dev", "param"));
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        accountRepository.deleteAll();
        handler.getEvent().reply("qq").queue();
    }

    @Override
    public void execute(ButtonHandler handler) {

    }

    @Override
    public void execute(ModalHandler handler) {

    }

    @Override
    public void execute(SelectMenuHandler handler) {

    }
}
