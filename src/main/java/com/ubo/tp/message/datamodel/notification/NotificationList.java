package main.java.com.ubo.tp.message.datamodel.notification;

import main.java.com.ubo.tp.message.datamodel.message.IMessageListObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationList implements INotification {
    /**
     * Liste des observateurs de notifications.
     * Utilisation de CopyOnWriteArrayList pour éviter les ConcurrentModificationException
     */
    protected List<INotificationListObserver> mObservers = new CopyOnWriteArrayList<>();

    /**
     * Liste des notifications.
     */
    protected List<Notification> notifications = new ArrayList<>();

    @Override
    public void addObserver(INotificationListObserver observer) {
        this.mObservers.add((INotificationListObserver) observer);
    }

    @Override
    public void removeObserver(INotificationListObserver observer) {
        this.mObservers.remove((INotificationListObserver) observer);
    }

    @Override
    public void addNotification(Notification notification) {
        if (notification != null) {
            this.notifications.add(notification);

            // Notifier les observateurs
            for (INotificationListObserver observer : mObservers) {
                observer.notifyNotificationAdded(notification);
            }
        }
    }

    @Override
    public void removeNotification(Notification notification) {
        if (notification != null && this.notifications.remove(notification)) {
            // Notifier les observateurs uniquement si la notification a été retirée
            for (INotificationListObserver observer : mObservers) {
                observer.notifyNotificationRemoved(notification);
            }
        }
    }

    /**
     * Retourne la liste des notifications
     * @return Liste des notifications
     */
    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Marque toutes les notifications comme lues
     */
    public void markAllNotificationsAsRead() {
        boolean hasChanged = false;
        for (Notification notification : this.notifications) {
            if (!notification.isRead()) {
                notification.markAsRead();
                hasChanged = true;
            }
        }
        if (hasChanged) {
            for (INotificationListObserver observer : mObservers) {
                observer.notifyNotificationsRead();
            }
        }
    }
}
