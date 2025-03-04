package main.java.com.ubo.tp.message.ihm.menu.directoryChoose;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.ihm.MessageApp;

import javax.swing.*;
import java.io.File;
import java.util.Properties;

/**
 * Classe qui gère la sélection du répertoire d'échange
 */
public class DirectoryChooserView {

    /**
     * Référence vers la fenêtre principale
     */
    private JFrame parentFrame;

    /**
     * Contrôleur pour la gestion du répertoire
     */
    private DirectoryController directoryController;

    /**
     * Constructeur
     *
     * @param parentFrame Fenêtre parente pour les dialogues
     * @param directoryController Contrôleur pour la gestion du répertoire
     */
    public DirectoryChooserView(JFrame parentFrame, DirectoryController directoryController) {
        this.parentFrame = parentFrame;
        this.directoryController = directoryController;
    }

    /**
     * Affiche un sélecteur de répertoire et met à jour le répertoire d'échange
     *
     * @return Le fichier sélectionné ou null si aucun répertoire n'a été choisi
     */
    public File showDirectoryChooser() {
        // Création du sélecteur de fichier
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionnez un répertoire d'échange");

        // Affichage du sélecteur de fichier
        int returnValue = fileChooser.showOpenDialog(parentFrame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String directoryPath = selectedFile.getAbsolutePath();

            // Déléguer la vérification et le changement de répertoire au contrôleur
            if (directoryController.isValidExchangeDirectory(selectedFile)) {
                if (directoryController.changeDirectory(directoryPath)) {
                    // Affichage d'un message de confirmation
                    JOptionPane.showMessageDialog(
                            parentFrame,
                            "Le répertoire d'échange a été changé pour :\n" + directoryPath,
                            "Répertoire d'échange",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    return selectedFile;
                }
            } else {
                // Affichage d'un message d'erreur
                JOptionPane.showMessageDialog(
                        parentFrame,
                        "Le répertoire sélectionné n'est pas valide.\n" +
                                "Veuillez sélectionner un répertoire accessible en lecture et écriture.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        return null;
    }
}