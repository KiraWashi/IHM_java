package main.java.com.ubo.tp.message.datamodel.notification;

import main.java.com.ubo.tp.message.datamodel.message.IMessageListObserver;

import java.util.List;

/**
 * Interface observer d'une notification
 */
public interface INotification {

    /**
     * Ajoute un observateur de notifications.
     *
     * @param observer L'observateur à ajouter
     */
    void addObserver(INotificationListObserver observer);

    /**
     * Retire un observateur de notifications.
     *
     * @param observer L'observateur à retirer
     */
    void removeObserver(INotificationListObserver observer);

    void clear();

    void refreshMessage();

    /**
     * Ajoute une notification dans la liste
     * @param notification
     */
    void addNotification(Notification notification);

    /**
     * Retire une notification dans la liste
     * @param notification
     */
    void removeNotification(Notification notification);

    List<Notification> getNotifications();

    void markAllNotificationsAsRead();
}
