package arbuzica.exchange.discord.handlers.impl;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import arbuzica.exchange.Instance;
import arbuzica.exchange.discord.commands.Command;
import arbuzica.exchange.discord.handlers.IHandler;
import lombok.Getter;

@Getter
public class SelectMenuHandler implements IHandler {
    private StringSelectInteractionEvent event;

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof StringSelectInteractionEvent eventIn)) {
            return;
        }

        for (Command command : Instance.get().getCommandHandler().getCommandList()) {
            this.event = eventIn;
            command.execute(this);
        }
    }

    public String getMenuId() {
        String menuId = event.getComponentId();
        return menuId.contains("/") ? menuId.split("/")[0] : menuId;
    }

    public List<String> getParams() {
        String menuId = event.getComponentId();
        List<String> values = new ArrayList<>();

        if (menuId.contains("/")) {
            String[] split = menuId.split("/");
            values.addAll(Arrays.asList(split).subList(1, split.length));
        }

        return values;
    }
}
