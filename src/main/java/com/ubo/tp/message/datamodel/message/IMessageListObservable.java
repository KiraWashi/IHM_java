package main.java.com.ubo.tp.message.datamodel.message;

/**
 * Interface permettant de placer des messages observer dans les listes
 */
public interface IMessageListObservable {
    /**
     * Ajoute un observateur de messages.
     *
     * @param observer L'observateur à ajouter
     */
    void addObserver(IMessageObserver observer);

    /**
     * Retire un observateur de messages.
     *
     * @param observer L'observateur à retirer
     */
    void removeObserver(IMessageObserver observer);
}
