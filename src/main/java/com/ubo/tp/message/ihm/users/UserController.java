package main.java.com.ubo.tp.message.ihm.users;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.user.IUser;
import main.java.com.ubo.tp.message.datamodel.user.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contrôleur pour la gestion des utilisateurs
 */
public class UserController {

    /**
     * Session active
     */
    private final ISession session;

    /**
     * Gestionnaire d'entités
     */
    private final EntityManager entityManager;

    private final IUser userList;

    /**
     * Constructeur
     *
     * @param session Session active
     * @param entityManager Gestionnaire d'entités
     */
    public UserController(ISession session, EntityManager entityManager, IUser user) {
        this.session = session;
        this.entityManager = entityManager;
        this.userList = user;
    }

    /**
     * Récupère tous les utilisateurs
     *
     * @return Liste des utilisateurs
     */
    public List<User> getAllUsers(Set<User> searchResults) {
        List<User> users;
        if(searchResults != null){
            users = new ArrayList<>(searchResults);
        } else {
            users = userList.getUsers();
        }


        // Tri par nom
        users.sort((u1, u2) -> u1.getName().compareToIgnoreCase(u2.getName()));

        return users;
    }

    /**
     * Recherche des utilisateurs
     *
     * @param searchQuery Texte de recherche
     * @return Liste des utilisateurs correspondants
     */
    public List<User> searchUsers(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return getAllUsers(null);
        }

        searchQuery = searchQuery.trim().toLowerCase();

        Set<User> searchResults = new HashSet<>();

        // Recherche par tag ou nom
        for (User user : userList.getUsers()) {
            if (user.getUserTag().toLowerCase().contains(searchQuery) ||
                    user.getName().toLowerCase().contains(searchQuery)) {
                searchResults.add(user);
            }
        }

        return this.getAllUsers(searchResults);
    }

    /**
     * Suit un utilisateur
     *
     * @param userToFollow Utilisateur à suivre
     * @return Message d'erreur ou null si l'opération a réussi
     */
    public String followUser(User userToFollow) {
        User connectedUser = session.getConnectedUser();

        // Vérifications
        if (connectedUser == null) {
            return "Vous devez être connecté pour suivre un utilisateur";
        }

        if (userToFollow == null) {
            return "Utilisateur invalide";
        }

        if (connectedUser.equals(userToFollow)) {
            return "Vous ne pouvez pas vous suivre vous-même";
        }

        if (connectedUser.getFollows().contains(userToFollow.getUserTag())) {
            return "Vous suivez déjà cet utilisateur";
        }

        // Ajout de l'utilisateur aux follows
        connectedUser.addFollowing(userToFollow.getUserTag());

        // Mise à jour dans la base de données
        userList.modifiyUser(connectedUser);

        // Écriture du fichier utilisateur
        entityManager.writeUserFile(connectedUser);

        return null; // Pas d'erreur
    }

    /**
     * Ne plus suivre un utilisateur
     *
     * @param userToUnfollow Utilisateur à ne plus suivre
     * @return Message d'erreur ou null si l'opération a réussi
     */
    public String unfollowUser(User userToUnfollow) {
        User connectedUser = session.getConnectedUser();

        // Vérifications
        if (connectedUser == null) {
            return "Vous devez être connecté pour ne plus suivre un utilisateur";
        }

        if (userToUnfollow == null) {
            return "Utilisateur invalide";
        }

        if (!connectedUser.getFollows().contains(userToUnfollow.getUserTag())) {
            return "Vous ne suivez pas cet utilisateur";
        }

        // Suppression de l'utilisateur des follows
        connectedUser.removeFollowing(userToUnfollow.getUserTag());

        // Mise à jour dans la base de données
        userList.modifiyUser(connectedUser);

        // Écriture du fichier utilisateur
        entityManager.writeUserFile(connectedUser);

        return null; // Pas d'erreur
    }

    /**
     * Vérifie si l'utilisateur connecté suit un utilisateur
     *
     * @param user Utilisateur à vérifier
     * @return true si l'utilisateur est suivi, false sinon
     */
    public boolean isFollowing(User user) {
        User connectedUser = session.getConnectedUser();

        if (connectedUser == null || user == null) {
            return false;
        }

        return connectedUser.getFollows().contains(user.getUserTag());
    }

    /**
     * Récupère le nombre de followers d'un utilisateur
     *
     * @param user Utilisateur
     * @return Nombre de followers
     */
    public int getFollowersCount(User user) {
        if (user == null) {
            return 0;
        }

        return userList.getFollowersCount(user);
    }

    /**
     * Récupère le nombre d'utilisateurs suivis par un utilisateur
     *
     * @param user Utilisateur
     * @return Nombre d'utilisateurs suivis
     */
    public int getFollowingCount(User user) {
        if (user == null) {
            return 0;
        }

        return user.getFollows().size();
    }
}
