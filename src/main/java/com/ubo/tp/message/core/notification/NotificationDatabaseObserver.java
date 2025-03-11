package main.java.com.ubo.tp.message.core.notification;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.Notification;
import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Observateur de base de données pour générer des notifications
 */
public class NotificationDatabaseObserver implements IDatabaseObserver {

    private IDatabase database;
    private ISession session;
    private NotificationController notificationController;

    /**
     * Constructeur
     */
    public NotificationDatabaseObserver(IDatabase database, ISession session, NotificationController notificationController) {
        this.database = database;
        this.session = session;
        this.notificationController = notificationController;
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        User currentUser = session.getConnectedUser();
        if (currentUser != null) {
            User sender = addedMessage.getSender();

            // Si le message provient d'un utilisateur que l'utilisateur actuel suit
            if (currentUser.isFollowing(sender) && !currentUser.equals(sender)) {
                notificationController.addNotification(new Notification(addedMessage, sender));
            }
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        // Non utilisé
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        // Non utilisé
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        // Non utilisé
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        // Non utilisé
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        // Non utilisé
    }
}