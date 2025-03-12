package main.java.com.ubo.tp.message.ihm.messages.list.cell;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Composant combinant Swing et JavaFX pour un rendu de message amélioré
 */
public class MessageCellView extends JPanel {

    /**
     * Message à afficher
     */
    private final Message message;

    /**
     * Format de date
     */
    private final SimpleDateFormat dateFormat;

    /**
     * Session active
     */
    private final ISession session;

    /**
     * Panneau JavaFX pour le rendu
     */
    private JFXPanel fxPanel;

    /**
     * Constructeur
     *
     * @param message Message à afficher
     * @param dateFormat Format de date pour l'affichage
     * @param session Session active
     */
    public MessageCellView(Message message, SimpleDateFormat dateFormat, ISession session) {
        this.message = message;
        this.dateFormat = dateFormat;
        this.session = session;

        initSwingUI();
    }

    /**
     * Initialisation de l'interface Swing
     */
    private void initSwingUI() {
        this.setLayout(new BorderLayout());

        // Créer le panneau JavaFX
        fxPanel = new JFXPanel();
        fxPanel.setPreferredSize(new Dimension(500, 100));

        this.add(fxPanel, BorderLayout.CENTER);

        // Initialiser le contenu JavaFX
        Platform.runLater(this::initFXContent);
    }

    /**
     * Initialisation du contenu JavaFX
     */
    private void initFXContent() {
        // Récupération des données du message
        User sender = message.getSender();
        String messageText = message.getText();
        Date messageDate = new Date(message.getEmissionDate());

        // Vérifier si le message est de l'utilisateur connecté
        User connectedUser = session.getConnectedUser();
        boolean isCurrentUserMessage = message.getSender().equals(connectedUser);

        // Créer la racine JavaFX
        HBox root = new HBox(10);
        root.setPadding(new Insets(10));
        root.setStyle(
                "-fx-background-color: " + (isCurrentUserMessage ? "#e6f3e6" : "white") + "; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: " + (isCurrentUserMessage ? "#c0e0c0" : "#e0e0e0") + "; " +
                        "-fx-border-radius: 8px;"
        );

        // Avatar
        ImageView avatarView = createAvatarView(sender);

        // Conteneur principal pour le message
        VBox messageContainer = new VBox(5);
        messageContainer.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(messageContainer, Priority.ALWAYS);

        // En-tête du message
        HBox headerBox = createMessageHeader(sender, messageDate);

        // Corps du message
        TextArea messageTextArea = createMessageTextArea(messageText, isCurrentUserMessage);

        // Assemblage du conteneur
        messageContainer.getChildren().addAll(headerBox, messageTextArea);

        // Ajout des composants à la racine
        root.getChildren().addAll(avatarView, messageContainer);

        // Créer et définir la scène
        Scene scene = new Scene(root);
        fxPanel.setScene(scene);
    }

    /**
     * Crée la vue de l'avatar
     */
    private ImageView createAvatarView(User sender) {
        ImageView avatarView = new ImageView();
        avatarView.setFitWidth(50);
        avatarView.setFitHeight(50);
        avatarView.setPreserveRatio(true);

        try {
            // Charger l'avatar
            if (sender.getAvatarPath() != null && !sender.getAvatarPath().isEmpty()) {
                File avatarFile = new File(sender.getAvatarPath());
                if (avatarFile.exists()) {
                    Image avatarImage = new Image(new FileInputStream(avatarFile), 50, 50, true, true);
                    avatarView.setImage(avatarImage);

                    // Clip circulaire
                    Circle clip = new Circle(25, 25, 25);
                    avatarView.setClip(clip);
                } else {
                    avatarView = createDefaultAvatarView(sender);
                }
            } else {
                avatarView = createDefaultAvatarView(sender);
            }
        } catch (Exception e) {
            avatarView = createDefaultAvatarView(sender);
        }

        return avatarView;
    }

    /**
     * Crée un avatar par défaut
     */
    private ImageView createDefaultAvatarView(User sender) {
        ImageView avatarView = new ImageView();
        avatarView.setFitWidth(50);
        avatarView.setFitHeight(50);

        // Créer un avatar textuel
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(50, 50);
        avatarPane.setStyle("-fx-background-color: #4285F4; -fx-background-radius: 25px;");

        Label initialLabel = new Label(sender.getName().substring(0, 1).toUpperCase());
        initialLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        initialLabel.setTextFill(Color.WHITE);

        avatarPane.getChildren().add(initialLabel);

        // Capture de l'image
        Scene tempScene = new Scene(avatarPane, 50, 50);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image image = avatarPane.snapshot(params, null);

        avatarView.setImage(image);

        // Clip circulaire
        Circle clip = new Circle(25, 25, 25);
        avatarView.setClip(clip);

        return avatarView;
    }

    /**
     * Crée l'en-tête du message
     */
    private HBox createMessageHeader(User sender, Date messageDate) {
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Nom de l'utilisateur
        Label nameLabel = new Label(sender.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web("#333333"));

        // Tag de l'utilisateur
        Label tagLabel = new Label("@" + sender.getUserTag());
        tagLabel.setFont(Font.font("Arial", 12));
        tagLabel.setTextFill(Color.web("#666666"));

        // Date du message
        Label dateLabel = new Label(dateFormat.format(messageDate));
        dateLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 10));
        dateLabel.setTextFill(Color.web("#999999"));

        headerBox.getChildren().addAll(nameLabel, tagLabel, dateLabel);
        return headerBox;
    }

    /**
     * Crée la zone de texte du message
     */
    private TextArea createMessageTextArea(String messageText, boolean isCurrentUserMessage) {
        TextArea messageTextArea = new TextArea(messageText);
        messageTextArea.setWrapText(true);
        messageTextArea.setEditable(false);

        // Configuration détaillée du style
        messageTextArea.setStyle(
                "-fx-control-inner-background: " + (isCurrentUserMessage ? "#e6f3e6" : "white") + "; " + // Couleur de fond interne
                        "-fx-background-color: " + (isCurrentUserMessage ? "#e6f3e6" : "white") + "; " + // Couleur de fond générale
                        "-fx-text-box-border: transparent; " + // Bordure transparente
                        "-fx-control-inner-border: transparent; " + // Bordure interne transparente
                        "-fx-border-color: transparent; " + // Bordure externe transparente
                        "-fx-font-size: 13px; " +
                        "-fx-background-radius: 8px;" +
                        "-fx-background-insets: 0;"
        );

        messageTextArea.setPrefRowCount(2);
        messageTextArea.setMaxWidth(300);

        return messageTextArea;
    }
}