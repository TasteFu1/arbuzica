package arbuzica.exchange.discord.override;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import arbuzica.exchange.discord.handlers.IHandler;
import arbuzica.exchange.utilities.discord.HandlerUtility;

public class Callback {
    private final List<ItemComponent[]> actionRows = new ArrayList<>();
    private final List<MessageEmbed> embeds = new ArrayList<>();
    private final List<FileUpload> files = new ArrayList<>();
    private final StringBuilder content = new StringBuilder();
    private final IHandler handler;

    private Callback(IHandler handler) {
        this.handler = handler;
    }

    public static Callback builder(IHandler handler) {
        return new Callback(handler);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Callback addEmbeds(MessageEmbed... embeds) {
        this.embeds.addAll(Arrays.asList(embeds));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Callback addActionRow(ItemComponent... components) {
        this.actionRows.add(components);
        return this;
    }

    public Callback addFile(FileUpload file) {
        this.files.add(file);
        return this;
    }

    public Callback addContent(String content) {
        this.content.append(content);
        return this;
    }

    public void queue() {
        if (HandlerUtility.isAcknowledged(handler)) {
            WebhookMessageCreateAction<Message> messageCreateAction = HandlerUtility.getHook(handler).sendMessageEmbeds(embeds);

            this.actionRows.forEach(messageCreateAction::addActionRow);
            this.files.forEach(messageCreateAction::addFiles);

            messageCreateAction.setContent(this.content.toString()).queue();
        } else {
            ReplyCallbackAction callbackAction = HandlerUtility.getCallback(handler).addEmbeds(embeds);

            this.actionRows.forEach(callbackAction::addActionRow);
            this.files.forEach(callbackAction::addFiles);

            callbackAction.setContent(this.content.toString()).queue();
        }
    }

    public Message complete() {
        WebhookMessageCreateAction<Message> messageCreateAction = HandlerUtility.getHook(handler).sendMessageEmbeds(embeds);

        for (ItemComponent[] actionRow : this.actionRows) {
            messageCreateAction.addActionRow(actionRow);
        }

        return messageCreateAction.complete();
    }
}
