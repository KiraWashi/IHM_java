package main.java.com.ubo.tp.message.ihm.menu.directoryChoose;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.ihm.MessageApp;

import java.io.File;
import java.util.Properties;

/**
 * Contrôleur pour la gestion du répertoire d'échange
 */
public class DirectoryController {

    /**
     * Référence vers l'application
     */
    private MessageApp messageApp;

    /**
     * Constructeur
     *
     * @param messageApp L'application MessageApp
     */
    public DirectoryController(MessageApp messageApp) {
        this.messageApp = messageApp;
    }

    /**
     * Vérifie si le répertoire est valide pour servir de répertoire d'échange
     *
     * @param directory Répertoire à tester
     * @return true si le répertoire est valide, false sinon
     */
    public boolean isValidExchangeDirectory(File directory) {
        return directory != null && directory.exists() && directory.isDirectory() &&
                directory.canRead() && directory.canWrite();
    }

    /**
     * Change le répertoire d'échange de l'application
     *
     * @param directoryPath Chemin du répertoire
     * @return true si le changement a réussi, false sinon
     */
    public boolean changeDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (isValidExchangeDirectory(directory)) {
            // Mise à jour du répertoire d'échange
            messageApp.changeDirectory(directoryPath);

            // Sauvegarde dans la configuration
            saveDirectoryConfiguration(directoryPath);

            return true;
        }

        return false;
    }

    /**
     * Sauvegarde le répertoire d'échange dans la configuration
     *
     * @param directoryPath Chemin du répertoire à sauvegarder
     */
    private void saveDirectoryConfiguration(String directoryPath) {
        String configFilePath = Constants.CONFIGURATION_FILE;
        Properties config = new File(configFilePath).exists() ?
                PropertiesManager.loadProperties(configFilePath) :
                new Properties();

        config.setProperty(Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY, directoryPath);
        PropertiesManager.writeProperties(config, configFilePath);
    }
}