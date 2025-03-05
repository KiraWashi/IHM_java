package main.java.com.ubo.tp.message.ihm.menu;

import main.java.com.ubo.tp.message.ihm.MessageAppMainView;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.ihm.menu.about.AboutController;
import main.java.com.ubo.tp.message.ihm.menu.about.AboutView;
import main.java.com.ubo.tp.message.ihm.menu.directoryChoose.DirectoryChooserView;
import main.java.com.ubo.tp.message.ihm.menu.directoryChoose.DirectoryController;
import main.java.com.ubo.tp.message.ihm.menu.profile.ProfileController;
import main.java.com.ubo.tp.message.ihm.menu.profile.ProfileView;

/**
 * Classe qui gère la logique du menu de l'application.
 * Responsable de la gestion des événements et des interactions entre la vue et le modèle.
 */
public class MenuController implements ISessionObserver {

    /**
     * Référence vers la vue principale
     */
    private MessageAppMainView mainView;

    /**
     * Vue du menu
     */
    private MessageAppMenuView menuView;

    /**
     * Session de l'application
     */
    private ISession session;

    /**
     * Utilisateur actuellement connecté
     */
    private User connectedUser;

    /**
     * Contrôleur pour la gestion du profil
     */
    private ProfileController profileController;

    /**
     * Contrôleur pour la gestion du répertoire d'échange
     */
    private DirectoryController directoryController;

    /**
     * Contrôleur pour la gestion des informations "À propos"
     */
    private AboutController aboutController;

    /**
     * Composant pour l'affichage du profil
     */
    private ProfileView profileView;

    /**
     * Composant pour le choix du répertoire d'échange
     */
    private DirectoryChooserView directoryChooserView;

    /**
     * Composant pour l'affichage de la boîte "À propos"
     */
    private AboutView aboutView;

    /**
     * Constructeur.
     *
     * @param mainView La vue principale de l'application
     * @param session La session de l'application
     * @param profileController Le contrôleur de profil
     * @param directoryController Le contrôleur de répertoire
     * @param aboutController Le contrôleur "À propos"
     */
    public MenuController(MessageAppMainView mainView, ISession session,
                          ProfileController profileController,
                          DirectoryController directoryController,
                          AboutController aboutController) {
        this.mainView = mainView;
        this.session = session;
        this.profileController = profileController;
        this.directoryController = directoryController;
        this.aboutController = aboutController;

        // S'abonner aux événements de session
        this.session.addObserver(this);

        // Initialisation des vues
        this.profileView = new ProfileView(mainView, profileController);
        this.directoryChooserView = new DirectoryChooserView(mainView, directoryController);
        this.aboutView = new AboutView(mainView, aboutController);

        // Création de la vue du menu
        this.menuView = new MessageAppMenuView(this);
    }

    /**
     * Initialise le contrôleur
     */
    public void init() {
        // Vérifier l'état initial de connexion
        this.connectedUser = session.getConnectedUser();
        if (menuView != null) {
            menuView.updateMenuState(this.connectedUser != null);
        }
    }

    /**
     * Retourne la vue du menu
     *
     * @return La vue du menu
     */
    public MessageAppMenuView getMenuView() {
        return menuView;
    }

    /**
     * Méthode appelée quand l'utilisateur veut quitter l'application
     */
    public void exit() {
        mainView.closeApp();
    }

    /**
     * Méthode appelée quand l'utilisateur veut changer le répertoire d'échange
     */
    public void chooseDirectory() {
        directoryChooserView.showDirectoryChooser();
    }

    /**
     * Méthode appelée quand l'utilisateur veut voir son profil
     */
    public void showProfile() {
        if (connectedUser != null) {
            profileView.showUserProfile(connectedUser);
        }
    }

    /**
     * Méthode appelée quand l'utilisateur veut se déconnecter
     */
    public void logout() {
        mainView.logout();
    }

    /**
     * Méthode appelée quand l'utilisateur veut voir la boîte "À propos"
     */
    public void showAbout() {
        aboutView.showAboutDialog();
    }

    /**
     * Vérifie si un utilisateur est connecté
     *
     * @return true si un utilisateur est connecté, false sinon
     */
    public boolean isUserConnected() {
        return connectedUser != null;
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
        if (menuView != null) {
            menuView.updateMenuState(true);
        }
    }

    @Override
    public void notifyLogout() {
        this.connectedUser = null;
        if (menuView != null) {
            menuView.updateMenuState(false);
        }
    }
}