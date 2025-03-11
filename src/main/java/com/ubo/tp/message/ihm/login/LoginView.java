package main.java.com.ubo.tp.message.ihm.login;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Composant gérant l'affichage de la connexion et de l'inscription des utilisateurs
 * Version JavaFX intégrée dans un conteneur Swing, compatible avec JDK 1.8
 */
public class LoginView extends JPanel {

    /**
     * Référence vers le contrôleur de login
     */
    private final LoginController loginController;

    /**
     * Panel JavaFX
     */
    private JFXPanel jfxPanel;

    /**
     * Scene JavaFX pour la connexion
     */
    private Scene loginScene;

    /**
     * Scene JavaFX pour l'inscription
     */
    private Scene registerScene;

    /**
     * Chemin d'accès à l'avatar sélectionné
     */
    private String selectedAvatarPath = "";

    /**
     * Label affichant le nom du fichier avatar sélectionné
     */
    private Label avatarFileLabel;

    /**
     * Constructeur
     *
     * @param loginController Contrôleur de login
     */
    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        this.setLayout(new BorderLayout());

        // Initialisation du UI
        initUI();
    }

    /**
     * Initialise l'interface utilisateur
     */
    private void initUI() {
        // Création du panel JavaFX
        jfxPanel = new JFXPanel();
        this.add(jfxPanel, BorderLayout.CENTER);

        // Initialisation de JavaFX dans un thread JavaFX
        // L'initialisation de JavaFX se fait automatiquement avec la création de JFXPanel
        Platform.runLater(this::initFX);
    }

    /**
     * Initialise les composants JavaFX
     */
    private void initFX() {
        // Création des scènes
        loginScene = createLoginScene();
        registerScene = createRegisterScene();

        // Définition de la scène initiale
        jfxPanel.setScene(loginScene);
    }

    /**
     * Crée la scène de connexion
     */
    private Scene createLoginScene() {
        // Conteneur principal
        VBox root = new VBox(15);
        root.setPadding(new Insets(20, 50, 20, 50));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        // Logo ou titre de l'application
        Label appLabel = new Label("MessageApp");
        appLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        appLabel.setTextFill(javafx.scene.paint.Color.web("#4285F4"));

        // Titre
        Label titleLabel = new Label("Connexion");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(javafx.scene.paint.Color.web("#333333"));

        // Champs de saisie
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setMaxWidth(300);

        // Tag utilisateur
        Label tagLabel = new Label("Tag utilisateur");
        tagLabel.setTextFill(javafx.scene.paint.Color.web("#555555"));
        TextField tagField = new TextField();
        tagField.setPromptText("Entrez votre tag");
        tagField.setPrefHeight(35);
        tagField.setPrefWidth(300);
        tagField.setStyle("-fx-background-radius: 4; -fx-border-radius: 4;");

        // Mot de passe
        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setTextFill(javafx.scene.paint.Color.web("#555555"));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");
        passwordField.setPrefHeight(35);
        passwordField.setPrefWidth(300);
        passwordField.setStyle("-fx-background-radius: 4; -fx-border-radius: 4;");

        // Ajout des champs au formulaire
        formGrid.add(tagLabel, 0, 0);
        formGrid.add(tagField, 0, 1);
        formGrid.add(passwordLabel, 0, 2);
        formGrid.add(passwordField, 0, 3);

        // Boutons
        Button loginButton = new Button("Se connecter");
        loginButton.setPrefWidth(300);
        loginButton.setPrefHeight(40);
        loginButton.setStyle(
                "-fx-background-color: #4285F4; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 4;"
        );

        Hyperlink registerLink = new Hyperlink("Créer un compte");
        registerLink.setStyle("-fx-text-fill: #4285F4;");

        // Action des boutons
        loginButton.setOnAction(e -> {
            String errorMessage = loginController.attemptLogin(tagField.getText(), passwordField.getText());
            if (errorMessage != null) {
                // Utiliser SwingUtilities pour les boîtes de dialogue Swing
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            LoginView.this,
                            errorMessage,
                            "Erreur de connexion",
                            JOptionPane.ERROR_MESSAGE
                    );
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            LoginView.this,
                            "Connexion réussie!",
                            "Connexion",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                });
            }
        });

        registerLink.setOnAction(e -> {
            // Basculer vers l'écran d'inscription
            Platform.runLater(() -> jfxPanel.setScene(registerScene));
        });

        // Ajout d'espaces
        root.getChildren().addAll(
                appLabel,
                new Region() {{ setMinHeight(10); }},
                titleLabel,
                new Region() {{ setMinHeight(15); }},
                formGrid,
                new Region() {{ setMinHeight(20); }},
                loginButton,
                registerLink
        );

        return new Scene(root, 400, 450);
    }

    /**
     * Crée la scène d'inscription
     */
    private Scene createRegisterScene() {
        // Conteneur principal
        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 50, 20, 50));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        // Titre
        Label titleLabel = new Label("Créer un compte");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(javafx.scene.paint.Color.web("#333333"));

        // Formulaire d'inscription
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setMaxWidth(300);

        // Nom
        Label nameLabel = new Label("Nom");
        nameLabel.setTextFill(javafx.scene.paint.Color.web("#555555"));
        TextField nameField = new TextField();
        nameField.setPromptText("Entrez votre nom");
        nameField.setPrefHeight(35);
        nameField.setStyle("-fx-background-radius: 4; -fx-border-radius: 4;");

        // Tag utilisateur
        Label tagLabel = new Label("Tag utilisateur");
        tagLabel.setTextFill(javafx.scene.paint.Color.web("#555555"));
        TextField tagField = new TextField();
        tagField.setPromptText("Choisissez un tag unique");
        tagField.setPrefHeight(35);
        tagField.setStyle("-fx-background-radius: 4; -fx-border-radius: 4;");

        // Mot de passe
        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setTextFill(javafx.scene.paint.Color.web("#555555"));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Créez un mot de passe");
        passwordField.setPrefHeight(35);
        passwordField.setStyle("-fx-background-radius: 4; -fx-border-radius: 4;");

        // Avatar
        Label avatarLabel = new Label("Avatar");
        avatarLabel.setTextFill(javafx.scene.paint.Color.web("#555555"));

        HBox avatarBox = new HBox(10);
        avatarBox.setAlignment(Pos.CENTER_LEFT);

        Button avatarButton = new Button("Choisir un avatar");
        avatarButton.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-background-radius: 4;"
        );

        avatarFileLabel = new Label("Aucun avatar sélectionné");
        avatarFileLabel.setTextFill(javafx.scene.paint.Color.web("#777777"));

        avatarBox.getChildren().addAll(avatarButton, avatarFileLabel);

        // Ajout des champs au formulaire
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 0, 1);
        formGrid.add(tagLabel, 0, 2);
        formGrid.add(tagField, 0, 3);
        formGrid.add(passwordLabel, 0, 4);
        formGrid.add(passwordField, 0, 5);
        formGrid.add(avatarLabel, 0, 6);
        formGrid.add(avatarBox, 0, 7);

        // Boutons
        Button registerButton = new Button("S'inscrire");
        registerButton.setPrefWidth(300);
        registerButton.setPrefHeight(40);
        registerButton.setStyle(
                "-fx-background-color: #4285F4; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 4;"
        );

        Hyperlink loginLink = new Hyperlink("Retour à la connexion");
        loginLink.setStyle("-fx-text-fill: #4285F4;");

        // Action des boutons
        avatarButton.setOnAction(e -> {
            // Ouvrir une boîte de dialogue Swing pour sélectionner un fichier
            SwingUtilities.invokeLater(() -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Sélectionner un avatar");
                fileChooser.setFileFilter(new FileNameExtensionFilter(
                        "Images", "jpg", "jpeg", "png", "gif"));

                int result = fileChooser.showOpenDialog(LoginView.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedAvatarPath = selectedFile.getAbsolutePath();

                    // Mise à jour du label dans le thread JavaFX
                    final String fileName = selectedFile.getName();
                    Platform.runLater(() -> {
                        avatarFileLabel.setText(fileName);
                    });
                }
            });
        });

        registerButton.setOnAction(e -> {
            String errorMessage = loginController.attemptRegister(
                    nameField.getText(),
                    tagField.getText(),
                    passwordField.getText(),
                    selectedAvatarPath
            );

            if (errorMessage != null) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            LoginView.this,
                            errorMessage,
                            "Erreur d'inscription",
                            JOptionPane.ERROR_MESSAGE
                    );
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            LoginView.this,
                            "Inscription réussie! Vous pouvez maintenant vous connecter avec vos identifiants.",
                            "Inscription",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                });

                // Réinitialiser les champs
                Platform.runLater(() -> {
                    nameField.clear();
                    tagField.clear();
                    passwordField.clear();
                    selectedAvatarPath = "";
                    avatarFileLabel.setText("Aucun avatar sélectionné");

                    // Retourner à l'écran de connexion
                    jfxPanel.setScene(loginScene);
                });
            }
        });

        loginLink.setOnAction(e -> {
            // Basculer vers l'écran de connexion
            Platform.runLater(() -> jfxPanel.setScene(loginScene));
        });

        // Assemblage final
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, loginLink);

        root.getChildren().addAll(
                titleLabel,
                new Region() {{ setMinHeight(10); }},
                formGrid,
                new Region() {{ setMinHeight(15); }},
                buttonBox
        );

        return new Scene(root, 400, 500);
    }
}