package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.menu.MessageAppMenuView;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JFrame implements IDatabaseObserver {

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
     * Barre de menu de l'application
     */
    private MessageAppMenuView appMenu;

    /**
     * Constructeur.
     */
    public MessageAppMainView(MessageApp messageApp) {
        super("MessageApp");
        this.messageApp = messageApp;
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

        // Initialisation du menu (maintenant via un composant séparé)
        this.appMenu = new MessageAppMenuView(messageApp, this);
        this.setJMenuBar(this.appMenu);

        // Création du panneau principal avec layout BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Création de la zone de texte pour les logs
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // Ajout de la zone de texte au panneau principal
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Définition du panneau principal comme contenu de la fenêtre
        this.setContentPane(mainPanel);
    }

    /**
     * Ajoute un message de log à la zone de texte
     */
    private void log(String message) {
        // Formater avec la date et l'heure
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // Ajouter le message au début de la zone de texte avec un timestamp
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionnez un répertoire d'échange");

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String directoryPath = fileChooser.getSelectedFile().getAbsolutePath();
            // Transmettre le répertoire sélectionné à l'application
            this.notifyDirectorySelected(directoryPath);
            return new File(directoryPath);
        }
        return null;
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
     * Notifie l'application qu'un répertoire a été sélectionné
     */
    private void notifyDirectorySelected(String directoryPath) {
        log("Répertoire sélectionné: " + directoryPath);
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
}