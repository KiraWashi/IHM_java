package main.java.com.ubo.tp.message.datamodel.message;

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




}