package arbuzica.exchange.discord.commands;

import java.util.ArrayList;
import java.util.List;

import arbuzica.exchange.Instance;
import arbuzica.exchange.discord.commands.impl.DevCommand;
import arbuzica.exchange.discord.commands.impl.UserCommand;
import lombok.Getter;

@Getter
public class CommandHandler {
    private final List<Command> commandList = new ArrayList<>();

    private CommandHandler() {
        commandList.add(new UserCommand());
        commandList.add(new DevCommand());
    }

    public static CommandHandler init() {
        return new CommandHandler();
    }

    public CommandHandler upsert() {
        commandList.forEach(command -> Instance.get().getJda().upsertCommand(command.getData()).queue());
        return this;
    }

    public Command getCommandByName(String name) {
        return commandList.stream().filter(command -> command.getData().getName().equals(name)).findFirst().orElse(null);
    }
}
