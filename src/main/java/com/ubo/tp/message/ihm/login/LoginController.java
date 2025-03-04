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
        this.session = session;

        // Création de la vue de login
        this.loginView = new LoginView(database, entityManager, session);

        // Création d'un panneau pour le contenu principal (après connexion)
        this.mainContentView = new JPanel(new BorderLayout());

        // S'inscrire comme observateur de la session
        session.addObserver(this);
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
        }
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
     * Définit le panneau de contenu principal à afficher après connexion
     *
     * @param mainView Panneau du contenu principal
     */
    public void setMainContentView(JPanel mainView) {
        this.mainContentView.removeAll();
        this.mainContentView.add(mainView, BorderLayout.CENTER);
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