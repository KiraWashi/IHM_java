package main.java.com.ubo.tp.message.core.notification;

import java.util.*;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.Notification;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;

/**
 * Gestionnaire des notifications
 */
public class NotificationController implements IDatabaseObserver, ISessionObserver {

    private IDatabase database;
    private ISession session;
    private List<Notification> notifications;
    private Set<INotificationObserver> observers;
    private User connectedUser;

    private NotificationManager notificationManager;
    private Set<UUID> readMessageIds = new HashSet<>();
    /**
     * Constructeur
     */
    public NotificationController(IDatabase database, ISession session, String exchangeDirectory) {
        this.database = database;
        this.session = session;
        this.notifications = new ArrayList<>();
        this.observers = new HashSet<>();
        this.notificationManager = new NotificationManager(exchangeDirectory);

        // S'enregistrer comme observateur
        this.session.addObserver(this);
        this.database.addObserver(this);
    }

    /**
     * Ajoute une notification
     */
    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        notifyObservers();
    }

    /**
     * Marque toutes les notifications comme lues
     */
    public void markAllAsRead() {
        boolean changed = false;

        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.markAsRead();
                // Ajouter l'ID du message à la liste des messages lus
                readMessageIds.add(notification.getMessage().getUuid());
                changed = true;
            }
        }

        if (changed && connectedUser != null) {
            // Sauvegarder les notifications lues
            notificationManager.saveReadNotifications(connectedUser, readMessageIds);
            notifyObservers();
        }
    }


    /**
     * Retourne toutes les notifications
     */
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Retourne le nombre de notifications non lues
     */
    public int getUnreadCount() {
        int count = 0;

        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Ajoute un observateur
     */
    public void addObserver(INotificationObserver observer) {
        observers.add(observer);
    }

    /**
     * Retire un observateur
     */
    public void removeObserver(INotificationObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifie tous les observateurs
     */
    private void notifyObservers() {
        for (INotificationObserver observer : observers) {
            observer.notifyNotificationChanged();
        }
    }


    @Override
    public void notifyLogout() {
        this.connectedUser = null;
        // Réinitialiser les notifications lors de la déconnexion
        this.notifications.clear();
        // Notifier les observateurs
        notifyObservers();
    }

    /**
     * Méthodes de IDatabaseObserver
     */
    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if (connectedUser != null) {
            User sender = addedMessage.getSender();

            // Pour le débogage : afficher toujours les informations
            System.out.println("Message reçu de : " + sender.getUserTag());
            System.out.println("User connecté : " + connectedUser.getUserTag());
            System.out.println("isFollowing : " + connectedUser.isFollowing(sender));
            System.out.println("Follows list : " + connectedUser.getFollows());

            // Version modifiée pour test : notifier pour tout message qui n'est pas de l'utilisateur lui-même
            if (!sender.equals(connectedUser)) {
                Notification notification = new Notification(addedMessage, sender);
                addNotification(notification);
                System.out.println("Notification créée pour: " + addedMessage.getText());
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

                    notifications.add(notification);
                }
            }
        }

        // Tri des notifications par date (plus récentes d'abord)
        Collections.sort(notifications, new Comparator<Notification>() {
            @Override
            public int compare(Notification n1, Notification n2) {
                return n2.getCreationDate().compareTo(n1.getCreationDate());
            }
        });

        // Notifier les observateurs
        if (!notifications.isEmpty()) {
            notifyObservers();
        }
    }

    // Modifiez la méthode notifyLogin pour charger les notifications au moment de la connexion
    @Override
    public void notifyLogin(User connectedUser) {
        this.connectedUser = connectedUser;
        this.notifications.clear();

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
            loadExistingMessages();
        }
    }
}