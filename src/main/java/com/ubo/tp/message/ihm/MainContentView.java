package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeController;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeView;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListController;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListView;
import main.java.com.ubo.tp.message.ihm.users.UserController;
import main.java.com.ubo.tp.message.ihm.users.UserListView;

/**
 * Vue principale de l'application après connexion
 */
public class MainContentView extends JPanel implements ISessionObserver {

    /**
     * Base de données
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
     * Contrôleur de composition de message
     */
    private MessageComposeController messageComposeController;

    /**
     * Contrôleur de liste des messages
     */
    private MessageListController messageListController;

    /**
     * Contrôleur des utilisateurs
     */
    private UserController userController;

    /**
     * Vue de composition de message
     */
    private MessageComposeView messageComposeView;

    /**
     * Vue de liste des messages
     */
    private MessageListView messageListView;

    /**
     * Vue de liste des utilisateurs
     */
    private UserListView userListView;

    /**
     * Panneau avec les onglets
     */
    private JTabbedPane tabbedPane;

    /**
     * Panneau du fil d'actualité
     */
    private JPanel timelinePanel;

    /**
     * Panneau pour l'utilisateur non connecté
     */
    private JPanel disconnectedPanel;

    /**
     * Constructeur
     *
     * @param database Base de données
     * @param session Session active
     * @param entityManager Gestionnaire d'entités
     */
    public MainContentView(IDatabase database, ISession session, EntityManager entityManager) {
        this.database = database;
        this.session = session;
        this.entityManager = entityManager;

        // S'abonner aux notifications de session
        this.session.addObserver(this);

        // Initialisation des contrôleurs
        this.initControllers();

        // Initialisation de l'interface
        this.initUI();

        // Mise à jour de l'état initial
        this.updateUIState();
    }

    /**
     * Initialisation des contrôleurs
     */
    private void initControllers() {
        // Contrôleur pour la composition de messages
        this.messageComposeController = new MessageComposeController(database, entityManager, session);

        // Contrôleur pour la liste des messages
        this.messageListController = new MessageListController(database, session);

        // Contrôleur pour les utilisateurs
        this.userController = new UserController(database, session, entityManager);
    }

    /**
     * Initialisation de l'interface utilisateur
     */
    private void initUI() {
        this.setLayout(new BorderLayout());

        // Initialisation des vues
        this.messageComposeView = new MessageComposeView(messageComposeController, session);
        this.messageListView = new MessageListView(messageListController, session);
        this.userListView = new UserListView(userController, session);

        // Abonnement aux événements de la base de données
        database.addObserver(messageListView);
        database.addObserver(userListView);

        // Panneau principal avec onglets
        tabbedPane = new JTabbedPane();

        // Onglet "Fil d'actualité"
        timelinePanel = new JPanel(new BorderLayout(0, 10));

        // Placer la liste des messages au centre
        timelinePanel.add(messageListView, BorderLayout.CENTER);

        // Placer le champ de saisie en bas
        timelinePanel.add(messageComposeView, BorderLayout.SOUTH);

        tabbedPane.addTab("Fil d'actualité", timelinePanel);

        // Onglet "Utilisateurs"
        tabbedPane.addTab("Utilisateurs", userListView);

        // Ajouter l'écouteur de changement d'onglet
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Si on revient à l'onglet du fil d'actualité (index 0)
                if (tabbedPane.getSelectedIndex() == 0) {
                    // Rafraîchir la liste des messages
                    messageListView.refreshMessages();
                }
            }
        });

        // Panneau pour l'utilisateur non connecté
        disconnectedPanel = new JPanel(new GridBagLayout());
        JLabel disconnectedLabel = new JLabel("Veuillez vous connecter pour accéder à l'application");
        disconnectedLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        disconnectedPanel.add(disconnectedLabel);
    }

    /**
     * Met à jour l'état de l'interface en fonction de l'état de connexion
     */
    private void updateUIState() {
        boolean isConnected = session.getConnectedUser() != null;

        this.removeAll();

        if (isConnected) {
            // Utilisateur connecté : affichage des onglets
            this.add(tabbedPane, BorderLayout.CENTER);
        } else {
            // Utilisateur non connecté : affichage du message
            this.add(disconnectedPanel, BorderLayout.CENTER);
        }

        this.revalidate();
        this.repaint();
    }

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        updateUIState();
    }

    @Override
    public void notifyLogout() {
        updateUIState();
    }
}