package main.java.com.ubo.tp.message.ihm.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Classe qui gère l'affichage du menu de l'application MessageApp.
 * Responsable uniquement de l'interface utilisateur du menu.
 */
public class MessageAppMenuView extends JMenuBar {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Contrôleur du menu
     */
    private MenuController menuController;

    /**
     * Menu Compte (dynamique selon connexion)
     */
    private JMenu accountMenu;

    /**
     * Élément pour se déconnecter
     */
    private JMenuItem logoutItem;

    /**
     * Élément pour voir son profil
     */
    private JMenuItem profileItem;

    /**
     * Constructeur.
     *
     * @param menuController Le contrôleur du menu
     */
    public MessageAppMenuView(MenuController menuController) {
        this.menuController = menuController;

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
                menuController.chooseDirectory();
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
                menuController.exit();
            }
        });

        // Ajout des éléments au menu Fichier
        fileMenu.add(directoryItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Menu Compte (affiché uniquement si un utilisateur est connecté)
        accountMenu = new JMenu("Compte");
        accountMenu.setVisible(false); // Caché par défaut

        // Élément pour voir son profil
        profileItem = new JMenuItem("Mon profil");
        try {
            profileItem.setIcon(new ImageIcon(ImageIO.read(new File(ICON_PATH + "logo_20.png"))));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'icône de profil: " + e.getMessage());
        }
        profileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuController.showProfile();
            }
        });

        // Élément pour se déconnecter
        logoutItem = new JMenuItem("Déconnexion");
        try {
            logoutItem.setIcon(new ImageIcon(ImageIO.read(new File(ICON_PATH + "exitIcon_20.png"))));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'icône de déconnexion: " + e.getMessage());
        }
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuController.logout();
            }
        });

        // Ajout des éléments au menu Compte
        accountMenu.add(profileItem);
        accountMenu.addSeparator();
        accountMenu.add(logoutItem);

        // Menu Aide
        JMenu helpMenu = new JMenu("?");

        // Élément À propos
        JMenuItem aboutItem = new JMenuItem("À propos");
        try {
            aboutItem.setIcon(new ImageIcon(ImageIO.read(new File(ICON_PATH + "logo_20.png"))));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'icône d'aide: " + e.getMessage());
        }
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuController.showAbout();
            }
        });

        // Ajout des éléments au menu Aide
        helpMenu.add(aboutItem);

        // Ajout des menus à la barre de menu
        this.add(fileMenu);
        this.add(accountMenu);
        this.add(helpMenu);

        // Vérification initiale de l'état de connexion
        updateMenuState(menuController.isUserConnected());
    }

    /**
     * Met à jour l'état du menu en fonction de la connexion
     * Cette méthode est appelée par le contrôleur
     *
     * @param isConnected true si un utilisateur est connecté, false sinon
     */
    public void updateMenuState(boolean isConnected) {
        accountMenu.setVisible(isConnected);

        // Rafraîchir la barre de menu
        this.revalidate();
        this.repaint();
    }
}