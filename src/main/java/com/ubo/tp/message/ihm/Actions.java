package main.java.com.ubo.tp.message.ihm;

public interface Actions {
    /**
     * Quitte l'application
     */
    void exitApplication();

    /**
     * Change le répertoire d'échange
     */
    void changeDirectory(String directoryPath);

    /**
     * Déconnecte l'utilisateur
     */
    void logout();
}
