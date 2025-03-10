package main.java.com.ubo.tp.message.datamodel.message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Session de l'application.
 *
 * @author S.Lucas
 */
public class MessageList implements IMessageObserver {
    /**
     * Liste des observateurs de messages.
     * Utilisation de CopyOnWriteArrayList pour éviter les ConcurrentModificationException
     */
    protected List<IMessageObserver> mObservers = new CopyOnWriteArrayList<>();

    /**
     * Liste des messages.
     */
    protected List<Message> messages = new ArrayList<>();

    /**
     * Ajoute un message à la liste et notifie les observateurs
     *
     * @param message Le message à ajouter
     */
    public void addMessage(Message message) {
        if (message != null && !messages.contains(message)) {
            messages.add(message);
            // Notification des observateurs
            notifyMessageAdded(message);
        }
    }

    /**
     * Supprime un message de la liste et notifie les observateurs
     *
     * @param message Le message à supprimer
     */
    public void removeMessage(Message message) {
        if (message != null && messages.contains(message)) {
            messages.remove(message);
            // Notification des observateurs
            notifyMessageDeleted(message);
        }
    }

    /**
     * Retourne une copie de la liste des messages
     *
     * @return Une copie de la liste des messages
     */
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    /**
     * Vide la liste de messages
     */
    public void clear() {
        List<Message> messagesToRemove = new ArrayList<>(messages);
        for (Message message : messagesToRemove) {
            removeMessage(message);
        }
    }

    /**
     * Ajoute un observateur à la liste
     *
     * @param observer L'observateur à ajouter
     */
    public void addObserver(IMessageObserver observer) {
        if (observer != null && !mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    /**
     * Retire un observateur de la liste
     *
     * @param observer L'observateur à retirer
     */
    public void removeObserver(IMessageObserver observer) {
        if (observer != null) {
            mObservers.remove(observer);
        }
    }

    /**
     * Retourne les messages d'un utilisateur spécifique
     *
     * @param user L'utilisateur dont on veut les messages
     * @return La liste des messages de l'utilisateur
     */
    public List<Message> getUserMessages(User user) {
        List<Message> userMessages = new ArrayList<>();
        if (user != null) {
            for (Message message : messages) {
                if (message.getSender().equals(user)) {
                    userMessages.add(message);
                }
            }
        }
        return userMessages;
    }

    /**
     * Recherche les messages contenant un tag spécifique
     *
     * @param tag Le tag à rechercher
     * @return La liste des messages contenant ce tag
     */
    public List<Message> getMessagesWithTag(String tag) {
        List<Message> taggedMessages = new ArrayList<>();
        if (tag != null && !tag.isEmpty()) {
            for (Message message : messages) {
                if (message.containsTag(tag)) {
                    taggedMessages.add(message);
                }
            }
        }
        return taggedMessages;
    }

    /**
     * Recherche les messages mentionnant un utilisateur spécifique
     *
     * @param userTag Le tag utilisateur à rechercher
     * @return La liste des messages mentionnant cet utilisateur
     */
    public List<Message> getMessagesWithUserTag(String userTag) {
        List<Message> taggedMessages = new ArrayList<>();
        if (userTag != null && !userTag.isEmpty()) {
            for (Message message : messages) {
                if (message.containsUserTag(userTag)) {
                    taggedMessages.add(message);
                }
            }
        }
        return taggedMessages;
    }

    // Implémentation des méthodes de l'interface IMessageObserver

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        for (IMessageObserver observer : mObservers) {
            observer.notifyMessageAdded(addedMessage);
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        for (IMessageObserver observer : mObservers) {
            observer.notifyMessageDeleted(deletedMessage);
        }
    }

}
