package main.java.com.ubo.tp.message.ihm.login;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.user.IUser;
import main.java.com.ubo.tp.message.datamodel.user.User;

import java.util.HashSet;
import java.util.UUID;



/**
 * Contrôleur pour gérer le composant de login et la navigation
 */
public class LoginController {

    private final IUser userList;

    /**
     * Gestionnaire d'entités
     */
    private final EntityManager entityManager;

    /**
     * Session
     */
    private final ISession session;

    /**
     * Constructeur
     *
     * @param entityManager Gestionnaire d'entités
     * @param session Session de l'application
     */
    public LoginController(EntityManager entityManager, ISession session, IUser user) {
        this.entityManager = entityManager;
        this.session = session;
        this.userList = user;
    }

    /**
     * Tente de connecter un utilisateur
     *
     * @param tag Tag de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Message d'erreur ou null si la connexion est réussie
     */
    public String attemptLogin(String tag, String password) {
        if (tag.isEmpty()) {
            return "Veuillez entrer votre tag utilisateur";
        }

        // Recherche de l'utilisateur dans la base de données par son tag
        User foundUser = null;
        for (User user : userList.getUsers()) {
            if (user.getUserTag().equals(tag)) {
                foundUser = user;
                break;
            }
        }

        if (foundUser == null) {
            return "Utilisateur introuvable";
        }

        // Vérification du mot de passe (dans un vrai système, il faudrait hasher)
        if (!foundUser.getUserPassword().equals(password)) {
            return "Mot de passe incorrect";
        }

        // Connexion réussie
        session.connect(foundUser);
        return null; // Pas d'erreur
    }

    /**
     * Tente d'inscrire un nouvel utilisateur
     *
     * @param name Nom de l'utilisateur
     * @param tag Tag de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @param avatarPath Chemin de l'avatar de l'utilisateur
     * @return Message d'erreur ou null si l'inscription est réussie
     */
    public String attemptRegister(String name, String tag, String password, String avatarPath) {
        // Validation des champs obligatoires
        if (name.isEmpty() || tag.isEmpty()) {
            return "Le nom et le tag sont obligatoires";
        }

        // Vérification que le tag n'existe pas déjà
        for (User existingUser : userList.getUsers()) {
            if (existingUser.getUserTag().equals(tag)) {
                return "Ce tag utilisateur existe déjà";
            }
        }

        // Création du nouvel utilisateur
        User newUser = createUser(name, tag, password, avatarPath);

        // Génération du fichier utilisateur
        entityManager.writeUserFile(newUser);

        //userList.addUser(newUser);


        return null;
    }

    /**
     * Crée un nouvel utilisateur
     *
     * @param name Nom de l'utilisateur
     * @param tag Tag de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @param avatarPath Chemin de l'avatar de l'utilisateur
     * @return Le nouvel utilisateur créé
     */
    private User createUser(String name, String tag, String password, String avatarPath) {
        UUID newUserId = UUID.randomUUID();
        HashSet<String> emptyFollows = new HashSet<>();
        return new User(newUserId, tag, password, name, emptyFollows, avatarPath);
    }

}