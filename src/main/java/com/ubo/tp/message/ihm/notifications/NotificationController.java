package main.java.com.ubo.tp.message.ihm.notifications;

import java.io.File;
import java.util.*;

import main.java.com.ubo.tp.message.core.notification.NotificationManager;
import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.notification.INotification;
import main.java.com.ubo.tp.message.datamodel.notification.Notification;
import main.java.com.ubo.tp.message.datamodel.notification.NotificationList;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Gestionnaire des notifications
 */
public class NotificationController implements ISessionObserver {

    private final IDatabase database;
    private final INotification notificationList;
    private User connectedUser;

    private NotificationManager notificationManager;
    private Set<UUID> readMessageIds = new HashSet<>();

    private int count;

    /**
     * Constructeur
     */
    public NotificationController(IDatabase database, ISession session, INotification notif, String exchangeDirectory) {
        this.database = database;
        this.notificationList = notif;
        this.notificationManager = new NotificationManager(exchangeDirectory);
        this.count = 0;

        // S'enregistrer comme observateur
        session.addObserver(this);
    }

    /**
     * Marque toutes les notifications comme lues
     */
    public void markAllAsRead() {
        this.notificationList.markAllNotificationsAsRead();
        notificationManager.deleteAllNotificationFiles();
        for (Notification notification : this.getNotificationsUser()) {
            notificationList.removeNotification(notification);
        }
        notificationList.clear();
        this.count = 0;
        //notificationManager.saveReadNotifications(connectedUser, readMessageIds);
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
        return count;
    }

    public void refreshCount(){
        this.count = 0;
        for (Notification notification : notificationList.getNotifications()) {
            if (connectedUser != null && connectedUser.isFollowing(notification.getSender())) {
                this.count++;
            }
        }
    }

    public List<Notification> getNotificationsUser(){
        ArrayList<Notification> notifs = new ArrayList<>();
        for (Notification notification : notificationList.getNotifications()) {
            if (connectedUser != null && connectedUser.isFollowing(notification.getSender())) {
                notifs.add(notification);
            }
        }
        return  notifs;
    }



    @Override
    public void notifyLogout() {

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
        this.notificationList.refreshMessage();

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
    public INotification getNotificationList() {
        return notificationList;
    }


}