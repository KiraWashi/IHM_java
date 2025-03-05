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

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JFrame implements IDatabaseObserver, ISessionObserver {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Zone de texte pour afficher les logs
     */
    private JTextArea logArea;

    /**
     * Référence à l'application
     */
    private MessageApp messageApp;

    /**
     * Panneau principal de l'application (après connexion)
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
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        try {
            // Chargement et définition de l'icône de l'application
            Image logoImage = ImageIO.read(new File(ICON_PATH + "logo_20.png"));
            this.setIconImage(logoImage);
        } catch (IOException e) {
            System.err.println("Impossible de charger le logo: " + e.getMessage());
        }

        // Création de la zone de texte pour les logs
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // Ajout de la zone de texte au panneau principal
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Le menu est maintenant défini par le contrôleur de menu via MessageApp
    }

    /**
     * Retourne le panneau principal
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Ajoute un message de log à la zone de texte
     */
    private void log(String message) {
        // Formater avec la date et l'heure
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // Ajouter le message à la zone de texte avec un timestamp
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + timestamp + "] " + message + "\n");
            // Auto-scroll vers le bas
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });

        // Également afficher dans la console pour le débogage
        System.out.println("[" + timestamp + "] " + message);
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

    // Implémentation des méthodes de l'interface IDatabaseObserver

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        log("NOUVEAU MESSAGE : " + addedMessage.getSender().getName() + " - " + addedMessage.getText());
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        log("MESSAGE SUPPRIMÉ : " + deletedMessage.getSender().getName() + " - " + deletedMessage.getText());
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        log("MESSAGE MODIFIÉ : " + modifiedMessage.getSender().getName() + " - " + modifiedMessage.getText());
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        log("NOUVEL UTILISATEUR : @" + addedUser.getUserTag() + " - " + addedUser.getName());
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        log("UTILISATEUR SUPPRIMÉ : @" + deletedUser.getUserTag() + " - " + deletedUser.getName());
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        log("UTILISATEUR MODIFIÉ : @" + modifiedUser.getUserTag() + " - " + modifiedUser.getName());
    }

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        log("CONNEXION : L'utilisateur @" + connectedUser.getUserTag() + " s'est connecté");

        // Mise à jour du titre de la fenêtre avec le nom de l'utilisateur
        this.setTitle("MessageApp - " + connectedUser.getName() + " (@" + connectedUser.getUserTag() + ")");
    }

    @Override
    public void notifyLogout() {
        log("DÉCONNEXION : L'utilisateur s'est déconnecté");

        // Réinitialisation du titre de la fenêtre
        this.setTitle("MessageApp");
    }

    // Dans MessageAppMainView.java, ajoutez cette méthode

    /**
     * Configure le panneau principal pour afficher les messages
     */
    public void setupMessagePanel() {
        // Nettoyer le panneau principal
        mainPanel.removeAll();

        // Mettre en place le layout
        mainPanel.setLayout(new BorderLayout());

        // Référence aux composants de message
        MessageListView messageListView = messageApp.mMessageListView;
        MessageComposeView messageComposeView = messageApp.mMessageComposeView;

        // Ajouter la liste des messages (occupe la majeure partie de l'écran)
        mainPanel.add(messageListView, BorderLayout.CENTER);

        // Ajouter le composant de saisie de message (en bas)
        mainPanel.add(messageComposeView, BorderLayout.SOUTH);

        // Rafraîchir l'affichage
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}