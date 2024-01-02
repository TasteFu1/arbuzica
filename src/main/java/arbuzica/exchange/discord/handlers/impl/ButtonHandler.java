package arbuzica.exchange.discord.handlers.impl;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import arbuzica.exchange.Instance;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.handlers.IHandler;
import lombok.Getter;

@Getter
public class ButtonHandler implements IHandler {
    private ButtonInteractionEvent event;

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof ButtonInteractionEvent eventIn)) {
            return;
        }

        for (Command command : Instance.get().getCommandHandler().getCommandList()) {
            this.event = eventIn;
            command.execute(this);
        }
    }

    public String getButtonId() {
        String buttonId = event.getComponentId();
        return buttonId.contains("/") ? buttonId.split("/")[0] : buttonId;
    }

    public List<String> getParams() {
        String buttonId = event.getComponentId();
        List<String> values = new ArrayList<>();

        if (buttonId.contains("/")) {
            String[] split = buttonId.split("/");
            values.addAll(Arrays.asList(split).subList(1, split.length));
        }

        return values;
    }
}
