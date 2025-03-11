package main.java.com.ubo.tp.message.datamodel.user;

import main.java.com.ubo.tp.message.datamodel.message.Message;

import java.util.List;
import java.util.Set;

/**
 * Interface observer d'un message
 */
public interface IUser {


    /**
     * Ajoute un observateur de messages.
     *
     * @param observer L'observateur à ajouter
     */
    void addObserver(IUserListObserver observer);

    /**
     * Retire un observateur de messages.
     *
     * @param observer L'observateur à retirer
     */
    void removeObserver(IUserListObserver observer);

    void addUser(User user);

    void removeUser(User user);
    void refreshUser();

    List<User> getUsers();

    void modifiyUser(User userToModify);

    int getFollowersCount(User user);





}