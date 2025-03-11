package main.java.com.ubo.tp.message.datamodel.message;

import main.java.com.ubo.tp.message.datamodel.user.User;

import java.util.List;

/**
 * Interface observer d'un message
 */
public interface IMessage {


    /**
     * Ajoute un observateur de messages.
     *
     * @param observer L'observateur à ajouter
     */
    void addObserver(IMessageListObserver observer);

    /**
     * Retire un observateur de messages.
     *
     * @param observer L'observateur à retirer
     */
    void removeObserver(IMessageListObserver observer);

    void addMessage(Message message);

    void removeMessage(Message message);
    void refreshMessage();

    List<Message> getUserMessages(User user);

    List<Message> getMessagesWithUserTag(String userTag);

    List<Message> getMessagesWithTag(String tag);

    void modifiyMessage(Message messageToModify);




}