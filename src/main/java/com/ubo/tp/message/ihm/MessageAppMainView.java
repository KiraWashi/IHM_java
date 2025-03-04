package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JFrame implements IDatabaseObserver {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Logo de l'application
     */
    private ImageIcon appLogo;

    /**
     * Zone de texte pour afficher les logs
     */
    private JTextArea logArea;

    /**
     * Constructeur.
     */
    public MessageAppMainView() {
        super("MessageApp");
    }

    /**
     * Initialisation de la vue.
     */
    public void init() {
        // Configuration de la fenêtre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        // Chargement du logo
        try {
            Image logoImage = ImageIO.read(new File(ICON_PATH + "logo_20.png"));
            this.appLogo = new ImageIcon(logoImage);
            this.setIconImage(logoImage);
        } catch (IOException e) {
            System.err.println("Impossible de charger le logo: " + e.getMessage());
        }

        // Initialisation du menu
        this.initMenu();

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
     * Initialisation du menu de l'application
     */
    private void initMenu() {
        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();

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
                System.exit(0);
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
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // Définition de la barre de menu pour la fenêtre
        this.setJMenuBar(menuBar);
    }

    /**
     * Affiche la boîte de dialogue "À propos"
     */
    private void showAboutDialog() {
        // Création du panneau personnalisé pour JOptionPane
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel du haut avec logo et titre
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Ajout du logo
        if (appLogo != null) {
            Image img = appLogo.getImage();
            Image newImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            ImageIcon scaledLogo = new ImageIcon(newImg);
            JLabel logoLabel = new JLabel(scaledLogo);
            topPanel.add(logoLabel);
        }

        // Ajout du titre
        JLabel titleLabel = new JLabel("UBO M2-TIIL");
        JLabel deptLabel = new JLabel("Département Informatique");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        topPanel.add(titleLabel);
        topPanel.add(deptLabel);

        // Ajout du texte du département

        // Organisation du panneau
        panel.add(topPanel, BorderLayout.CENTER);

        // Création de la boîte de dialogue avec JOptionPane
        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{"OK"},
                "OK"
        );

        // Création d'un JDialog à partir du JOptionPane
        JDialog dialog = optionPane.createDialog(this, "À propos");

        // Affichage de la boîte de dialogue
        dialog.setVisible(true);
    }

    /**
     * Affiche un sélecteur de répertoire
     */
    private void showDirectoryChooser() {
        // Code du sélecteur de répertoire inchangé...
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionnez un répertoire d'échange");

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String directoryPath = fileChooser.getSelectedFile().getAbsolutePath();
            // Transmettre le répertoire sélectionné à l'application
            this.notifyDirectorySelected(directoryPath);
        }
    }

    /**
     * Notifie l'application qu'un répertoire a été sélectionné
     */
    private void notifyDirectorySelected(String directoryPath) {
        // Cette méthode sera implémentée par la classe MessageApp
        // Ici on pourrait lancer un événement ou appeler une méthode de callback
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
    // Modifiées pour utiliser la méthode log au lieu de System.out.println

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