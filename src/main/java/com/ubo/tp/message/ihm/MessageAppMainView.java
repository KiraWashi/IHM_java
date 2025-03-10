package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;


/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JFrame implements ISessionObserver {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Constructeur.
     */
    public MessageAppMainView(ISession session) {
        super("MessageApp");
        // S'abonner aux événements de session
        session.addObserver(this);

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
     * Affiche un sélecteur de répertoire
     */
    public File showDirectoryChooser() {
        JFileChooser fileChooser = new JFileChooser();

        // Configuration du sélecteur de répertoire
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionnez un répertoire d'échange");

        // Configurer le JFileChooser pour qu'il s'ouvre à la racine du projet
        File projectRoot = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(projectRoot);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }

    /**
     * Ferme l'application après confirmation
     */
    public int closeApp() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment quitter l'application ?",
                "Confirmer la fermeture",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return response;
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