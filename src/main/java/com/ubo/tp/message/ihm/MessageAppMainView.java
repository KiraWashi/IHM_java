package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.menu.directoryChoose.DirectoryChooserView;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeView;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.menu.directoryChoose.DirectoryChooserView;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JFrame implements IDatabaseObserver, ISessionObserver {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Référence à l'application
     */
    private MessageApp messageApp;

    /**
     * Panneau principal de l'application
     */
    private JPanel mainPanel;

    /**
     * Composant pour la sélection du répertoire d'échange
     */
    private DirectoryChooserView directoryChooserView;

    /**
     * Label pour la barre de statut
     */
    private JLabel statusLabel;

    /**
     * Constructeur.
     */
    public MessageAppMainView(MessageApp messageApp) {
        super("MessageApp");
        this.messageApp = messageApp;

        // S'abonner aux événements de session
        this.messageApp.getSession().addObserver(this);

        // Initialisation des composants
        this.directoryChooserView = new DirectoryChooserView(this, messageApp.mDirectoryController);

        // Création du panneau principal
        this.mainPanel = new JPanel(new BorderLayout());

        // Création de la barre de statut
        statusLabel = new JLabel("Prêt");
    }

    /**
     * Initialisation de la vue.
     */
    public void init() {
        // Configuration de la fenêtre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 700); // Fenêtre plus grande pour mieux voir les composants
        this.setLocationRelativeTo(null);

        try {
            // Chargement et définition de l'icône de l'application
            Image logoImage = ImageIO.read(new File(ICON_PATH + "logo_20.png"));
            this.setIconImage(logoImage);
        } catch (IOException e) {
            System.err.println("Impossible de charger le logo: " + e.getMessage());
        }

        // Ajout d'une barre de statut en bas de la fenêtre pour les notifications
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        this.add(statusPanel, BorderLayout.SOUTH);

        // Le menu est maintenant défini par le contrôleur de menu via MessageApp
    }

    /**
     * Retourne le panneau principal
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Affiche un sélecteur de répertoire
     */
    public File showDirectoryChooser() {
        return directoryChooserView.showDirectoryChooser();
    }

    /**
     * Ferme l'application après confirmation
     */
    public void closeApp() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment quitter l'application ?",
                "Confirmer la fermeture",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            this.messageApp.close();
        }
    }

    /**
     * Déconnecte l'utilisateur courant
     */
    public void logout() {
        if (messageApp.getSession().getConnectedUser() != null) {
            messageApp.getSession().disconnect();
        }
    }

    /**
     * Méthode permettant de rendre la fenêtre visible ou invisible.
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MessageAppMainView.super.setVisible(visible);
            }
        });
    }

    /**
     * Affiche une notification dans la barre de statut
     *
     * @param message Message à afficher
     */
    private void showNotification(String message) {
        // Formater avec la date et l'heure
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // Mettre à jour le label avec le message
        final String formattedMessage = "[" + timestamp + "] " + message;

        // Mise à jour du label de statut dans le thread d'interface graphique
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (statusLabel != null) {
                    statusLabel.setText(formattedMessage);
                }
            }
        });
    }

    // Implémentation des méthodes de l'interface IDatabaseObserver

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        // Afficher une notification pour le nouveau message
        showNotification("Nouveau message de @" + addedMessage.getSender().getUserTag());
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        // Afficher une notification pour le message supprimé
        showNotification("Message supprimé");
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        // Afficher une notification pour le message modifié
        showNotification("Message modifié");
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        // Afficher une notification pour le nouvel utilisateur
        showNotification("Nouvel utilisateur: @" + addedUser.getUserTag());
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        // Afficher une notification pour l'utilisateur supprimé
        showNotification("Utilisateur supprimé: @" + deletedUser.getUserTag());
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        // Afficher une notification pour l'utilisateur modifié
        showNotification("Utilisateur modifié: @" + modifiedUser.getUserTag());
    }

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        // Afficher une notification pour la connexion
        showNotification("Connexion: @" + connectedUser.getUserTag());

        // Mise à jour du titre de la fenêtre avec le nom de l'utilisateur
        this.setTitle("MessageApp - " + connectedUser.getName() + " (@" + connectedUser.getUserTag() + ")");
    }

    @Override
    public void notifyLogout() {
        // Afficher une notification pour la déconnexion
        showNotification("Déconnexion");

        // Réinitialisation du titre de la fenêtre
        this.setTitle("MessageApp");
    }
}