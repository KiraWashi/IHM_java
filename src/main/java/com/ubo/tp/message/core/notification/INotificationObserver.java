package main.java.com.ubo.tp.message.core.notification;

/**
 * Interface pour les observateurs de notifications
 */
public interface INotificationObserver {

    /**
     * Méthode appelée lorsque les notifications changent
     */
    void notifyNotificationChanged();
}