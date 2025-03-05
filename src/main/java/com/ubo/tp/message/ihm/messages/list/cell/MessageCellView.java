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
     * Constructeur
     *
     * @param message Message à afficher
     * @param dateFormat Format de date pour l'affichage
     */
    public MessageCellView(Message message, SimpleDateFormat dateFormat) {
        this.message = message;
        this.dateFormat = dateFormat;

        this.initUI();
    }

    /**
     * Initialisation de l'interface utilisateur
     */
    private void initUI() {
        this.setLayout(new BorderLayout(10, 5));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        this.setBorder(new CompoundBorder(
                new EmptyBorder(5, 2, 5, 2),
                new CompoundBorder(
                        new LineBorder(new Color(220, 220, 220), 1, true),
                        new EmptyBorder(10, 10, 10, 10)
                )
        ));

        // Récupération des données du message
        User sender = message.getSender();
        String messageText = message.getText();
        Date messageDate = new Date(message.getEmissionDate());

        // Panneau gauche pour l'avatar
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
        this.add(avatarPanel, BorderLayout.WEST);

        // Panneau central pour le contenu du message
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // En-tête avec nom, tag et date
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Nom et tag de l'expéditeur
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
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
        headerPanel.add(dateLabel, BorderLayout.EAST);

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

        // Coloration des tags et mentions
        highlightTags(textArea);

        contentPanel.add(textArea);

        this.add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Colore les tags (#) et les mentions (@) dans le texte
     *
     * @param textArea Zone de texte à colorier
     */
    private void highlightTags(JTextArea textArea) {
        // Cette méthode pourrait être implémentée avec un StyledDocument
        // pour mettre en évidence les tags et les mentions d'utilisateurs
        // Mais par simplicité nous utiliserons simplement un JTextArea standard ici
    }
}