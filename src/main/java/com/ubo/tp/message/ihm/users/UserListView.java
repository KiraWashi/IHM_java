package main.java.com.ubo.tp.message.ihm.users;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.users.cell.UserCellView;

/**
 * Composant d'affichage de la liste des utilisateurs
 */
public class UserListView extends JPanel implements ISessionObserver {

    /**
     * Contrôleur d'utilisateurs
     */
    private final UserController userController;

    /**
     * Session active
     */
    private final ISession session;

    /**
     * Panneau contenant la liste des utilisateurs
     */
    private JPanel usersPanel;

    /**
     * Champ de recherche
     */
    private JTextField searchField;

    /**
     * Scroll pane pour la liste des utilisateurs
     */
    private JScrollPane scrollPane;

    /**
     * Constructeur
     *
     * @param userController Contrôleur d'utilisateurs
     * @param session Session active
     */
    public UserListView(UserController userController, ISession session) {
        this.userController = userController;
        this.session = session;

        // S'abonner aux notifications de session
        this.session.addObserver(this);

        // Initialisation de l'interface
        this.initUI();

        // Chargement initial des utilisateurs
        this.refreshUsers();
    }

    /**
     * Initialisation de l'interface utilisateur
     */
    private void initUI() {
        this.setLayout(new BorderLayout(0, 10));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panneau de recherche
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Label et champ de recherche
        JLabel searchLabel = new JLabel("Rechercher un utilisateur :");
        searchLabel.setFont(searchLabel.getFont().deriveFont(Font.BOLD));
        searchPanel.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField();

        // Écouteur pour la recherche en temps réel
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchUsers(); }

            @Override
            public void removeUpdate(DocumentEvent e) { searchUsers(); }

            @Override
            public void changedUpdate(DocumentEvent e) { searchUsers(); }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);

        // Bouton de recherche
        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchUsers();
            }
        });

        searchPanel.add(searchButton, BorderLayout.EAST);

        // Ajout du panneau de recherche
        this.add(searchPanel, BorderLayout.NORTH);

        // Création du panneau pour la liste des utilisateurs
        usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.setBackground(Color.WHITE);

        // Ajout d'un scroll pane
        scrollPane = new JScrollPane(usersPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Effectue une recherche et actualise la liste des utilisateurs
     */
    private void searchUsers() {
        String query = searchField.getText().trim();
        List<User> users = userController.searchUsers(query);
        displayUsers(users);
    }

    /**
     * Actualise la liste des utilisateurs
     */
    public void refreshUsers() {
        // Utiliser la recherche si un terme est saisi, sinon afficher tous les utilisateurs
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            searchUsers();
        } else {
            List<User> users = userController.getAllUsers(null);
            displayUsers(users);
        }
    }

    /**
     * Affiche la liste des utilisateurs
     *
     * @param users Liste des utilisateurs à afficher
     */
    private void displayUsers(List<User> users) {
        // Sauvegarder la position du scroll
        int verticalScrollValue = scrollPane.getVerticalScrollBar().getValue();

        // Effacer le panneau actuel
        usersPanel.removeAll();

        // Vérifier s'il y a des utilisateurs
        if (users.isEmpty()) {
            // Ajout d'un message d'information
            JLabel noUsersLabel = new JLabel("Aucun utilisateur à afficher");
            noUsersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noUsersLabel.setFont(noUsersLabel.getFont().deriveFont(Font.ITALIC));
            noUsersLabel.setBorder(new EmptyBorder(20, 0, 0, 0));

            usersPanel.add(Box.createVerticalGlue());
            usersPanel.add(noUsersLabel);
            usersPanel.add(Box.createVerticalGlue());
        } else {
            // Ajouter chaque utilisateur
            for (User user : users) {
                UserCellView cellView = new UserCellView(user, userController, session);
                usersPanel.add(cellView);

                // Ajouter un séparateur
                usersPanel.add(Box.createRigidArea(new Dimension(0, 1)));
                JSeparator separator = new JSeparator();
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                usersPanel.add(separator);
                usersPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        // Ajouter un espace de remplissage en bas
        usersPanel.add(Box.createVerticalGlue());

        // Rafraîchir l'affichage
        usersPanel.revalidate();
        usersPanel.repaint();

        // Restaurer la position du scroll
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(verticalScrollValue);
            }
        });
    }

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        refreshUsers();
    }

    @Override
    public void notifyLogout() {
        refreshUsers();
    }
}
