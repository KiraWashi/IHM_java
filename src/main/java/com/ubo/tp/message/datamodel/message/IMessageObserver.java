package main.java.com.ubo.tp.message.datamodel.message;

import main.java.com.ubo.tp.message.core.session.ISessionObserver;

/**
 * Interface observer d'un message
 */
public interface IMessageObserver {
    /**
     * Notification lorsqu'un Message est ajouté.
     *
     * @param addedMessage Le message ajouté
     */
    void notifyMessageAdded(Message addedMessage);

    /**
     * Notification lorsqu'un Message est supprimé.
     *
     * @param deletedMessage Le message supprimé
     */
    void notifyMessageDeleted(Message deletedMessage);
}