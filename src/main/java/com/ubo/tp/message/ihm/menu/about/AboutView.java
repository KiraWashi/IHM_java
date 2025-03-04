package main.java.com.ubo.tp.message.ihm.menu.about;



import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Classe qui gère l'affichage de la boîte de dialogue "À propos"
 */
public class AboutView {

    /**
     * Chemin vers les icônes
     */
    private static final String ICON_PATH = "MessageApp/src/main/resources/images/";

    /**
     * Référence vers la fenêtre principale
     */
    private JFrame parentFrame;

    /**
     * Contrôleur pour la gestion des informations "À propos"
     */
    private AboutController aboutController;

    /**
     * Logo de l'application
     */
    private ImageIcon appLogo;

    /**
     * Constructeur
     *
     * @param parentFrame Fenêtre parente pour les dialogues
     * @param aboutController Contrôleur pour la gestion des informations "À propos"
     */
    public AboutView(JFrame parentFrame, AboutController aboutController) {
        this.parentFrame = parentFrame;
        this.aboutController = aboutController;

        // Chargement du logo
        try {
            this.appLogo = new ImageIcon(ImageIO.read(new File(ICON_PATH + "logo_20.png")));
        } catch (IOException e) {
            System.err.println("Impossible de charger le logo: " + e.getMessage());
            this.appLogo = new ImageIcon();
        }
    }

    /**
     * Affiche la boîte de dialogue "À propos"
     */
    public void showAboutDialog() {
        // Création du panneau personnalisé pour JOptionPane
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel du haut avec titre
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("UBO M2-TIIL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel);

        // Panel du centre avec informations supplémentaires
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel deptLabel = new JLabel("Département Informatique");
        deptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appNameLabel = new JLabel(aboutController.getAppName());
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Version " + aboutController.getAppVersion());
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(deptLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(appNameLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(versionLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Organisation du panneau
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Création de la boîte de dialogue avec JOptionPane
        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                appLogo,
                new Object[]{"OK"},
                "OK"
        );

        // Création d'un JDialog à partir du JOptionPane
        JDialog dialog = optionPane.createDialog(parentFrame, "À propos");

        // Affichage de la boîte de dialogue
        dialog.setVisible(true);
    }
}