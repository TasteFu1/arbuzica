package arbuzica.exchange.discord.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

import arbuzica.exchange.discord.commands.impl.basic.CaptchaCommand;
import arbuzica.exchange.discord.commands.impl.basic.ExchangeCommand;
import arbuzica.exchange.discord.commands.impl.admin.GenerateCommand;
import arbuzica.exchange.discord.commands.impl.basic.RedeemCommand;
import arbuzica.exchange.discord.commands.impl.admin.StockCommand;
import arbuzica.exchange.discord.commands.impl.dev.AdminCommand;
import arbuzica.exchange.discord.commands.impl.temp.TestCommand;
import arbuzica.exchange.discord.commands.impl.basic.UserCommand;
import lombok.Getter;

@Getter
public class CommandHandler {
    private final List<Command> commandList = new ArrayList<>();

    public CommandHandler(JDA jda) {
        commandList.add(new AdminCommand(this, Commands.slash("admin", "param")));
        commandList.add(new TestCommand(this, Commands.slash("test", "param")));

        commandList.add(new UserCommand(this, Commands.slash("user", "param")));
        commandList.add(new RedeemCommand(this, Commands.slash("redeem", "param") //
                .addOption(OptionType.STRING, "key", "param", true)));

        commandList.add(new GenerateCommand(this, Commands.slash("generate", "param") //
                .addOption(OptionType.INTEGER, "quantity", "param", true) //
                .addOption(OptionType.NUMBER, "credits", "param", true)));
        commandList.add(new StockCommand(this, Commands.slash("stock", "param") //
                .addOption(OptionType.BOOLEAN, "redeemed", "param")));
        commandList.add(new ExchangeCommand(this, Commands.slash("exchange", "param") //
                .addOption(OptionType.STRING, "address", "param", true) //
                .addOption(OptionType.NUMBER, "credits", "param", true)));
        commandList.add(new CaptchaCommand(this, Commands.slash("captcha", "param") //
                .addOption(OptionType.STRING, "solution", "param", true)));

        for (Command command : commandList) {
            jda.upsertCommand(command.getData()).queue();
        }
    }

    public Command getCommand(String name) {
        return commandList.stream().filter(command -> command.getData().getName().equals(name)).findFirst().orElse(null);
    }

    public Command getCommand(Class<? extends Command> classIn) {
        return commandList.stream().filter(command -> command.getClass().equals(classIn)).findFirst().orElse(null);
    }
}
