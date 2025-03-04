package main.java.com.ubo.tp.message.ihm.login;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Composant gérant la connexion et l'inscription des utilisateurs
 */
public class LoginView extends JPanel {

    /**
     * Référence vers la base de données
     */
    private IDatabase database;

    /**
     * Référence vers le gestionnaire d'entités
     */
    private EntityManager entityManager;

    /**
     * Référence vers la session
     */
    private ISession session;

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
     * @param database Base de données de l'application
     * @param entityManager Gestionnaire d'entités
     * @param session Session de l'application
     */
    public LoginView(IDatabase database, EntityManager entityManager, ISession session) {
        this.database = database;
        this.entityManager = entityManager;
        this.session = session;

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
                attemptLogin(tagField.getText(), new String(passwordField.getPassword()));
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
                attemptRegister(
                        nameField.getText(),
                        tagField.getText(),
                        new String(passwordField.getPassword()),
                        selectedAvatarPath
                );
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

    /**
     * Tente de connecter l'utilisateur
     */
    private void attemptLogin(String tag, String password) {
        if (tag.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer votre tag utilisateur",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Recherche de l'utilisateur dans la base de données par son tag
        User foundUser = null;
        for (User user : database.getUsers()) {
            if (user.getUserTag().equals(tag)) {
                foundUser = user;
                break;
            }
        }

        if (foundUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Utilisateur introuvable",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérification du mot de passe (dans un vrai système, il faudrait hasher)
        if (!foundUser.getUserPassword().equals(password)) {
            JOptionPane.showMessageDialog(this,
                    "Mot de passe incorrect",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Connexion réussie
        session.connect(foundUser);
        JOptionPane.showMessageDialog(this,
                "Connexion réussie. Bienvenue " + foundUser.getName() + "!",
                "Connexion",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tente d'inscrire un nouvel utilisateur
     */
    private void attemptRegister(String name, String tag, String password, String avatarPath) {
        // Validation des champs obligatoires
        if (name.isEmpty() || tag.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le nom et le tag sont obligatoires",
                    "Erreur d'inscription",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérification que le tag n'existe pas déjà
        for (User existingUser : database.getUsers()) {
            if (existingUser.getUserTag().equals(tag)) {
                JOptionPane.showMessageDialog(this,
                        "Ce tag utilisateur existe déjà",
                        "Erreur d'inscription",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Création du nouvel utilisateur
        UUID newUserId = UUID.randomUUID();
        Set<String> emptyFollows = new HashSet<>();
        User newUser = new User(newUserId, tag, password, name, emptyFollows, avatarPath);

        // Ajout à la base de données
        database.addUser(newUser);

        // Génération du fichier utilisateur
        entityManager.writeUserFile(newUser);

        // Connexion automatique de l'utilisateur
        session.connect(newUser);

        JOptionPane.showMessageDialog(this,
                "Inscription réussie. Bienvenue " + name + "!",
                "Inscription",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
