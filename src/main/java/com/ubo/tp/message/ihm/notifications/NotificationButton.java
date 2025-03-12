package main.java.com.ubo.tp.message.ihm.notifications;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.notification.INotification;
import main.java.com.ubo.tp.message.datamodel.notification.INotificationListObserver;
import main.java.com.ubo.tp.message.datamodel.notification.Notification;
import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Bouton de notification avec compteur
 */
public class NotificationButton extends JButton implements INotificationListObserver {

    private int unreadCount;
    private final NotificationController controller;

    /**
     * Constructeur
     */
    public NotificationButton(NotificationController notificationController, INotification notificationList) {

        this.unreadCount = notificationController.getUnreadCount();
        this.controller = notificationController;
        notificationList.addObserver(this);

        // S'abonner aux changements de notifications
        notificationController.getNotificationList().addObserver(this);

        // Initialisation de l'interface
        this.initUI();
        // Dans le constructeur de NotificationButton, ajouter:
        addActionListener(e -> {
            // Sélectionner l'onglet des notifications (index 2)
            Component parent = getParent();
            while (parent != null && !(parent instanceof JTabbedPane)) {
                parent = parent.getParent();
            }

            if (parent != null) {
                JTabbedPane tabbedPane = (JTabbedPane) parent;
                tabbedPane.setSelectedIndex(2);
            }
        });
    }

    /**
     * Initialisation de l'interface
     */
    private void initUI() {
        setText("Notifications");
        setFocusPainted(false);

        // Personnalisation pour afficher le compteur
        setIcon(createIcon());
    }

    /**
     * Crée l'icône avec le compteur
     */
    private Icon createIcon() {
        if (unreadCount == 0) {
            return null;
        }

        // Création d'une icône personnalisée avec un badge rouge
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Badge rouge
                g2d.setColor(Color.RED);
                Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, 16, 16);
                g2d.fill(circle);

                // Nombre
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                String countText = unreadCount > 9 ? "9+" : String.valueOf(unreadCount);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(countText);
                int textHeight = fm.getHeight();
                g2d.drawString(countText, 8 - textWidth / 2, 8 + textHeight / 4);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return 16;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }

    @Override
    public void notifyNotificationAdded(Notification addedNotification) {
        this.controller.refreshCount();
        this.unreadCount= this.controller.getUnreadCount();
        System.out.println(unreadCount);
        setIcon(createIcon());
        repaint();
    }

    @Override
    public void notifyRefreshNotif() {
        this.controller.refreshCount();
        this.unreadCount= this.controller.getUnreadCount();
        System.out.println(unreadCount);
        setIcon(createIcon());
        repaint();
    }

    @Override
    public void notifyNotificationRemoved(Notification removedNotification) {
        this.unreadCount=0;
        setIcon(createIcon());
        repaint();
    }

    @Override
    public void notifyNotificationsRead() {
        this.unreadCount = 0; // Toutes les notifications ont été lues
        // Mettre à jour l'icône (qui disparaîtra car unreadCount = 0)
        setIcon(createIcon());
        repaint();
    }

}