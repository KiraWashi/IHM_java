package main.java.com.ubo.tp.message.ihm.messages.compose;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Composant pour la saisie et l'envoi de messages
 */
public class MessageComposeView extends JPanel implements ISessionObserver {

    /**
     * Contrôleur de composition de message
     */
    private MessageComposeController composeController;

    /**
     * Session active
     */
    private ISession session;

    /**
     * Zone de texte pour la saisie du message
     */
    private JTextArea messageTextArea;

    /**
     * Bouton d'envoi du message
     */
    private JButton sendButton;

    /**
     * Label affichant le nombre de caractères restants
     */
    private JLabel characterCountLabel;

    /**
     * Limite de caractères pour un message
     */
    private int characterLimit;

    /**
     * Constructeur
     *
     * @param composeController Contrôleur de composition de message
     * @param session Session active
     */
    public MessageComposeView(MessageComposeController composeController, ISession session) {
        this.composeController = composeController;
        this.session = session;
        this.characterLimit = composeController.getMessageCharacterLimit();

        // S'abonner aux notifications de session
        this.session.addObserver(this);

        // Initialisation de l'interface
        this.initUI();

        // Mise à jour de l'état initial
        this.updateUIState(session.getConnectedUser() != null);
    }

    /**
     * Initialisation de l'interface utilisateur
     */
    private void initUI() {
        this.setLayout(new BorderLayout(0, 10));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Zone de saisie du message
        messageTextArea = new JTextArea();
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Ajout du DocumentFilter pour limiter la saisie
        ((AbstractDocument) messageTextArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                // Calculer la nouvelle longueur après insertion
                int newLength = fb.getDocument().getLength() + string.length();

                // N'autoriser l'insertion que si on ne dépasse pas la limite
                if (newLength <= characterLimit) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    // Si la chaîne à insérer peut être partiellement ajoutée sans dépasser la limite
                    int remainingSpace = characterLimit - fb.getDocument().getLength();
                    if (remainingSpace > 0) {
                        // Insérer seulement ce qui peut rentrer
                        super.insertString(fb, offset, string.substring(0, remainingSpace), attr);
                    }
                    // Signaler par un bip sonore que la limite est atteinte
                    Toolkit.getDefaultToolkit().beep();
                }
                updateCharacterCount();
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                // Calculer la nouvelle longueur après remplacement
                int newLength = fb.getDocument().getLength() + text.length() - length;

                // N'autoriser le remplacement que si on ne dépasse pas la limite
                if (newLength <= characterLimit) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    // Si le texte de remplacement peut être partiellement ajouté
                    int remainingSpace = characterLimit - (fb.getDocument().getLength() - length);
                    if (remainingSpace > 0) {
                        super.replace(fb, offset, length, text.substring(0, remainingSpace), attrs);
                    }
                    // Signaler par un bip sonore que la limite est atteinte
                    Toolkit.getDefaultToolkit().beep();
                }
                updateCharacterCount();
            }
        });

        // Scrollpane pour la zone de texte
        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 100));

        // Écouteur pour compter les caractères
        messageTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCharacterCount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCharacterCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCharacterCount();
            }
        });

        // Permettre l'envoi avec Ctrl+Enter
        messageTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                    e.consume();
                }
            }
        });

        // Panneau inférieur avec compteur et bouton
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));

        // Compteur de caractères
        characterCountLabel = new JLabel(characterLimit + " caractères restants");
        characterCountLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        bottomPanel.add(characterCountLabel, BorderLayout.WEST);

        // Bouton d'envoi
        sendButton = new JButton("Envoyer");
        sendButton.setBackground(new Color(240, 240, 240)); // Couleur de fond gris clair/blanc
        sendButton.setForeground(Color.BLACK); // Texte en noir
        sendButton.setFocusPainted(false);
        sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Ajout des composants
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Met à jour le compteur de caractères
     */
    private void updateCharacterCount() {
        int currentLength = messageTextArea.getText().length();
        int remaining = characterLimit - currentLength;

        // Affichage du nombre de caractères restants (jamais négatif)
        characterCountLabel.setText(Math.max(0, remaining) + " caractères restants");

        // Changer la couleur si on approche ou atteint la limite
        if (remaining < 0) {
            characterCountLabel.setForeground(Color.RED);

            // Si dépassement, tronquer le texte à la limite autorisée
            messageTextArea.setText(messageTextArea.getText().substring(0, characterLimit));

            // Mise à jour après troncature
            remaining = 0;
        } else if (remaining < 20) {
            characterCountLabel.setForeground(new Color(255, 140, 0)); // Orange
        } else {
            characterCountLabel.setForeground(Color.BLACK);
        }

        // Mettre à jour le compteur après troncature éventuelle
        characterCountLabel.setText(remaining + " caractères restants");
    }


    /**
     * Envoie le message
     */
    private void sendMessage() {
        String messageText = messageTextArea.getText().trim();
        String errorMessage = composeController.sendMessage(messageText);

        if (errorMessage != null) {
            // Afficher le message d'erreur
            JOptionPane.showMessageDialog(
                    this,
                    errorMessage,
                    "Erreur d'envoi",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            // Réinitialiser la zone de texte
            messageTextArea.setText("");
            updateCharacterCount();
        }
    }

    /**
     * Met à jour l'état de l'interface en fonction de l'état de connexion
     *
     * @param isConnected true si un utilisateur est connecté, false sinon
     */
    private void updateUIState(boolean isConnected) {
        messageTextArea.setEnabled(isConnected);
        sendButton.setEnabled(isConnected);

        if (!isConnected) {
            messageTextArea.setText("Veuillez vous connecter pour envoyer un message");
            messageTextArea.setForeground(Color.GRAY);
        } else {
            if (messageTextArea.getText().equals("Veuillez vous connecter pour envoyer un message")) {
                messageTextArea.setText("");
            }
            messageTextArea.setForeground(Color.BLACK);
        }
    }

    // Implémentation des méthodes de l'interface ISessionObserver

    @Override
    public void notifyLogin(User connectedUser) {
        updateUIState(true);
    }

    @Override
    public void notifyLogout() {
        updateUIState(false);
    }
}