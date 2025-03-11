package main.java.com.ubo.tp.message.datamodel.user;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Session de l'application.
 *
 * @author S.Lucas
 */
public class UserList implements IUser {
    /**
     * Liste des observateurs de messages.
     * Utilisation de CopyOnWriteArrayList pour éviter les ConcurrentModificationException
     */
    protected List<IUserListObserver> mObservers = new CopyOnWriteArrayList<>();

    /**
     * Liste des messages.
     */
    protected Set<User> users = new HashSet<>();

    /**
     * Ajoute un message à la liste et notifie les observateurs
     *
     * @param user Le message à ajouter
     */
    @Override
    public void addUser(User user) {
        // Ajout de l'utilisateur
        this.users.add(user);

        // Notification des observateurs
        for (IUserListObserver observer : mObservers) {
            observer.notifyUserAdded(user);
        }
    }

    @Override
    public void refreshUser() {
        for (IUserListObserver observer : mObservers) {
            observer.notifyRefreshUser();
        }
    }

    /**
     * Supprime un message de la liste et notifie les observateurs
     *
     * @param user Le message à supprimer
     */
    @Override
    public void removeUser(User user) {
        // Suppression de l'utilisateur
        this.users.remove(user);

        // Notification des observateurs
        for (IUserListObserver observer : mObservers) {
            observer.notifyUserDeleted(user);
        }
    }

    @Override
    public void modifiyUser(User userToModify) {
        // Le ré-ajout va écraser l'ancienne copie.
        this.users.add(userToModify);

        // Notification des observateurs
        for (IUserListObserver observer : mObservers) {
            observer.notifyUserModified(userToModify);
        }
    }

    /**
     * Retourne une copie de la liste des messages
     *
     * @return Une copie de la liste des messages
     */
    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public int getFollowersCount(User user) {
        return this.getFollowers(user).size();
    }

    public Set<User> getFollowers(User user) {
        Set<User> followers = new HashSet<>();

        // Parcours de tous les utilisateurs de la base
        for (User otherUser : this.getUsers()) {
            // Si le l'utilisateur courant suit l'utilisateur donné
            if (otherUser.isFollowing(user)) {
                followers.add(otherUser);
            }
        }

        return followers;
    }


    /**
     * Vide la liste de messages
     */

    public void clear() {
        List<User> usersToRemove = new ArrayList<>(users);
        for (User usert : usersToRemove) {
            removeUser(usert);
        }
    }

    /**
     * Ajoute un observateur à la liste
     *
     * @param observer L'observateur à ajouter
     */
    @Override
    public void addObserver(IUserListObserver observer) {
        if (observer != null && !mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    /**
     * Retire un observateur de la liste
     *
     * @param observer L'observateur à retirer
     */
    @Override
    public void removeObserver(IUserListObserver observer) {
        if (observer != null) {
            mObservers.remove(observer);
        }
    }







}
