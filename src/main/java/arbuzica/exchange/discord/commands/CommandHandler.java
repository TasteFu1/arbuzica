package arbuzica.exchange.discord.commands;

import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CommandHandler {
    private final List<Command> commandList = new ArrayList<>();

    private CommandHandler() {

    }

    public static CommandHandler builder() {
        return new CommandHandler();
    }

    public CommandHandler build(JDA jda) {
        commandList.forEach(command -> jda.upsertCommand(command.getData()).queue());
        return this;
    }

    public Command getCommandByName(String name) {
        return commandList.stream().filter(command -> command.getData().getName().equals(name)).findFirst().orElse(null);
    }
}
