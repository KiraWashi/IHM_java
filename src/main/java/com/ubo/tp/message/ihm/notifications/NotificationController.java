package main.java.com.ubo.tp.message.ihm.notifications;

import java.util.*;

import main.java.com.ubo.tp.message.core.notification.NotificationManager;
import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.notification.Notification;
import main.java.com.ubo.tp.message.datamodel.notification.NotificationList;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;

/**
 * Gestionnaire des notifications
 */
public class NotificationController implements IDatabaseObserver, ISessionObserver {

    private final IDatabase database;
    private final NotificationList notificationList;
    private User connectedUser;

    private NotificationManager notificationManager;
    private Set<UUID> readMessageIds = new HashSet<>();

    /**
     * Constructeur
     */
    public NotificationController(IDatabase database, ISession session, String exchangeDirectory) {
        this.database = database;
        this.notificationList = new NotificationList();
        this.notificationManager = new NotificationManager(exchangeDirectory);

        // S'enregistrer comme observateur
        session.addObserver(this);
        this.database.addObserver(this);
    }

    /**
     * Ajoute une notification
     */
    public void addNotification(Notification notification) {
        this.notificationList.addNotification(notification);
        // La notification des observateurs est gérée par les callbacks de la NotificationList
    }

    /**
     * Marque toutes les notifications comme lues
     */
    public void markAllAsRead() {
        this.notificationList.markAllNotificationsAsRead();
        notificationManager.saveReadNotifications(connectedUser, readMessageIds);
    }

    /**
     * Retourne toutes les notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationList.getNotifications();
    }

    /**
     * Retourne le nombre de notifications non lues
     */
    public int getUnreadCount() {
        int count = 0;

        for (Notification notification : notificationList.getNotifications()) {
            if (!notification.isRead()) {
                count++;
            }
        }

        return count;
    }



    @Override
    public void notifyLogout() {
        this.connectedUser = null;

        // Récupérer toutes les notifications pour les retirer une par une
        List<Notification> allNotifications = new ArrayList<>(notificationList.getNotifications());
        for (Notification notification : allNotifications) {
            notificationList.removeNotification(notification);
        }
    }

    /**
     * Méthodes de IDatabaseObserver
     */
    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if (connectedUser != null) {
            User sender = addedMessage.getSender();

            // Version modifiée pour test : notifier pour tout message qui n'est pas de l'utilisateur lui-même
            if (!sender.equals(connectedUser)) {
                Notification notification = new Notification(addedMessage, sender);
                addNotification(notification);
            }
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        // Non implémenté pour cet exemple
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        // Non implémenté pour cet exemple
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        // Non implémenté pour cet exemple
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        // Non implémenté pour cet exemple
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        // Non implémenté pour cet exemple
    }

    /**
     * Permet de charger les notifications de l'utilisateur actif
     */
    private void loadExistingMessages() {
        if (connectedUser == null) return;

        System.out.println("Chargement des messages existants pour " + connectedUser.getUserTag());

        Set<Message> allMessages = database.getMessages();
        List<Notification> notificationsToAdd = new ArrayList<>();

        for (Message message : allMessages) {
            User sender = message.getSender();

            // Vérification stricte que l'émetteur n'est pas l'utilisateur connecté
            if (sender != null && !sender.getUuid().equals(connectedUser.getUuid())) {
                // Vérifier si l'utilisateur suit l'émetteur
                if (isUserFollowing(connectedUser, sender)) {
                    Notification notification = new Notification(message, sender);

                    // Marquer comme lue si déjà vue
                    if (readMessageIds.contains(message.getUuid())) {
                        notification.markAsRead();
                    }

                    notificationsToAdd.add(notification);
                }
            }
        }

        // Tri des notifications par date (plus récentes d'abord)
        Collections.sort(notificationsToAdd, (n1, n2) ->
                n2.getCreationDate().compareTo(n1.getCreationDate()));

        // Ajouter les notifications triées
        for (Notification notification : notificationsToAdd) {
            notificationList.addNotification(notification);
        }
    }

    @Override
    public void notifyLogin(User connectedUser) {
        this.connectedUser = connectedUser;

        // Vider la liste de notifications actuelle
        List<Notification> currentNotifications = new ArrayList<>(notificationList.getNotifications());
        for (Notification notification : currentNotifications) {
            notificationList.removeNotification(notification);
        }

        // Charger les notifications lues pour l'utilisateur
        this.readMessageIds = notificationManager.loadReadNotifications(connectedUser);

        // Charger les notifications pour les messages existants
        loadExistingMessages();
    }

    private boolean isUserFollowing(User follower, User followed) {
        if (follower == null || followed == null) return false;

        String tagToCheck = followed.getUserTag();
        for (String tag : follower.getFollows()) {
            if (tag != null && !tag.isEmpty() && tag.equals(tagToCheck)) {
                return true;
            }
        }
        return false;
    }

    public void setExchangeDirectory(String directoryPath) {
        this.notificationManager = new NotificationManager(directoryPath);

        // Recharger les notifications si un utilisateur est connecté
        if (this.connectedUser != null) {
            this.readMessageIds = notificationManager.loadReadNotifications(connectedUser);

            // Vider la liste de notifications actuelle
            List<Notification> currentNotifications = new ArrayList<>(notificationList.getNotifications());
            for (Notification notification : currentNotifications) {
                notificationList.removeNotification(notification);
            }

            // Recharger les notifications
            loadExistingMessages();
        }
    }

    /**
     * Retourne la liste de notifications gérée par ce contrôleur
     */
    public NotificationList getNotificationList() {
        return notificationList;
    }

}