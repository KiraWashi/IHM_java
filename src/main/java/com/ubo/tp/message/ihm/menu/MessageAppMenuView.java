package main.java.com.ubo.tp.message.ihm.menu;
import main.java.com.ubo.tp.message.ihm.MessageApp;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Classe qui gère le menu de l'application MessageApp.
 */
public class MessageAppMenuView extends JMenuBar {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Logo de l'application
     */
    private ImageIcon appLogo;

    /**
     * Référence vers l'application
     */
    private MessageApp messageApp;

    /**
     * Référence vers la vue principale
     */
    private MessageAppMainView mainView;

    /**
     * Constructeur.
     *
     * @param messageApp L'application MessageApp
     * @param mainView La vue principale de l'application
     */
    public MessageAppMenuView(MessageApp messageApp, MessageAppMainView mainView) {
        this.messageApp = messageApp;
        this.mainView = mainView;

        // Chargement du logo
        try {
            appLogo = new ImageIcon(ImageIO.read(new File(ICON_PATH + "logo_20.png")));
        } catch (IOException e) {
            System.err.println("Impossible de charger le logo: " + e.getMessage());
        }

        // Initialisation du menu
        this.initMenu();
    }

    /**
     * Initialisation du menu de l'application
     */
    private void initMenu() {
        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");

        // Élément pour choisir le répertoire d'échange
        JMenuItem directoryItem = new JMenuItem("Choisir répertoire d'échange");
        try {
            directoryItem.setIcon(new ImageIcon(ImageIO.read(new File(ICON_PATH + "editIcon_20.png"))));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'icône de dossier: " + e.getMessage());
        }
        directoryItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainView.showDirectoryChooser();
            }
        });

        // Élément pour quitter
        JMenuItem exitItem = new JMenuItem("Quitter");
        try {
            exitItem.setIcon(new ImageIcon(ImageIO.read(new File(ICON_PATH + "exitIcon_20.png"))));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'icône de sortie: " + e.getMessage());
        }
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainView.closeApp();
            }
        });

        // Ajout des éléments au menu Fichier
        fileMenu.add(directoryItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Menu Aide
        JMenu helpMenu = new JMenu("?");

        // Élément À propos
        JMenuItem aboutItem = new JMenuItem("À propos");
        try {
            aboutItem.setIcon(new ImageIcon(ImageIO.read(new File(ICON_PATH + "logo_50.png"))));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'icône d'aide: " + e.getMessage());
        }
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });

        // Ajout des éléments au menu Aide
        helpMenu.add(aboutItem);

        // Ajout des menus à la barre de menu
        this.add(fileMenu);
        this.add(helpMenu);
    }

    /**
     * Affiche la boîte de dialogue "À propos"
     */
    private void showAboutDialog() {
        // Création du panneau personnalisé pour JOptionPane
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel du haut avec titre
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Ajout du titre
        JLabel titleLabel = new JLabel("UBO M2-TIIL");
        JLabel deptLabel = new JLabel("Département Informatique");
        topPanel.add(titleLabel);

        JPanel deptPanel = new JPanel();
        deptPanel.add(deptLabel);

        // Organisation du panneau
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(deptPanel, BorderLayout.SOUTH);

        // Création de la boîte de dialogue avec JOptionPane
        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                appLogo,
                new Object[]{"OK"},
                "OK"
        );

        // Création d'un JDialog à partir du JOptionPane
        JDialog dialog = optionPane.createDialog(mainView, "À propos");

        // Affichage de la boîte de dialogue
        dialog.setVisible(true);
    }
}