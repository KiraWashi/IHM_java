package main.java.com.ubo.tp.message.datamodel.message;

/**
 * Interface permettant de placer des messages observer dans les listes
 */
public interface IMessageListObserver {
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

    void notifyRefreshMessage();

    /**
     * Notification lorsqu'un Message est modifié en base de données.
     *
     * @param modifiedMessage
     */
    void notifyMessageModified(Message modifiedMessage);

}
