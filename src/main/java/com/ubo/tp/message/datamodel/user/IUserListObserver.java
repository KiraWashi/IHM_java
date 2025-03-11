package main.java.com.ubo.tp.message.datamodel.user;

/**
 * Interface permettant de placer des messages observer dans les listes
 */
public interface IUserListObserver {
    /**
     * Notification lorsqu'un Message est ajouté.
     *
     * @param addedUser Le message ajouté
     */
    void notifyUserAdded(User addedUser);

    /**
     * Notification lorsqu'un Message est supprimé.
     *
     * @param deletedUser Le message supprimé
     */
    void notifyUserDeleted(User deletedUser);

    void notifyRefreshUser();

    void notifyUserModified(User modifiedUser);

}
