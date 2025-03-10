package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.java.com.ubo.tp.message.core.notification.NotificationController;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeController;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeView;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListController;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListView;
import main.java.com.ubo.tp.message.ihm.notifications.NotificationButton;
import main.java.com.ubo.tp.message.ihm.notifications.NotificationView;
import main.java.com.ubo.tp.message.ihm.users.UserController;
import main.java.com.ubo.tp.message.ihm.users.UserListView;

/**
 * Vue principale de l'application après connexion
 */
public class MainContentView extends JPanel implements ISessionObserver {

    /**
     * Session active
     */
    private final ISession session;

    /**
     * Contrôleur de composition de message
     */
    private final MessageComposeController messageComposeController;

    /**
     * Contrôleur de liste des messages
     */
    private final MessageListController messageListController;

    /**
     * Contrôleur des utilisateurs
     */
    private final UserController userController;

    /**
     * Panneau avec les onglets
     */
    private JTabbedPane tabbedPane;

    /**
     * Panneau pour l'utilisateur non connecté
     */
    private JPanel disconnectedPanel;

    /**
     * Classe qui gère les notification de l'application
     */
    private final NotificationController notificationController;


    /**
     * Constructeur
     */
    public MainContentView(ISession session, NotificationController notificationController, MessageComposeController messageComposeController,
                                MessageListController messageListController, UserController userController) {
        this.session = session;
        this.notificationController = notificationController;
        this.messageComposeController = messageComposeController;
        this.messageListController = messageListController;
        this.userController = userController;

        // S'abonner aux notifications de session
        this.session.addObserver(this);

        // Initialisation de l'interface
        this.initUI();

        // Mise à jour de l'état initial
        this.updateUIState();
    }

    /**
     * Initialisation de l'interface utilisateur
     */
    private void initUI() {
        this.setLayout(new BorderLayout());

        // Initialisation des vues
        MessageComposeView messageComposeView = new MessageComposeView(messageComposeController, session);
        MessageListView messageListView = new MessageListView(messageListController, session);
        UserListView userListView = new UserListView(userController, session);
        NotificationView notificationView = new NotificationView(notificationController);

        // Bouton de notification
        NotificationButton notificationButton = new NotificationButton(notificationController);

        // Panneau principal avec onglets
        tabbedPane = new JTabbedPane();

        // Onglet "Fil d'actualité"
        JPanel timelinePanel = new JPanel(new BorderLayout(0, 10));
        timelinePanel.add(messageListView, BorderLayout.CENTER);
        timelinePanel.add(messageComposeView, BorderLayout.SOUTH);
        tabbedPane.addTab("Fil d'actualité", timelinePanel);

        // Onglet "Utilisateurs"
        tabbedPane.addTab("Utilisateurs", userListView);

        // Onglet "Notifications"
        tabbedPane.addTab("", notificationView);
        tabbedPane.setTabComponentAt(2, notificationButton);

        // Écouteur de changement d'onglet
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Si on revient à l'onglet du fil d'actualité (index 0)
                if (tabbedPane.getSelectedIndex() == 0) {
                    // Rafraîchir la liste des messages
                    messageListView.refreshMessages();
                }
                // Si on arrive sur l'onglet des notifications (index 2)
                else if (tabbedPane.getSelectedIndex() == 2) {
                    // Marquer les notifications comme lues
                    notificationController.markAllAsRead();
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