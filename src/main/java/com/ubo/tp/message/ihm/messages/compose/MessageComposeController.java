package main.java.com.ubo.tp.message.ihm.messages.compose;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la composition et l'envoi de messages
 */
public class MessageComposeController {

    /**
     * Base de données de l'application
     */
    private final IDatabase database;

    /**
     * Gestionnaire d'entités
     */
    private final EntityManager entityManager;

    /**
     * Session active
     */
    private final ISession session;

    /**
     * Limite de caractères pour un message
     */
    private static final int MESSAGE_CHARACTER_LIMIT = 200;

    /**
     * Constructeur
     *
     * @param database Base de données
     * @param entityManager Gestionnaire d'entités
     * @param session Session active
     */
    public MessageComposeController(IDatabase database, EntityManager entityManager, ISession session) {
        this.database = database;
        this.entityManager = entityManager;
        this.session = session;
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

            // Ajout du message à la base de données
            database.addMessage(newMessage);

            // Génération du fichier message
            entityManager.writeMessageFile(newMessage);

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