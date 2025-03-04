package main.java.com.ubo.tp.message.ihm.menu.profile;

import main.java.com.ubo.tp.message.datamodel.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Classe qui gère l'affichage du profil utilisateur
 */
public class ProfileView {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Référence vers la fenêtre principale
     */
    private JFrame parentFrame;

    /**
     * Contrôleur pour la gestion du profil
     */
    private ProfileController profileController;

    /**
     * Logo par défaut à utiliser si l'avatar n'est pas disponible
     */
    private ImageIcon defaultLogo;

    /**
     * Constructeur
     *
     * @param parentFrame Fenêtre parente pour les dialogues
     * @param profileController Contrôleur pour la gestion du profil
     */
    public ProfileView(JFrame parentFrame, ProfileController profileController) {
        this.parentFrame = parentFrame;
        this.profileController = profileController;

        // Chargement du logo par défaut
        try {
            this.defaultLogo = new ImageIcon(ImageIO.read(new File(ICON_PATH + "logo_20.png")));
        } catch (IOException e) {
            System.err.println("Impossible de charger le logo par défaut: " + e.getMessage());
            this.defaultLogo = new ImageIcon();
        }
    }

    /**
     * Affiche le profil de l'utilisateur connecté
     *
     * @param user L'utilisateur dont le profil doit être affiché
     */
    public void showUserProfile(User user) {
        if (user == null) {
            return;
        }

        // Obtenir les données via le contrôleur
        int followersCount = profileController.getFollowersCount(user);
        int followedCount = profileController.getFollowedCount(user);
        int messagesCount = profileController.getUserMessagesCount(user);

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
                parentFrame,
                profileInfo.toString(),
                "Profil de " + user.getName(),
                JOptionPane.INFORMATION_MESSAGE,
                profileIcon
        );
    }
}