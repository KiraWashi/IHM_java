package main.java.com.ubo.tp.message.ihm.login;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.HashSet;
import java.util.UUID;



/**
 * Contrôleur pour gérer le composant de login et la navigation
 */
public class LoginController {

    /**
     * Base de données de l'application
     */
    private IDatabase database;

    /**
     * Gestionnaire d'entités
     */
    private EntityManager entityManager;

    /**
     * Session
     */
    private ISession session;

    /**
     * Constructeur
     *
     * @param database Base de données
     * @param entityManager Gestionnaire d'entités
     * @param session Session de l'application
     */
    public LoginController(IDatabase database, EntityManager entityManager, ISession session) {
        this.database = database;
        this.entityManager = entityManager;
        this.session = session;
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
        for (User user : database.getUsers()) {
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
        for (User existingUser : database.getUsers()) {
            if (existingUser.getUserTag().equals(tag)) {
                return "Ce tag utilisateur existe déjà";
            }
        }

        // Création du nouvel utilisateur
        User newUser = createUser(name, tag, password, avatarPath);

        // Génération du fichier utilisateur
        entityManager.writeUserFile(newUser);

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