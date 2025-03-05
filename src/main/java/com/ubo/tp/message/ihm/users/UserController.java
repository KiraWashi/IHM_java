package main.java.com.ubo.tp.message.ihm.users;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contrôleur pour la gestion des utilisateurs
 */
public class UserController {

    /**
     * Base de données de l'application
     */
    private IDatabase database;

    /**
     * Session active
     */
    private ISession session;

    /**
     * Gestionnaire d'entités
     */
    private EntityManager entityManager;

    /**
     * Constructeur
     *
     * @param database Base de données
     * @param session Session active
     * @param entityManager Gestionnaire d'entités
     */
    public UserController(IDatabase database, ISession session, EntityManager entityManager) {
        this.database = database;
        this.session = session;
        this.entityManager = entityManager;
    }

    /**
     * Récupère tous les utilisateurs
     *
     * @return Liste des utilisateurs
     */
    public List<User> getAllUsers() {
        Set<User> userSet = database.getUsers();
        List<User> userList = new ArrayList<>(userSet);

        // Tri par nom
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getName().compareToIgnoreCase(u2.getName());
            }
        });

        return userList;
    }

    /**
     * Recherche des utilisateurs
     *
     * @param searchQuery Texte de recherche
     * @return Liste des utilisateurs correspondants
     */
    public List<User> searchUsers(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return getAllUsers();
        }

        searchQuery = searchQuery.trim().toLowerCase();

        Set<User> searchResults = new HashSet<>();

        // Recherche par tag ou nom
        for (User user : database.getUsers()) {
            if (user.getUserTag().toLowerCase().contains(searchQuery) ||
                    user.getName().toLowerCase().contains(searchQuery)) {
                searchResults.add(user);
            }
        }

        // Conversion en liste et tri
        List<User> userList = new ArrayList<>(searchResults);
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getName().compareToIgnoreCase(u2.getName());
            }
        });

        return userList;
    }

    /**
     * Récupère les followers d'un utilisateur
     *
     * @param user Utilisateur
     * @return Liste des followers
     */
    public List<User> getFollowers(User user) {
        if (user == null) {
            return new ArrayList<>();
        }

        Set<User> followers = database.getFollowers(user);
        List<User> followerList = new ArrayList<>(followers);

        // Tri par nom
        Collections.sort(followerList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getName().compareToIgnoreCase(u2.getName());
            }
        });

        return followerList;
    }

    /**
     * Récupère les utilisateurs suivis par un utilisateur
     *
     * @param user Utilisateur
     * @return Liste des utilisateurs suivis
     */
    public List<User> getFollowing(User user) {
        if (user == null) {
            return new ArrayList<>();
        }

        Set<String> followedTags = user.getFollows();
        Set<User> followingSet = new HashSet<>();

        // Récupération des utilisateurs correspondant aux tags suivis
        for (User otherUser : database.getUsers()) {
            if (followedTags.contains(otherUser.getUserTag())) {
                followingSet.add(otherUser);
            }
        }

        List<User> followingList = new ArrayList<>(followingSet);

        // Tri par nom
        Collections.sort(followingList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getName().compareToIgnoreCase(u2.getName());
            }
        });

        return followingList;
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
        database.modifiyUser(connectedUser);

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
        database.modifiyUser(connectedUser);

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

        return database.getFollowersCount(user);
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
