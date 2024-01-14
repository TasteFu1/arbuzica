package arbuzica.exchange.utilities.task.impl;

import java.io.IOException;

import arbuzica.exchange.Instance;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.utilities.LitecoinUtility;
import arbuzica.exchange.utilities.task.Task;

public class RequestRateTask extends Task {
    public RequestRateTask(long delayMillis) {
        super(delayMillis);
    }

    @Override
    public void execute() {
        if (getTimer().loop(getDelayMillis())) {
            Double rateToUSD;

            try {
                rateToUSD = LitecoinUtility.getRateToUSD();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Command command : Instance.get().getCommandHandler().getCommandList()) {
                command.getLitecoin().setRateToUSD(rateToUSD);
            }

            System.out.println("RequestRateTask: " + rateToUSD);
        }
    }
}
