package main.java.com.ubo.tp.message.ihm.notifications;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.core.notification.INotificationObserver;
import main.java.com.ubo.tp.message.core.notification.NotificationController;
import main.java.com.ubo.tp.message.datamodel.Notification;

/**
 * Vue pour afficher les notifications
 */
public class NotificationView extends JPanel implements INotificationObserver {

    private NotificationController notificationController;
    private JPanel notificationsPanel;
    private JScrollPane scrollPane;
    private SimpleDateFormat dateFormat;

    /**
     * Constructeur
     */
    public NotificationView(NotificationController notificationController) {
        this.notificationController = notificationController;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // S'abonner aux changements de notifications
        this.notificationController.addObserver(this);

        // Initialisation de l'interface
        this.initUI();

        // Affichage initial des notifications
        this.refreshNotifications();
    }

    /**
     * Initialisation de l'interface
     */
    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Bouton pour marquer toutes les notifications comme lues
        JButton markAllReadButton = new JButton("Marquer tout comme lu");
        markAllReadButton.addActionListener(e -> {
            notificationController.markAllAsRead();
            refreshNotifications();
        });
        headerPanel.add(markAllReadButton, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        this.add(headerPanel, BorderLayout.NORTH);

        // Panel des notifications
        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        // ScrollPane
        scrollPane = new JScrollPane(notificationsPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Rafraîchit la liste des notifications
     */
    public void refreshNotifications() {
        notificationsPanel.removeAll();

        List<Notification> notifications = notificationController.getAllNotifications();

        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucune notification");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setFont(emptyLabel.getFont().deriveFont(Font.ITALIC));

            notificationsPanel.add(Box.createVerticalGlue());
            notificationsPanel.add(emptyLabel);
            notificationsPanel.add(Box.createVerticalGlue());
        } else {
            for (Notification notification : notifications) {
                // Création d'un panel pour chaque notification
                JPanel notificationPanel = createNotificationPanel(notification);
                notificationsPanel.add(notificationPanel);

                // Séparateur
                notificationsPanel.add(Box.createRigidArea(new Dimension(0, 1)));
                JSeparator separator = new JSeparator();
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                notificationsPanel.add(separator);
                notificationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        notificationsPanel.revalidate();
        notificationsPanel.repaint();
    }

    /**
     * Crée un panel pour une notification
     */
    private JPanel createNotificationPanel(Notification notification) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 2, 5, 2),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                notification.isRead() ? new Color(220, 220, 220) : new Color(173, 216, 230),
                                1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        ));

        // État de lecture (point bleu si non lu)
        if (!notification.isRead()) {
            JPanel readIndicator = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(30, 144, 255));
                    g.fillOval(0, 5, 10, 10);
                }
            };
            readIndicator.setPreferredSize(new Dimension(15, 20));
            panel.add(readIndicator, BorderLayout.WEST);
        }

        // Contenu principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        // En-tête (expéditeur et date)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel senderLabel = new JLabel("@" + notification.getSender().getUserTag() + " a publié un message");
        senderLabel.setFont(senderLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(senderLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(dateFormat.format(notification.getCreationDate()));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.ITALIC, 10));
        headerPanel.add(dateLabel, BorderLayout.EAST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Message
        JTextArea messageArea = new JTextArea(notification.getMessage().getText());
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageArea.setBackground(new Color(0, 0, 0, 0));
        messageArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        contentPanel.add(messageArea, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void notifyNotificationChanged() {
        SwingUtilities.invokeLater(() -> refreshNotifications());
    }
}