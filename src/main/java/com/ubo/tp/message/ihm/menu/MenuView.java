package main.java.com.ubo.tp.message.ihm.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Vue unifiée pour le menu de l'application.
 * Combine les fonctionnalités de MessageAppMenuView, ProfileView, DirectoryChooserView et AboutView.
 */
public class MenuView extends JMenuBar implements ISessionObserver {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Contrôleur du menu
     */
    private final MenuController menuController;

    /**
     * Menu Compte (dynamique selon connexion)
     */
    private JMenu accountMenu;

    /**
     * Logo par défaut pour l'application
     */
    private ImageIcon defaultLogo;

    /**
     * Parent pour les boîtes de dialogue
     */
    private Component parentComponent;

    private User connectedUser;

    /**
     * Constructeur.
     *
     * @param menuController Le contrôleur du menu
     */
    public MenuView(MenuController menuController, ISession session) {
        this.menuController = menuController;

        // Chargement du logo par défaut
        try {
            this.defaultLogo = new ImageIcon(ImageIO.read(new File(ICON_PATH + "logo_20.png")));
        } catch (IOException e) {
            System.err.println("Impossible de charger le logo par défaut: " + e.getMessage());
            this.defaultLogo = new ImageIcon();
        }

        session.addObserver(this);


        // Initialisation du menu
        this.initMenu();
    }

    /**
     * Définit le composant parent pour les boîtes de dialogue
     *
     * @param parent Composant parent
     */
    public void setParentComponent(Component parent) {
        this.parentComponent = parent;
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
                showDirectoryChooser();
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
        JMenuItem profileItem = new JMenuItem("Mon profil");
        try {
            profileItem.setIcon(new ImageIcon(ImageIO.read(new File(ICON_PATH + "logo_20.png"))));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'icône de profil: " + e.getMessage());
        }
        profileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connectedUser != null) {
                    showUserProfile(connectedUser);
                }
            }
        });

        // Élément pour se déconnecter
        JMenuItem logoutItem = new JMenuItem("Déconnexion");
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
                showAboutDialog();
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

    /**
     * Affiche un sélecteur de répertoire
     *
     * @return Le fichier sélectionné ou null si aucun répertoire n'a été choisi
     */
    public void showDirectoryChooser() {
        // Création du sélecteur de fichier
        JFileChooser fileChooser = new JFileChooser();

        // Configurer le JFileChooser pour qu'il s'ouvre à la racine du projet
        File projectRoot = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(projectRoot);

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionnez un répertoire d'échange");

        // Affichage du sélecteur de fichier
        int returnValue = fileChooser.showOpenDialog(parentComponent);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Vérifier si le répertoire est valide
            if (menuController.isValidExchangeDirectory(selectedFile)) {
                menuController.changeDirectory(selectedFile.getAbsolutePath());
            } else {
                // Affichage d'un message d'erreur
                JOptionPane.showMessageDialog(
                        parentComponent,
                        "Le répertoire sélectionné n'est pas valide.\n" +
                                "Veuillez sélectionner un répertoire accessible en lecture et écriture.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    /**
     * Affiche le profil d'un utilisateur
     *
     * @param user L'utilisateur dont le profil doit être affiché
     */
    public void showUserProfile(User user) {
        if (user == null) {
            return;
        }

        // Obtenir les données via le contrôleur
        int followersCount = menuController.getFollowersCount(user);
        int followedCount = menuController.getFollowedCount(user);
        int messagesCount = menuController.getUserMessagesCount(user);

        // Construction des informations du profil
        StringBuilder profileInfo = new StringBuilder();
        profileInfo.append("Nom: ").append(user.getName()).append("\n");
        profileInfo.append("Tag: @").append(user.getUserTag()).append("\n");
        profileInfo.append("Abonnements: ").append(followedCount).append("\n");
        profileInfo.append("Followers: ").append(followersCount).append("\n");
        profileInfo.append("Messages: ").append(messagesCount);

        // Création de l'icône à partir de l'avatar de l'utilisateur ou utilisation du logo par défaut
        ImageIcon profileIcon = defaultLogo;
        if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
            try {
                profileIcon = new ImageIcon(ImageIO.read(new File(user.getAvatarPath())));
            } catch (IOException e) {
                System.err.println("Impossible de charger l'avatar: " + e.getMessage());
            }
        }

        // Affichage de la boîte de dialogue avec les informations du profil
        JOptionPane.showMessageDialog(
                parentComponent,
                profileInfo.toString(),
                "Profil de " + user.getName(),
                JOptionPane.INFORMATION_MESSAGE,
                profileIcon
        );
    }

    /**
     * Affiche la boîte de dialogue "À propos"
     */
    public void showAboutDialog() {
        // Création du panneau personnalisé pour JOptionPane
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel du haut avec titre
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("UBO M2-TIIL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel);

        // Panel du centre avec informations supplémentaires
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel deptLabel = new JLabel("Département Informatique");
        deptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appNameLabel = new JLabel(menuController.getAppName());
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Version " + menuController.getAppVersion());
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(deptLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(appNameLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(versionLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Organisation du panneau
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Création de la boîte de dialogue avec JOptionPane
        JOptionPane.showMessageDialog(
                parentComponent,
                panel,
                "À propos",
                JOptionPane.INFORMATION_MESSAGE,
                defaultLogo
        );
    }

    @Override
    public void notifyLogin(User connectedUser) {
        this.connectedUser = connectedUser;
        this.updateMenuState(true);
    }

    @Override
    public void notifyLogout() {
        this.updateMenuState(false);
    }
}