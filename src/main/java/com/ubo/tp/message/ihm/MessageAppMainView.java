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
public class MessageAppMainView extends JFrame implements ISessionObserver {

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

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        // Mise à jour du titre de la fenêtre avec le nom de l'utilisateur
        this.setTitle("MessageApp - " + connectedUser.getName() + " (@" + connectedUser.getUserTag() + ")");
    }

    @Override
    public void notifyLogout() {
        // Réinitialisation du titre de la fenêtre
        this.setTitle("MessageApp");
    }
}