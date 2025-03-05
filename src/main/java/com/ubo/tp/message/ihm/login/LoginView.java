package main.java.com.ubo.tp.message.ihm.login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


/**
 * Composant gérant l'affichage de la connexion et de l'inscription des utilisateurs
 */
public class LoginView extends JPanel {

    /**
     * Référence vers le contrôleur de login
     */
    private LoginController loginController;

    /**
     * Panneau de contenu principal
     */
    private JPanel contentPanel;

    /**
     * CardLayout pour basculer entre les écrans de connexion et d'inscription
     */
    private CardLayout cardLayout;

    /**
     * Chemins d'accès à l'avatar sélectionné
     */
    private String selectedAvatarPath = "";

    /**
     * Constante: nom du card pour la connexion
     */
    private static final String LOGIN_CARD = "LOGIN";

    /**
     * Constante: nom du card pour l'inscription
     */
    private static final String REGISTER_CARD = "REGISTER";

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
        // Configuration du panel principal avec CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Création des écrans
        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();

        // Ajout des écrans au CardLayout
        contentPanel.add(loginPanel, LOGIN_CARD);
        contentPanel.add(registerPanel, REGISTER_CARD);

        // Ajoute le contenu au panel principal
        this.add(contentPanel, BorderLayout.CENTER);

        // Affiche l'écran de connexion par défaut
        cardLayout.show(contentPanel, LOGIN_CARD);
    }

    /**
     * Crée le panneau de connexion
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Titre
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Champs de saisie
        JLabel tagLabel = new JLabel("Tag utilisateur:");
        JTextField tagField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Mot de passe:");
        JPasswordField passwordField = new JPasswordField(20);

        // Boutons
        JButton loginButton = new JButton("Se connecter");
        JButton goToRegisterButton = new JButton("Créer un compte");

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(goToRegisterButton);

        // Actions des boutons
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String errorMessage = loginController.attemptLogin(tagField.getText(), new String(passwordField.getPassword()));
                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(LoginView.this,
                            errorMessage,
                            "Erreur de connexion",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LoginView.this,
                            "Connexion réussie!",
                            "Connexion",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        goToRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, REGISTER_CARD);
            }
        });

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(tagLabel, gbc);

        gbc.gridx = 1;
        panel.add(tagField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    /**
     * Crée le panneau d'inscription
     */
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Titre
        JLabel titleLabel = new JLabel("Créer un compte");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Champs de saisie
        JLabel nameLabel = new JLabel("Nom:");
        JTextField nameField = new JTextField(20);

        JLabel tagLabel = new JLabel("Tag utilisateur:");
        JTextField tagField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Mot de passe:");
        JPasswordField passwordField = new JPasswordField(20);

        JLabel avatarLabel = new JLabel("Avatar:");
        JButton avatarButton = new JButton("Choisir un avatar");
        JLabel avatarPathLabel = new JLabel("Aucun avatar sélectionné");

        // Panel pour l'avatar
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        avatarPanel.add(avatarButton);
        avatarPanel.add(avatarPathLabel);

        // Boutons
        JButton registerButton = new JButton("S'inscrire");
        JButton backToLoginButton = new JButton("Retour à la connexion");

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);

        // Actions des boutons
        avatarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));

                int result = fileChooser.showOpenDialog(panel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedAvatarPath = selectedFile.getAbsolutePath();
                    avatarPathLabel.setText(selectedFile.getName());
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String errorMessage = loginController.attemptRegister(
                        nameField.getText(),
                        tagField.getText(),
                        new String(passwordField.getPassword()),
                        selectedAvatarPath
                );

                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(LoginView.this,
                            errorMessage,
                            "Erreur d'inscription",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LoginView.this,
                            "Inscription réussie. Bienvenue " + nameField.getText() + "!",
                            "Inscription",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, LOGIN_CARD);
            }
        });

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(tagLabel, gbc);

        gbc.gridx = 1;
        panel.add(tagField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(avatarLabel, gbc);

        gbc.gridx = 1;
        panel.add(avatarPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        return panel;
    }
}