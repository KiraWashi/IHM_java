package main.java.com.ubo.tp.message.datamodel.notification;

/**
 * Interface permettant de placer des notifications observer dans les listes
 */
public interface INotificationListObserver {
    /**
     * Notification lorsqu'une Notification est ajoutée.
     *
     * @param addedNotification La notification ajoutée
     */
    void notifyNotificationAdded(Notification addedNotification);

    /**
     * Notification lorsqu'une Notification est supprimée.
     *
     * @param removedNotification La notification supprimée
     */
    void notifyNotificationRemoved(Notification removedNotification);

    /**
     * Notification lorsque des notifications sont marquées comme lues.
     */
    void notifyNotificationsRead();
}