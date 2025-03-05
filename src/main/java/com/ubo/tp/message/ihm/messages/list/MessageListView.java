package main.java.com.ubo.tp.message.ihm.messages.list;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListController;
import main.java.com.ubo.tp.message.ihm.messages.list.cell.MessageCellView;

/**
 * Composant d'affichage de la liste des messages
 */
public class MessageListView extends JPanel implements IDatabaseObserver, ISessionObserver {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Contrôleur de liste de messages
     */
    private MessageListController messageListController;

    /**
     * Session active
     */
    private ISession session;

    /**
     * Panneau contenant la liste des messages
     */
    private JPanel messagesPanel;

    /**
     * Champ de recherche
     */
    private JTextField searchField;

    /**
     * Bouton de recherche
     */
    private JButton searchButton;

    /**
     * Scroll pane pour la liste des messages
     */
    private JScrollPane scrollPane;

    /**
     * Format de date pour l'affichage
     */
    private SimpleDateFormat dateFormat;

    /**
     * Constructeur
     *
     * @param messageListController Contrôleur de liste de messages
     * @param session Session active
     */
    public MessageListView(MessageListController messageListController, ISession session) {
        this.messageListController = messageListController;
        this.session = session;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // S'abonner aux notifications de session
        this.session.addObserver(this);

        // Initialisation de l'interface
        this.initUI();

        // Chargement initial des messages
        this.refreshMessages();
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
        JLabel searchLabel = new JLabel("Rechercher :");
        searchLabel.setFont(searchLabel.getFont().deriveFont(Font.BOLD));
        searchPanel.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField();

        // Écouteur pour la recherche en temps réel
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchMessages(); }

            @Override
            public void removeUpdate(DocumentEvent e) { searchMessages(); }

            @Override
            public void changedUpdate(DocumentEvent e) { searchMessages(); }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);

        // Bouton de recherche
        searchButton = new JButton("Rechercher");
        try {
            searchButton.setIcon(new ImageIcon(ImageIO.read(new File(ICON_PATH + "searchIcon_20.png"))));
        } catch (IOException e) {
            // Icône non trouvée, pas critique
        }

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMessages();
            }
        });

        searchPanel.add(searchButton, BorderLayout.EAST);

        // Ajout du panneau de recherche
        this.add(searchPanel, BorderLayout.NORTH);

        // Création du panneau pour la liste des messages
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));

        // Ajout d'un scroll pane
        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Effectue une recherche et actualise la liste des messages
     */
    private void searchMessages() {
        String query = searchField.getText().trim();
        List<Message> messages = messageListController.searchMessages(query);
        displayMessages(messages);
    }

    /**
     * Actualise la liste des messages
     */
    public void refreshMessages() {
        // Utiliser la recherche si un terme est saisi, sinon afficher les messages pertinents
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            searchMessages();
        } else {
            List<Message> messages = messageListController.getRelevantMessages();
            displayMessages(messages);
        }
    }

    /**
     * Affiche la liste des messages
     *
     * @param messages Liste des messages à afficher
     */
    private void displayMessages(List<Message> messages) {
        // Effacer le panneau actuel
        messagesPanel.removeAll();

        // Vérifier s'il y a des messages
        if (messages.isEmpty()) {
            // Ajout d'un message d'information
            JLabel noMessagesLabel = new JLabel("Aucun message à afficher");
            noMessagesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noMessagesLabel.setFont(noMessagesLabel.getFont().deriveFont(Font.ITALIC));
            noMessagesLabel.setBorder(new EmptyBorder(20, 0, 0, 0));

            messagesPanel.add(Box.createVerticalGlue());
            messagesPanel.add(noMessagesLabel);
            messagesPanel.add(Box.createVerticalGlue());
        } else {
            // Ajouter chaque message
            for (Message message : messages) {
                // Passer la session au constructeur de MessageCellView
                MessageCellView cellView = new MessageCellView(message, dateFormat, session);
                messagesPanel.add(cellView);

                // Ajouter un séparateur
                messagesPanel.add(Box.createRigidArea(new Dimension(0, 1)));
                JSeparator separator = new JSeparator();
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                messagesPanel.add(separator);
                messagesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        // Ajouter un espace de remplissage en bas
        messagesPanel.add(Box.createVerticalGlue());

        // Rafraîchir l'affichage
        messagesPanel.revalidate();
        messagesPanel.repaint();

        // Défilement automatique vers le bas
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
        });
    }

    // Implémentation des méthodes de l'interface IDatabaseObserver

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        refreshMessages();
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        refreshMessages();
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        refreshMessages();
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        // Non utilisé pour ce composant
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        // Non utilisé pour ce composant
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        // Non utilisé pour ce composant
    }

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        refreshMessages();
    }

    @Override
    public void notifyLogout() {
        refreshMessages();
    }
}