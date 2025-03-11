package main.java.com.ubo.tp.message.datamodel;

import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.user.User;

import java.util.Date;

/**
 * Représente une notification dans l'application
 */
public class Notification {

    private Message message;
    private User sender;
    private boolean read;
    private Date creationDate;

    /**
     * Constructeur pour une notification
     *
     * @param message Le message concerné par la notification
     * @param sender L'émetteur du message
     */
    public Notification(Message message, User sender) {
        this.message = message;
        this.sender = sender;
        this.read = false;
        this.creationDate = new Date();
    }

    public Message getMessage() {
        return message;
    }

    public User getSender() {
        return sender;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}