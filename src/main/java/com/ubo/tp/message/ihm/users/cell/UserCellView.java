package main.java.com.ubo.tp.message.ihm.users.cell;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.message.IMessage;
import main.java.com.ubo.tp.message.datamodel.user.IUser;
import main.java.com.ubo.tp.message.datamodel.user.IUserListObserver;
import main.java.com.ubo.tp.message.datamodel.user.User;
import main.java.com.ubo.tp.message.ihm.users.UserController;

/**
 * Composant représentant une cellule d'utilisateur dans la liste
 */
public class UserCellView extends JPanel implements ISessionObserver, IUserListObserver {

    /**
     * Utilisateur à afficher
     */
    private final User user;

    /**
     * Contrôleur d'utilisateurs
     */
    private final UserController userController;

    /**
     * Session active
     */
    private final ISession session;

    /**
     * Bouton pour suivre/ne plus suivre
     */
    private JButton followButton;

    /**
     * Label pour le nombre de followers
     */
    private JLabel followersLabel;

    private JLabel followingLabel;

    /**
     * Constructeur
     *
     * @param user Utilisateur à afficher
     * @param userController Contrôleur d'utilisateurs
     * @param session Session active
     */
    public UserCellView(User user, UserController userController, ISession session) {
        this.user = user;
        this.userController = userController;
        this.session = session;

        // S'abonner aux notifications de session
        this.session.addObserver(this);

        this.initUI();
    }

    /**
     * Initialisation de l'interface utilisateur
     */
    private void initUI() {
        this.setLayout(new BorderLayout(10, 5));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        this.setBorder(new CompoundBorder(
                new EmptyBorder(5, 2, 5, 2),
                new CompoundBorder(
                        new LineBorder(new Color(220, 220, 220), 1, true),
                        new EmptyBorder(10, 10, 10, 10)
                )
        ));

        // Panneau gauche pour l'avatar
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setPreferredSize(new Dimension(60, 60));
        avatarPanel.setOpaque(false);

        JLabel avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);

        // Chargement de l'avatar s'il existe
        if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
            try {
                File avatarFile = new File(user.getAvatarPath());
                if (avatarFile.exists()) {
                    ImageIcon avatarIcon = new ImageIcon(ImageIO.read(avatarFile));
                    Image img = avatarIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(img));
                } else {
                    // Avatar par défaut
                    avatarLabel.setText(user.getName().substring(0, 1).toUpperCase());
                    avatarLabel.setFont(avatarLabel.getFont().deriveFont(Font.BOLD, 24));
                }
            } catch (IOException e) {
                // Avatar par défaut en cas d'erreur
                avatarLabel.setText(user.getName().substring(0, 1).toUpperCase());
                avatarLabel.setFont(avatarLabel.getFont().deriveFont(Font.BOLD, 24));
            }
        } else {
            // Avatar par défaut
            avatarLabel.setText(user.getName().substring(0, 1).toUpperCase());
            avatarLabel.setFont(avatarLabel.getFont().deriveFont(Font.BOLD, 24));
        }

        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        this.add(avatarPanel, BorderLayout.WEST);

        // Panneau central pour les informations de l'utilisateur
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Nom et tag de l'utilisateur
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        namePanel.setOpaque(false);
        namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14));

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setForeground(new Color(100, 100, 100));

        namePanel.add(nameLabel);
        namePanel.add(tagLabel);
        infoPanel.add(namePanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Statistiques (followers, following)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int followersCount = userController.getFollowersCount(user);
        followersLabel = new JLabel(followersCount + " followers");

        int followingCount = userController.getFollowingCount(user) - 1;

         //Label pour le nombre d'utilisateurs suivis

        followingLabel = new JLabel(followingCount + " abonnements");

        statsPanel.add(followersLabel);
        statsPanel.add(followingLabel);
        infoPanel.add(statsPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Bouton suivre/ne plus suivre
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        followButton = new JButton();
        updateFollowButton();

        followButton.addActionListener(e -> toggleFollow());

        buttonPanel.add(followButton);
        infoPanel.add(buttonPanel);

        this.add(infoPanel, BorderLayout.CENTER);

        // Mise à jour de l'état initial
        updateUIState();
    }

    /**
     * Met à jour l'état du bouton suivre/ne plus suivre
     */
    private void updateFollowButton() {
        User currentUser = session.getConnectedUser();
        boolean isConnected = currentUser != null;
        boolean isSelf = isConnected && currentUser.equals(user);
        boolean isFollowing = userController.isFollowing(user);

        // Désactiver le bouton si on n'est pas connecté ou si c'est l'utilisateur lui-même
        followButton.setEnabled(isConnected && !isSelf);

        if (!isConnected || isSelf) {
            followButton.setText("Suivre");
        } else {
            followButton.setText(isFollowing ? "Ne plus suivre" : "Suivre");
            followButton.setForeground(isFollowing ? new Color(150, 0, 0) : new Color(0, 100, 0));
        }

        // Mise à jour des compteurs
        int followersCount = userController.getFollowersCount(user);
        followersLabel.setText(followersCount + " followers");

        int followingCount = userController.getFollowingCount(user);
        followingLabel.setText(followingCount + " abonnements");

    }

    /**
     * Bascule entre suivre et ne plus suivre l'utilisateur
     */
    private void toggleFollow() {
        String error;
        boolean isFollowing = userController.isFollowing(user);

        if (isFollowing) {
            error = userController.unfollowUser(user);
        } else {
            error = userController.followUser(user);
        }

        if (error != null) {
            JOptionPane.showMessageDialog(
                    this,
                    error,
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            // Mise à jour de l'affichage
            updateFollowButton();

            // Mise à jour du nombre de followers
            int followersCount = userController.getFollowersCount(user);
            followersLabel.setText(followersCount + " followers");
        }
    }

    /**
     * Met à jour l'état de l'interface
     */
    private void updateUIState() {
        updateFollowButton();
    }

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        updateUIState();
    }

    @Override
    public void notifyLogout() {
        updateUIState();
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        updateUIState();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        updateUIState();
    }

    @Override
    public void notifyRefreshUser() {
        updateUIState();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        updateUIState();
    }
}