package main.java.com.ubo.tp.message.ihm.menu;

import main.java.com.ubo.tp.message.ihm.MessageApp;

/**
 * Classe qui gère l'affichage du menu de l'application MessageApp.
 * Responsable uniquement de l'interface utilisateur du menu.
 */
public class MenuDelegate implements MenuController.MenuActionDelegate {

    /**
     * Référence vers l'application
     */
    private final MessageApp messageApp;

    /**
     * Constructeur
     *
     * @param messageApp L'application MessageApp
     */
    public MenuDelegate(MessageApp messageApp) {
        this.messageApp = messageApp;
    }

    @Override
    public void exitApplication() {
        messageApp.close();
    }

    @Override
    public void changeDirectory(String directoryPath) {
        messageApp.changeDirectory(directoryPath);
    }

    @Override
    public void logout() {
        messageApp.logout();
    }

}