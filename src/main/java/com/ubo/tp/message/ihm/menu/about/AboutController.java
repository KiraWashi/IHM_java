package main.java.com.ubo.tp.message.ihm.menu.about;

/**
 * Contrôleur pour la gestion de la fonctionnalité "À propos"
 */
public class AboutController {

    /**
     * Version de l'application
     */
    private static final String APP_VERSION = "1.0";

    /**
     * Nom de l'application
     */
    private static final String APP_NAME = "MessageApp";

    /**
     * Constructeur
     */
    public AboutController() {
        // Constructeur vide
    }

    /**
     * Récupère la version de l'application
     *
     * @return Version de l'application
     */
    public String getAppVersion() {
        return APP_VERSION;
    }

    /**
     * Récupère le nom de l'application
     *
     * @return Nom de l'application
     */
    public String getAppName() {
        return APP_NAME;
    }

    /**
     * Récupère les informations complètes sur l'application
     *
     * @return Informations sur l'application
     */
    public String getAppInfo() {
        StringBuilder info = new StringBuilder();
        info.append("UBO M2-TIIL\n");
        info.append("Département Informatique\n");
        info.append("Application: ").append(APP_NAME).append("\n");
        info.append("Version: ").append(APP_VERSION);

        return info.toString();
    }
}