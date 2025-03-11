package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.java.com.ubo.tp.message.core.notification.NotificationController;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeView;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListView;
import main.java.com.ubo.tp.message.ihm.notifications.NotificationButton;
import main.java.com.ubo.tp.message.ihm.notifications.NotificationView;
import main.java.com.ubo.tp.message.ihm.users.UserListView;

/**
 * Vue principale de l'application après connexion
 */
public class MainContentView extends JPanel {

    /**
     * Session active
     */
    private final ISession session;

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

    private final MessageListView messageListView;

    private final MessageComposeView messageComposeView;

    private final UserListView userListView;

    private final NotificationView notificationView;


    /**
     * Constructeur
     */
    public MainContentView(ISession session, NotificationController notificationController, MessageListView messageListView, MessageComposeView messageComposeView, UserListView userListView, NotificationView notificationView) {
        this.session = session;
        this.notificationController = notificationController;
        this.messageListView = messageListView;
        this.messageComposeView = messageComposeView;
        this.userListView = userListView;
        this.notificationView = notificationView;

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

        // Bouton de notification
        NotificationButton notificationButton = new NotificationButton(notificationController);

        // Panneau principal avec onglets
        tabbedPane = new JTabbedPane();

        // Onglet "Fil d'actualité"
        JPanel timelinePanel = new JPanel(new BorderLayout(0, 10));
        timelinePanel.add(messageListView, BorderLayout.CENTER);
        timelinePanel.add(messageComposeView, BorderLayout.SOUTH);
        tabbedPane.addTab("Fil d'actualité", timelinePanel);

        messageListView.refreshMessages();

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
    public void updateUIState() {
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

}