package main.java.com.ubo.tp.message.core.notification;

import java.io.File;
import java.util.*;
import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.datamodel.User;

public class NotificationManager {

    private static final String NOTIFICATIONS_CONFIG_EXTENSION = "notif";

    // Répertoire d'échange
    private String exchangeDirectory;

    public NotificationManager(String exchangeDirectory) {
        this.exchangeDirectory = exchangeDirectory;
    }

    // Persister les notifications lues
    public void saveReadNotifications(User user, Set<UUID> readMessageIds) {
        if (user == null || exchangeDirectory == null) return;

        String filename = getNotificationsFilename(user);
        Properties props = new Properties();

        // Convertir les UUIDs en chaînes de caractères
        int count = 0;
        for (UUID messageId : readMessageIds) {
            props.setProperty("message." + count, messageId.toString());
            count++;
        }

        // Sauvegarder dans le fichier
        PropertiesManager.writeProperties(props, filename);
    }

    // Charger les notifications lues
    public Set<UUID> loadReadNotifications(User user) {
        if (user == null || exchangeDirectory == null) return new HashSet<>();

        String filename = getNotificationsFilename(user);
        File file = new File(filename);

        if (!file.exists()) {
            return new HashSet<>();
        }

        Properties props = PropertiesManager.loadProperties(filename);
        Set<UUID> result = new HashSet<>();

        // Parcourir toutes les propriétés
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("message.")) {
                try {
                    UUID messageId = UUID.fromString(props.getProperty(key));
                    result.add(messageId);
                } catch (IllegalArgumentException e) {
                    // Ignorer les UUIDs invalides
                }
            }
        }

        return result;
    }

    // Construire le nom du fichier
    private String getNotificationsFilename(User user) {
        return exchangeDirectory + Constants.SYSTEM_FILE_SEPARATOR +
                user.getUuid().toString() + "." + NOTIFICATIONS_CONFIG_EXTENSION;
    }
}