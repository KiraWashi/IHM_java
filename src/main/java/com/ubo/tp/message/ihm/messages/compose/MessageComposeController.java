package main.java.com.ubo.tp.message.ihm.messages.compose;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.message.IMessage;
import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.notification.INotification;
import main.java.com.ubo.tp.message.datamodel.notification.Notification;
import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Contrôleur pour la composition et l'envoi de messages
 */
public class MessageComposeController {

    private final IMessage messageList;

    /**
     * Gestionnaire d'entités
     */
    private final EntityManager entityManager;

    /**
     * Session active
     */
    private final ISession session;

    private final INotification notificationList;

    /**
     * Limite de caractères pour un message
     */
    private static final int MESSAGE_CHARACTER_LIMIT = 200;

    /**
     * Constructeur
     *
     * @param entityManager Gestionnaire d'entités
     * @param session Session active
     */
    public MessageComposeController(EntityManager entityManager, ISession session, IMessage message, INotification notificationList) {
        this.entityManager = entityManager;
        this.session = session;
        this.messageList = message;
        this.notificationList = notificationList;
    }

    /**
     * Envoie un nouveau message
     *
     * @param messageText Texte du message
     * @return Message d'erreur ou null si l'envoi est réussi
     */
    public String sendMessage(String messageText) {
        // Vérification de l'utilisateur connecté
        User currentUser = session.getConnectedUser();
        if (currentUser == null) {
            return "Vous devez être connecté pour envoyer un message";
        }

        // Vérification du contenu du message
        if (messageText == null || messageText.trim().isEmpty()) {
            return "Le message ne peut pas être vide";
        }

        // Vérification de la longueur du message
        if (messageText.length() > MESSAGE_CHARACTER_LIMIT) {
            return "Le message ne doit pas dépasser " + MESSAGE_CHARACTER_LIMIT + " caractères";
        }

        try {
            // Création du message
            Message newMessage = new Message(currentUser, messageText);

            // Ajout du message à la base de donnée
            messageList.addMessage(newMessage);

            Notification newNotif = new Notification(newMessage, currentUser);

            // Génération du fichier message
            entityManager.writeMessageFile(newMessage);

            entityManager.writeNotifFile(newNotif);

            return null; // Pas d'erreur
        } catch (Exception e) {
            return "Erreur lors de l'envoi du message: " + e.getMessage();
        }
    }

    /**
     * Retourne la limite de caractères pour un message
     *
     * @return Limite de caractères
     */
    public int getMessageCharacterLimit() {
        return MESSAGE_CHARACTER_LIMIT;
    }
}