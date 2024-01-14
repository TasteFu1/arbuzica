package arbuzica.exchange.utilities.discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import arbuzica.exchange.Instance;
import arbuzica.exchange.database.entities.Account;
import arbuzica.exchange.discord.handlers.IHandler;
import arbuzica.exchange.discord.handlers.impl.ButtonHandler;
import arbuzica.exchange.discord.handlers.impl.ModalHandler;
import arbuzica.exchange.discord.handlers.impl.SelectMenuHandler;
import arbuzica.exchange.discord.handlers.impl.SlashCommandHandler;

public class HandlerUtility {
    public static GenericEvent getEvent(IHandler iHandler) {
        if (iHandler instanceof SlashCommandHandler handler) {
            return handler.getEvent();
        } else if (iHandler instanceof ButtonHandler handler) {
            return handler.getEvent();
        } else if (iHandler instanceof ModalHandler handler) {
            return handler.getEvent();
        } else if (iHandler instanceof SelectMenuHandler handler) {
            return handler.getEvent();
        }

        return null;
    }

    public static User getUser(IHandler iHandler) {
        if (iHandler instanceof SlashCommandHandler handler) {
            return handler.getEvent().getUser();
        } else if (iHandler instanceof ButtonHandler handler) {
            return handler.getEvent().getUser();
        } else if (iHandler instanceof ModalHandler handler) {
            return handler.getEvent().getUser();
        } else if (iHandler instanceof SelectMenuHandler handler) {
            return handler.getEvent().getUser();
        }

        return null;
    }

    public static ReplyCallbackAction getCallback(IHandler iHandler) {
        if (iHandler instanceof SlashCommandHandler handler) {
            return handler.getEvent().deferReply();
        } else if (iHandler instanceof ButtonHandler handler) {
            return handler.getEvent().deferReply();
        } else if (iHandler instanceof ModalHandler handler) {
            return handler.getEvent().deferReply();
        } else if (iHandler instanceof SelectMenuHandler handler) {
            return handler.getEvent().deferReply();
        }

        return null;
    }

    public static InteractionHook getHook(IHandler iHandler) {
        if (iHandler instanceof SlashCommandHandler handler) {
            return handler.getEvent().getHook();
        } else if (iHandler instanceof ButtonHandler handler) {
            return handler.getEvent().getHook();
        } else if (iHandler instanceof ModalHandler handler) {
            return handler.getEvent().getHook();
        } else if (iHandler instanceof SelectMenuHandler handler) {
            return handler.getEvent().getHook();
        }

        return null;
    }

    public static boolean isAcknowledged(IHandler iHandler) {
        if (iHandler instanceof SlashCommandHandler handler) {
            return handler.getEvent().isAcknowledged();
        } else if (iHandler instanceof ButtonHandler handler) {
            return handler.getEvent().isAcknowledged();
        } else if (iHandler instanceof ModalHandler handler) {
            return handler.getEvent().isAcknowledged();
        } else if (iHandler instanceof SelectMenuHandler handler) {
            return handler.getEvent().isAcknowledged();
        }

        return false;
    }
}
