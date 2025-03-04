package main.java.com.ubo.tp.message.ihm.login;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;

/**
 * Contrôleur pour gérer le composant de login et la navigation
 */
public class LoginController implements ISessionObserver {

    /**
     * Vue principale de l'application
     */
    private JFrame mainFrame;

    /**
     * Vue de login
     */
    private LoginView loginView;

    /**
     * Vue du contenu principal de l'application
     */
    private JPanel mainContentView;

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
     * @param mainFrame Fenêtre principale de l'application
     * @param database Base de données
     * @param entityManager Gestionnaire d'entités
     * @param session Session de l'application
     */
    public LoginController(JFrame mainFrame, IDatabase database, EntityManager entityManager, ISession session) {
        this.mainFrame = mainFrame;
        this.database = database;
        this.entityManager = entityManager;
        this.session = session;

        // S'inscrire comme observateur de la session
        session.addObserver(this);

        // Création de la vue de login
        this.loginView = new LoginView(this);
    }

    /**
     * Initialise le controller et affiche le composant de login
     */
    public void init() {
        // Récupère le contentPane
        Container contentPane = mainFrame.getContentPane();

        // Si l'utilisateur n'est pas déjà connecté, affiche la vue de login
        if (session.getConnectedUser() == null) {
            // Vide le contentPane
            contentPane.removeAll();

            // Affiche la vue de login
            contentPane.add(loginView, BorderLayout.CENTER);

            // Rafraîchit la vue
            contentPane.revalidate();
            contentPane.repaint();
        } else {
            // Si un utilisateur est déjà connecté, affiche le contenu principal
            showMainContent();
        }
    }

    /**
     * Définit le panneau de contenu principal à afficher après connexion
     *
     * @param mainView Panneau du contenu principal
     */
    public void setMainContentView(JPanel mainView) {
        this.mainContentView = mainView;
    }

    /**
     * Affiche la vue principale après connexion
     */
    private void showMainContent() {
        // Récupère le contentPane
        Container contentPane = mainFrame.getContentPane();

        // Vide le contentPane
        contentPane.removeAll();

        // Ajoute la vue principale
        contentPane.add(mainContentView, BorderLayout.CENTER);

        // Rafraîchit la vue
        contentPane.revalidate();
        contentPane.repaint();
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

        // Ajout à la base de données
        database.addUser(newUser);

        // Génération du fichier utilisateur
        entityManager.writeUserFile(newUser);

        // Connexion automatique de l'utilisateur
        session.connect(newUser);

        return null; // Pas d'erreur
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
        java.util.UUID newUserId = java.util.UUID.randomUUID();
        java.util.Set<String> emptyFollows = new java.util.HashSet<>();
        return new User(newUserId, tag, password, name, emptyFollows, avatarPath);
    }

    /**
     * Retourne la base de données
     */
    public IDatabase getDatabase() {
        return database;
    }

    /**
     * Retourne la session
     */
    public ISession getSession() {
        return session;
    }

    /**
     * Méthode appelée lors de la connexion d'un utilisateur
     */
    @Override
    public void notifyLogin(User connectedUser) {
        // Affiche la vue principale après connexion
        showMainContent();
    }

    /**
     * Méthode appelée lors de la déconnexion
     */
    @Override
    public void notifyLogout() {
        // Réaffiche la vue de login
        init();
    }
}