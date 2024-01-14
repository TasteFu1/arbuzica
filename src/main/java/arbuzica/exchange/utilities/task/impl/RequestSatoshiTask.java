package arbuzica.exchange.utilities.task.impl;

import java.io.IOException;

import arbuzica.exchange.Instance;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.utilities.LitecoinUtility;
import arbuzica.exchange.utilities.task.Task;

public class RequestSatoshiTask extends Task {
    public RequestSatoshiTask(long delayMillis) {
        super(delayMillis);
    }

    @Override
    public void execute() {
        if (getTimer().loop(getDelayMillis())) {
            Double satoshiPerByte;

            try {
                satoshiPerByte = LitecoinUtility.getSatoshiPerByte();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Command command : Instance.get().getCommandHandler().getCommandList()) {
                command.getLitecoin().setSatoshiPerByte(satoshiPerByte);
            }

            System.out.println("RequestSatoshiTask: " + satoshiPerByte);
        }
    }
}
