package main.java.com.ubo.tp.message.ihm.menu;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.user.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Vue du menu en JavaFX intégrée dans un composant Swing (JMenuBar)
 * Version optimisée utilisant davantage de fonctionnalités JavaFX
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
     * Composant pour l'intégration JavaFX dans Swing
     */
    private JFXPanel jfxPanel;

    /**
     * Scène JavaFX pour le menu
     */
    private Scene menuScene;

    /**
     * Barre de menu JavaFX
     */
    private MenuBar menuBar;

    /**
     * Menus JavaFX
     */
    private javafx.scene.control.Menu fileMenu;
    private javafx.scene.control.Menu accountMenu;
    private javafx.scene.control.Menu helpMenu;

    /**
     * Parent pour les boîtes de dialogue
     */
    private Component parentComponent;

    /**
     * Utilisateur connecté
     */
    private User connectedUser;

    /**
     * Logo par défaut pour l'application
     */
    private Image defaultLogo;

    /**
     * Logo par défaut au format Swing
     */
    private ImageIcon defaultLogoIcon;

    /**
     * Constructeur.
     *
     * @param menuController Le contrôleur du menu
     * @param session La session utilisateur
     */
    public MenuView(MenuController menuController, ISession session) {
        this.menuController = menuController;

        // Chargement du logo par défaut
        try {
            File logoFile = new File(ICON_PATH + "logo_20.png");
            if (logoFile.exists()) {
                this.defaultLogo = new Image(new FileInputStream(logoFile));
                this.defaultLogoIcon = new ImageIcon(logoFile.getAbsolutePath());
            } else {
                System.err.println("Logo non trouvé: " + logoFile.getAbsolutePath());
                this.defaultLogo = null;
                this.defaultLogoIcon = null;
            }
        } catch (IOException e) {
            System.err.println("Impossible de charger le logo par défaut: " + e.getMessage());
            this.defaultLogo = null;
            this.defaultLogoIcon = null;
        }

        // S'abonner aux changements de session
        session.addObserver(this);

        // Initialisation de l'interface
        initMenu();
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
     * Initialisation du menu JavaFX
     */
    private void initMenu() {
        // Créer un unique panneau JavaFX qui occupera toute la barre de menu
        jfxPanel = new JFXPanel();

        // Définir les dimensions du panneau
        jfxPanel.setPreferredSize(new Dimension(800, 25));

        // Initialiser JavaFX
        Platform.runLater(this::setupJavaFXMenu);

        // Ajouter le panneau JavaFX au menu Swing
        this.add(jfxPanel);
    }

    /**
     * Configuration de l'interface JavaFX du menu
     */
    private void setupJavaFXMenu() {
        // Créer la barre de menu JavaFX
        menuBar = new MenuBar();
        menuBar.setPrefHeight(25);

        // Créer les menus
        createFileMenu();
        createAccountMenu();
        createHelpMenu();

        // Ajouter les menus à la barre
        menuBar.getMenus().addAll(fileMenu, accountMenu, helpMenu);

        // Paramétrer la visibilité initiale (compte invisible si non connecté)
        accountMenu.setVisible(false);

        // Créer la scène avec la barre de menu
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        menuScene = new Scene(root);

        // Définir la scène
        jfxPanel.setScene(menuScene);
    }

    /**
     * Création du menu Fichier
     */
    private void createFileMenu() {
        // Créer le menu Fichier
        fileMenu = new javafx.scene.control.Menu("Fichier");

        // Créer les éléments de menu
        MenuItem directoryItem = createMenuItem("Choisir répertoire d'échange", "editIcon_20.png",
                event -> showDirectoryChooser());

        MenuItem exitItem = createMenuItem("Quitter", "exitIcon_20.png",
                event -> menuController.exit());

        // Ajouter les éléments au menu
        fileMenu.getItems().addAll(directoryItem, new SeparatorMenuItem(), exitItem);
    }

    /**
     * Création du menu Compte
     */
    private void createAccountMenu() {
        // Créer le menu Compte
        accountMenu = new javafx.scene.control.Menu("Compte");

        // Créer les éléments de menu
        MenuItem profileItem = createMenuItem("Mon profil", "logo_20.png",
                event -> {
                    if (connectedUser != null) {
                        showUserProfile(connectedUser);
                    }
                });

        MenuItem logoutItem = createMenuItem("Déconnexion", "exitIcon_20.png",
                event -> menuController.logout());

        // Ajouter les éléments au menu
        accountMenu.getItems().addAll(profileItem, new SeparatorMenuItem(), logoutItem);
    }

    /**
     * Création du menu Aide
     */
    private void createHelpMenu() {
        // Créer le menu Aide
        helpMenu = new javafx.scene.control.Menu("?");

        // Créer les éléments de menu
        MenuItem aboutItem = createMenuItem("À propos", "logo_20.png",
                event -> showAboutDialog());

        // Ajouter les éléments au menu
        helpMenu.getItems().add(aboutItem);
    }

    /**
     * Méthode utilitaire pour créer un élément de menu avec icône
     *
     * @param text Texte de l'élément de menu
     * @param iconName Nom du fichier icône
     * @param action Action à exécuter au clic
     * @return L'élément de menu créé
     */
    private MenuItem createMenuItem(String text, String iconName, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        MenuItem item = new MenuItem(text);

        // Ajouter l'icône si disponible
        try {
            File iconFile = new File(ICON_PATH + iconName);
            if (iconFile.exists()) {
                Image icon = new Image(new FileInputStream(iconFile));
                ImageView imageView = new ImageView(icon);
                imageView.setFitHeight(16);
                imageView.setFitWidth(16);
                item.setGraphic(imageView);
            }
        } catch (IOException e) {
            System.err.println("Impossible de charger l'icône " + iconName + ": " + e.getMessage());
        }

        // Ajouter l'action
        if (action != null) {
            item.setOnAction(action);
        }

        return item;
    }

    /**
     * Met à jour l'état du menu en fonction de la connexion
     *
     * @param isConnected true si un utilisateur est connecté, false sinon
     */
    public void updateMenuState(boolean isConnected) {
        // Mettre à jour l'état du menu dans le thread JavaFX
        Platform.runLater(() -> {
            accountMenu.setVisible(isConnected);
        });

        // Rafraîchir la barre de menu
        this.revalidate();
        this.repaint();
    }

    /**
     * Affiche un sélecteur de répertoire
     */
    public void showDirectoryChooser() {
        // Utiliser JFileChooser de Swing pour sélectionner un répertoire
        SwingUtilities.invokeLater(() -> {
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
                    showErrorDialog(
                            "Le répertoire sélectionné n'est pas valide.\n" +
                                    "Veuillez sélectionner un répertoire accessible en lecture et écriture.",
                            "Erreur de répertoire"
                    );
                }
            }
        });
    }

    /**
     * Affiche le profil d'un utilisateur avec une boîte de dialogue JavaFX
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

        // Création d'une boîte de dialogue JavaFX personnalisée
        Platform.runLater(() -> {
            // Créer une nouvelle étape pour la boîte de dialogue
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Profil de " + user.getName());
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Configuration de l'icône de la fenêtre
            if (defaultLogo != null) {
                dialogStage.getIcons().add(defaultLogo);
            }

            // Créer le contenu de la boîte de dialogue
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20));

            // Avatar/Image utilisateur
            ImageView avatarView = null;
            try {
                if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
                    File avatarFile = new File(user.getAvatarPath());
                    if (avatarFile.exists()) {
                        Image avatarImage = new Image(new FileInputStream(avatarFile));
                        avatarView = new ImageView(avatarImage);
                    }
                }

                if (avatarView == null && defaultLogo != null) {
                    avatarView = new ImageView(defaultLogo);
                }

                if (avatarView != null) {
                    avatarView.setFitHeight(64);
                    avatarView.setFitWidth(64);
                    avatarView.setPreserveRatio(true);

                    HBox avatarBox = new HBox(avatarView);
                    avatarBox.setAlignment(Pos.CENTER);
                    root.setTop(avatarBox);
                    BorderPane.setMargin(avatarBox, new Insets(0, 0, 15, 0));
                }
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de l'avatar: " + e.getMessage());
            }

            // Informations du profil
            VBox infoBox = new VBox(10);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label("Nom: " + user.getName());
            nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

            Label tagLabel = new Label("Tag: @" + user.getUserTag());

            Label followingLabel = new Label("Abonnements: " + followedCount);
            Label followersLabel = new Label("Followers: " + followersCount);
            Label messagesLabel = new Label("Messages: " + messagesCount);

            infoBox.getChildren().addAll(nameLabel, tagLabel,
                    new Separator(),
                    followingLabel, followersLabel, messagesLabel);

            root.setCenter(infoBox);

            // Bouton fermer
            Button closeButton = new Button("Fermer");
            closeButton.setOnAction(e -> dialogStage.close());

            HBox buttonBox = new HBox(closeButton);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            root.setBottom(buttonBox);
            BorderPane.setMargin(buttonBox, new Insets(15, 0, 0, 0));

            // Définir la scène
            Scene scene = new Scene(root, 300, 250);
            dialogStage.setScene(scene);

            // Afficher la boîte de dialogue
            dialogStage.showAndWait();
        });
    }

    /**
     * Affiche la boîte de dialogue "À propos" en JavaFX
     */
    public void showAboutDialog() {
        Platform.runLater(() -> {
            // Créer une nouvelle étape pour la boîte de dialogue
            javafx.stage.Stage aboutStage = new javafx.stage.Stage();
            aboutStage.setTitle("À propos");
            aboutStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Configuration de l'icône de la fenêtre
            if (defaultLogo != null) {
                aboutStage.getIcons().add(defaultLogo);
            }

            // Créer le contenu de la boîte de dialogue
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);

            // Logo
            if (defaultLogo != null) {
                ImageView logoView = new ImageView(defaultLogo);
                logoView.setFitHeight(64);
                logoView.setFitWidth(64);
                logoView.setPreserveRatio(true);
                root.getChildren().add(logoView);
            }

            // Titre
            Label titleLabel = new Label("UBO M2-TIIL");
            titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

            // Informations
            Label deptLabel = new Label("Département Informatique");
            Label appNameLabel = new Label(menuController.getAppName());
            Label versionLabel = new Label("Version " + menuController.getAppVersion());

            // Bouton fermer
            Button closeButton = new Button("Fermer");
            closeButton.setOnAction(e -> aboutStage.close());

            // Ajout des composants
            root.getChildren().addAll(
                    titleLabel,
                    new Separator(),
                    deptLabel,
                    appNameLabel,
                    versionLabel,
                    closeButton
            );

            // Définir la scène
            Scene scene = new Scene(root, 300, 250);
            aboutStage.setScene(scene);

            // Afficher la boîte de dialogue
            aboutStage.showAndWait();
        });
    }

    /**
     * Affiche une boîte de dialogue d'erreur en JavaFX
     *
     * @param message Message d'erreur
     * @param title Titre de la boîte de dialogue
     */
    private void showErrorDialog(String message, String title) {
        Platform.runLater(() -> {
            // Créer une nouvelle étape pour la boîte de dialogue
            javafx.stage.Stage errorStage = new javafx.stage.Stage();
            errorStage.setTitle(title);
            errorStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Créer le contenu de la boîte de dialogue
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);

            // Icône d'erreur
            Label iconLabel = new Label("⚠");
            iconLabel.setFont(Font.font("System", 36));
            iconLabel.setTextFill(javafx.scene.paint.Color.RED);

            // Message d'erreur
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(300);
            messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            // Bouton OK
            Button okButton = new Button("OK");
            okButton.setOnAction(e -> errorStage.close());

            // Ajout des composants
            root.getChildren().addAll(iconLabel, messageLabel, okButton);

            // Définir la scène
            Scene scene = new Scene(root, 350, 200);
            errorStage.setScene(scene);

            // Afficher la boîte de dialogue
            errorStage.showAndWait();
        });
    }

    @Override
    public void notifyLogin(User connectedUser) {
        this.connectedUser = connectedUser;
        this.updateMenuState(true);
    }

    @Override
    public void notifyLogout() {
        this.connectedUser = null;
        this.updateMenuState(false);
    }
}