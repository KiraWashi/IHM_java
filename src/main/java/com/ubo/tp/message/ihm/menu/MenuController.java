package main.java.com.ubo.tp.message.ihm.menu;

import java.io.File;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.message.IMessage;
import main.java.com.ubo.tp.message.datamodel.user.IUser;
import main.java.com.ubo.tp.message.datamodel.user.User;
import main.java.com.ubo.tp.message.ihm.Actions;

/**
 * Contrôleur unifié pour le menu de l'application.
 * Combine les fonctionnalités de MenuController, ProfileController,
 * DirectoryController et AboutController.
 */
public class MenuController implements ISessionObserver {

    /**
     * Version de l'application
     */
    private static final String APP_VERSION = "1.0";

    /**
     * Nom de l'application
     */
    private static final String APP_NAME = "MessageApp";

    /**
     * Session de l'application
     */
    private final ISession session;

    /**
     * Utilisateur actuellement connecté
     */
    private User connectedUser;

    /**
     * Base de données
     */
    private final IMessage messageList;

    private final IUser userList;

    /**
     * Interface pour déléguer les actions spécifiques à l'application
     */
    private final Actions action;

    /**
     * Constructeur.
     *
     * @param session La session de l'application
     */
    public MenuController(ISession session, IMessage message, IUser user, Actions action) {
        this.session = session;
        this.messageList = message;
        this.userList = user;
        this.action = action;

        // S'abonner aux événements de session
        this.session.addObserver(this);

    }

    /**
     * Initialise le contrôleur
     */
    public void init() {
        // Vérifier l'état initial de connexion
        this.connectedUser = session.getConnectedUser();
    }


    /**
     * Méthode appelée quand l'utilisateur veut quitter l'application
     */
    public void exit() {
        action.exitApplication();
    }

    /**
     * Méthode appelée quand l'utilisateur veut se déconnecter
     */
    public void logout() {
        action.logout();
    }

    /**
     * Méthode appelée quand l'utilisateur veut changer de repertoire
     */
    public void changeDirectory(String directoryPath) {
        action.changeDirectory(directoryPath);
    }

    /**
     * Retourne l'utilisateur connecté
     *
     * @return L'utilisateur connecté ou null si aucun utilisateur n'est connecté
     */
    public User getConnectedUser() {
        return connectedUser;
    }

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        this.connectedUser = connectedUser;
    }

    @Override
    public void notifyLogout() {
        this.connectedUser = null;
    }

    // Fonctionnalités liées au profil

    /**
     * Récupère le nombre de followers pour un utilisateur
     *
     * @param user Utilisateur
     * @return Nombre de followers
     */
    public int getFollowersCount(User user) {
        return userList.getFollowersCount(user);
    }

    /**
     * Récupère le nombre de messages envoyés par un utilisateur
     *
     * @param user Utilisateur
     * @return Nombre de messages
     */
    public int getUserMessagesCount(User user) {
        return messageList.getUserMessages(user).size();
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

    // Fonctionnalités liées au répertoire

    /**
     * Vérifie si le répertoire est valide pour servir de répertoire d'échange
     *
     * @param directory Répertoire à tester
     * @return true si le répertoire est valide, false sinon
     */
    public boolean isValidExchangeDirectory(File directory) {
        return directory != null && directory.exists() && directory.isDirectory() &&
                directory.canRead() && directory.canWrite();
    }

    // Fonctionnalités liées à "À propos"

    /**
     * Récupère la version de l'application
     *
     * @return Version de l'application
     */
    public String getAppVersion() {
        return APP_VERSION;
    }

    /**
     * Récupère le nom de l'application
     *
     * @return Nom de l'application
     */
    public String getAppName() {
        return APP_NAME;
    }
}