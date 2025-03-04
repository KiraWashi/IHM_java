package main.java.com.ubo.tp.message.ihm.menu.profile;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion du profil utilisateur
 */
public class ProfileController {

    /**
     * Référence vers la base de données
     */
    private IDatabase database;

    /**
     * Constructeur
     *
     * @param database Base de données
     */
    public ProfileController(IDatabase database) {
        this.database = database;
    }

    /**
     * Récupère le nombre de followers pour un utilisateur
     *
     * @param user Utilisateur
     * @return Nombre de followers
     */
    public int getFollowersCount(User user) {
        return database.getFollowersCount(user);
    }

    /**
     * Récupère le nombre de messages envoyés par un utilisateur
     *
     * @param user Utilisateur
     * @return Nombre de messages
     */
    public int getUserMessagesCount(User user) {
        return database.getUserMessages(user).size();
    }

    /**
     * Récupère le nombre d'utilisateurs suivis par un utilisateur
     *
     * @param user Utilisateur
     * @return Nombre d'utilisateurs suivis
     */
    public int getFollowedCount(User user) {
        return user.getFollows().size();
    }
}