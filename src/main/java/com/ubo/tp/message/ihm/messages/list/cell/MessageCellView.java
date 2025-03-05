package main.java.com.ubo.tp.message.ihm.messages.list.cell;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Composant représentant une cellule de message dans la liste
 */
public class MessageCellView extends JPanel {

    /**
     * Message à afficher
     */
    private Message message;

    /**
     * Format de date
     */
    private SimpleDateFormat dateFormat;

    /**
     * Session active
     */
    private ISession session;

    /**
     * Constructeur
     *
     * @param message Message à afficher
     * @param dateFormat Format de date pour l'affichage
     * @param session Session active pour déterminer l'utilisateur connecté
     */
    public MessageCellView(Message message, SimpleDateFormat dateFormat, ISession session) {
        this.message = message;
        this.dateFormat = dateFormat;
        this.session = session;

        this.initUI();
    }

    /**
     * Initialisation de l'interface utilisateur
     */
    private void initUI() {
        // Récupération des données du message
        User sender = message.getSender();
        String messageText = message.getText();
        Date messageDate = new Date(message.getEmissionDate());

        // Vérifier si le message est de l'utilisateur connecté
        User connectedUser = session.getConnectedUser();
        boolean isCurrentUserMessage = connectedUser != null &&
                message.getSender().equals(connectedUser);

        // Configuration du layout en fonction de l'émetteur du message
        this.setLayout(new BorderLayout(10, 5));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        if (isCurrentUserMessage) {
            // Message de l'utilisateur connecté
            this.setBorder(new CompoundBorder(
                    new EmptyBorder(5, 2, 5, 2),
                    new CompoundBorder(
                            new LineBorder(new Color(200, 230, 200), 1, true), // Couleur légèrement verte
                            new EmptyBorder(10, 10, 10, 10)
                    )
            ));
            this.setBackground(new Color(240, 255, 240)); // Vert très clair
        } else {
            // Message des autres utilisateurs
            this.setBorder(new CompoundBorder(
                    new EmptyBorder(5, 2, 5, 2),
                    new CompoundBorder(
                            new LineBorder(new Color(200, 230, 200), 1, true), // Couleur légèrement verte
                            new EmptyBorder(10, 10, 10, 10)
                    )
            ));
            this.setBackground(Color.WHITE);
        }

        // Panneau pour l'avatar
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setPreferredSize(new Dimension(50, 50));
        avatarPanel.setOpaque(false);

        JLabel avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);

        // Chargement de l'avatar s'il existe
        if (sender.getAvatarPath() != null && !sender.getAvatarPath().isEmpty()) {
            try {
                File avatarFile = new File(sender.getAvatarPath());
                if (avatarFile.exists()) {
                    ImageIcon avatarIcon = new ImageIcon(ImageIO.read(avatarFile));
                    Image img = avatarIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(img));
                } else {
                    // Avatar par défaut
                    avatarLabel.setText(sender.getName().substring(0, 1).toUpperCase());
                    avatarLabel.setFont(avatarLabel.getFont().deriveFont(Font.BOLD, 18));
                }
            } catch (IOException e) {
                // Avatar par défaut en cas d'erreur
                avatarLabel.setText(sender.getName().substring(0, 1).toUpperCase());
                avatarLabel.setFont(avatarLabel.getFont().deriveFont(Font.BOLD, 18));
            }
        } else {
            // Avatar par défaut
            avatarLabel.setText(sender.getName().substring(0, 1).toUpperCase());
            avatarLabel.setFont(avatarLabel.getFont().deriveFont(Font.BOLD, 18));
        }

        avatarPanel.add(avatarLabel, BorderLayout.CENTER);

        // Panneau central pour le contenu du message
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // En-tête avec nom, tag et date
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Nom et tag de l'expéditeur
        JPanel userPanel = new JPanel(new FlowLayout(isCurrentUserMessage ? FlowLayout.LEFT : FlowLayout.LEFT, 5, 0));
        userPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(sender.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));

        JLabel tagLabel = new JLabel("@" + sender.getUserTag());
        tagLabel.setForeground(new Color(100, 100, 100));

        userPanel.add(nameLabel);
        userPanel.add(tagLabel);

        headerPanel.add(userPanel, BorderLayout.WEST);

        // Date du message
        JLabel dateLabel = new JLabel(dateFormat.format(messageDate));
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.ITALIC, 11));
        dateLabel.setForeground(new Color(100, 100, 100));

        // Alignement de la date en fonction de l'émetteur
        JPanel datePanel = new JPanel(new FlowLayout(isCurrentUserMessage ? FlowLayout.RIGHT : FlowLayout.RIGHT, 5, 0));
        datePanel.setOpaque(false);
        datePanel.add(dateLabel);
        headerPanel.add(datePanel, BorderLayout.EAST);

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Contenu du message
        JTextArea textArea = new JTextArea(messageText);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textArea.setBackground(new Color(0, 0, 0, 0));
        textArea.setBorder(null);

        // Alignement du texte en fonction de l'émetteur
        if (isCurrentUserMessage) {
            // Pour les messages de l'utilisateur connecté, aligner le texte à droite
            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);
            textPanel.add(textArea, BorderLayout.CENTER);
            textArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            contentPanel.add(textPanel);
        } else {
            contentPanel.add(textArea);
        }

        // Positionnement de l'avatar et du contenu
        if (isCurrentUserMessage) {
            // Pour les messages de l'utilisateur connecté
            this.add(avatarPanel, BorderLayout.WEST);
            this.add(contentPanel, BorderLayout.CENTER);
        } else {
            // Pour les messages des autres utilisateurs
            this.add(avatarPanel, BorderLayout.WEST);
            this.add(contentPanel, BorderLayout.CENTER);
        }
    }
}