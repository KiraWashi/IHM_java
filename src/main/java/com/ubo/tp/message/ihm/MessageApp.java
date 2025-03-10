package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import java.io.File;
import java.util.Properties;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.directory.IWatchableDirectory;
import main.java.com.ubo.tp.message.core.directory.WatchableDirectory;
import main.java.com.ubo.tp.message.core.notification.NotificationController;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.login.LoginController;
import main.java.com.ubo.tp.message.ihm.login.LoginView;
import main.java.com.ubo.tp.message.ihm.menu.MenuController;
import main.java.com.ubo.tp.message.ihm.menu.about.AboutController;
import main.java.com.ubo.tp.message.ihm.menu.directoryChoose.DirectoryController;
import main.java.com.ubo.tp.message.ihm.menu.profile.ProfileController;
import javax.swing.UIManager;


/**
 * Classe principale l'application.
 *
 * @author S.Lucas
 */
public class MessageApp implements ISessionObserver {
	/**
	 * Base de données.
	 */
	protected IDatabase mDatabase;

	/**
	 * Gestionnaire des entités contenu de la base de données.
	 */
	protected EntityManager mEntityManager;

	/**
	 * Vue principale de l'application.
	 */
	protected MessageAppMainView mMainView;

	/**
	 * Session de l'application.
	 */
	protected ISession mSession;

	/**
	 * Contrôleur de profil.
	 */
	protected ProfileController mProfileController;

	/**
	 * Contrôleur de répertoire.
	 */
	protected DirectoryController mDirectoryController;

	/**
	 * Contrôleur "À propos".
	 */
	protected AboutController mAboutController;

	/**
	 * Contrôleur du menu.
	 */
	protected MenuController mMenuController;

	/**
	 * Contrôleur de login.
	 */
	protected LoginController mLoginController;

	/**
	 * Vue de login.
	 */
	protected LoginView mLoginView;

	/**
	 * Vue du contenu principal après connexion.
	 */
	protected MainContentView mMainContentView;

	/**
	 * Classe de surveillance de répertoire
	 */
	protected IWatchableDirectory mWatchableDirectory;

	/**
	 * Répertoire d'échange de l'application.
	 */
	protected String mExchangeDirectoryPath;

	/**
	 * Nom de la classe de l'UI.
	 */
	protected String mUiClassName;


	/**
	 * Controller pour les notifications
	 */
	protected NotificationController mNotificationController;

	/**
	 * Constructeur.
	 *
	 * @param entityManager
	 * @param database
	 */
	public MessageApp(IDatabase database, EntityManager entityManager) {
		this.mDatabase = database;
		this.mEntityManager = entityManager;

		// Création de la session
		this.mSession = new Session();
		mSession.addObserver(this);

	}

	/**
	 * Initialisation de l'application.
	 */
	public void init() {
		// Init du look and feel de l'application
		this.initLookAndFeel();

		// Initialisation des contrôleurs
		this.initControllers();

		// Initialisation de l'IHM
		this.initGui();

		// Initialisation du répertoire d'échange
		this.initDirectory();
	}

	/**
	 * Initialisation des contrôleurs.
	 */
	protected void initControllers() {
		// Création des contrôleurs pour les différentes parties de l'application
		this.mDirectoryController = new DirectoryController(this);
		this.mAboutController = new AboutController();
		this.mProfileController = new ProfileController(this.mDatabase);
		this.mNotificationController = new NotificationController(this.mDatabase, this.mSession, null);
	}

	/**
	 * Initialisation du look and feel de l'application.
	 */
	protected void initLookAndFeel() {
		try {
			// Utiliser le look and feel du système
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Erreur lors de l'initialisation du look and feel : " + e.getMessage());
		}
	}

	/**
	 * Initialisation de l'interface graphique.
	 */
	protected void initGui() {
		// Création de la vue principale
		this.mMainView = new MessageAppMainView(this);

		// Initialisation des contrôleurs qui nécessitent la vue principale
		this.mLoginController = new LoginController(this.mDatabase, this.mEntityManager, this.mSession);
		this.mMenuController = new MenuController(
				this.mMainView,
				this.mSession,
				this.mProfileController,
				this.mDirectoryController,
				this.mAboutController
		);

		// Création de la vue de login
		this.mLoginView = new LoginView(this.mLoginController);

		// Création de la vue de contenu principal
		this.mMainContentView = new MainContentView(this.mDatabase, this.mSession, this.mEntityManager, this);
		this.mDatabase.addObserver(this.mNotificationController);

		// Configuration du menu de l'application
		this.mMainView.setJMenuBar(this.mMenuController.getMenuView());

		// Ajout de la vue de login au contentPane
		Container contentPane = this.mMainView.getContentPane();
		contentPane.add(this.mLoginView, BorderLayout.CENTER);

		// Initialisation du contrôleur de menu
		this.mMenuController.init();

		// Initialisation de la vue principale
		this.mMainView.init();
	}

	/**
	 * Initialisation du répertoire d'échange (depuis la conf ou depuis un file
	 * chooser). <br/>
	 * <b>Le chemin doit obligatoirement avoir été saisi et être valide avant de
	 * pouvoir utiliser l'application</b>
	 */
	protected void initDirectory() {
		// Essayer de charger le répertoire depuis le fichier de configuration
		String configFilePath = Constants.CONFIGURATION_FILE;
		File configFile = new File(configFilePath);

		// Vérifier si le fichier de configuration existe
		if (configFile.exists()) {
			Properties config = PropertiesManager.loadProperties(configFilePath);
			String savedPath = config.getProperty(Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY);

			// Vérifier si le chemin est valide
			if (savedPath != null && isValideExchangeDirectory(new File(savedPath))) {
				initDirectory(savedPath);
				return;
			}
		}

		// Si pas de configuration valide, demander à l'utilisateur
		File file = this.mMainView.showDirectoryChooser();

		// Initialiser avec le répertoire sélectionné
		if (file != null && isValideExchangeDirectory(file)) {
			initDirectory(file.getAbsolutePath());

			// Sauvegarder le chemin dans la configuration
			Properties config = configFile.exists() ?
					PropertiesManager.loadProperties(configFilePath) :
					new Properties();
			config.setProperty(Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY, file.getAbsolutePath());
			PropertiesManager.writeProperties(config, configFilePath);
		}
	}

	/**
	 * Ferme proprement l'application
	 */
	public void close() {
		// Arrêter la surveillance du répertoire si active
		if (mWatchableDirectory != null) {
			mWatchableDirectory.stopWatching();
		}

		// Fermer la vue
		if (mMainView != null) {
			mMainView.dispose();
		}

		// Quitter l'application
		System.exit(0);
	}

	/**
	 * Indique si le fichier donné est valide pour servir de répertoire d'échange
	 *
	 * @param directory , Répertoire à tester.
	 */
	protected boolean isValideExchangeDirectory(File directory) {
		// Valide si répertoire disponible en lecture et écriture
		return directory != null && directory.exists() && directory.isDirectory() && directory.canRead()
				&& directory.canWrite();
	}

	/**
	 * Initialisation du répertoire d'échange.
	 *
	 * @param directoryPath
	 */
	protected void initDirectory(String directoryPath) {
		mExchangeDirectoryPath = directoryPath;
		mWatchableDirectory = new WatchableDirectory(directoryPath);
		mEntityManager.setExchangeDirectory(directoryPath);

		if (mNotificationController != null) {
			mNotificationController.setExchangeDirectory(directoryPath);
		}

		mWatchableDirectory.initWatching();
		mWatchableDirectory.addObserver(mEntityManager);
	}

	/**
	 * Change le répertoire d'échange
	 *
	 * @param directoryPath Nouveau chemin du répertoire
	 */
	public void changeDirectory(String directoryPath) {
		File file = new File(directoryPath);
		if (isValideExchangeDirectory(file)) {
			// Arrêter la surveillance actuelle
			if (mWatchableDirectory != null) {
				mWatchableDirectory.stopWatching();
			}

			// Initialiser avec le nouveau répertoire
			initDirectory(directoryPath);

			// Sauvegarder dans la configuration
			String configFilePath = Constants.CONFIGURATION_FILE;
			Properties config = new File(configFilePath).exists() ?
					PropertiesManager.loadProperties(configFilePath) :
					new Properties();
			config.setProperty(Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY, directoryPath);
			PropertiesManager.writeProperties(config, configFilePath);
		}
	}

	/**
	 * Affiche l'application
	 */
	public void show() {
		if (this.mMainView != null) {
			this.mMainView.setVisible(true);
		}
	}

	/**
	 * Affiche la vue principale
	 */
	private void showMainContent() {
		// Récupère le contentPane
		Container contentPane = this.mMainView.getContentPane();

		// Vide le contentPane
		contentPane.removeAll();

		// Ajoute la vue principale
		contentPane.add(mMainContentView, BorderLayout.CENTER);

		// Rafraîchit la vue
		contentPane.revalidate();
		contentPane.repaint();
	}

	/**
	 * Retourne la base de données
	 */
	public IDatabase getDatabase() {
		return mDatabase;
	}

	/**
	 * Retourne le gestionnaire d'entités
	 */
	public EntityManager getEntityManager() {
		return mEntityManager;
	}

	/**
	 * Retourne la session de l'application
	 */
	public ISession getSession() {
		return mSession;
	}

	/**
	 * Retourne le controller de notifications
	 */
	public NotificationController getNotificationController() {
		return this.mNotificationController;
	}

	@Override
	public void notifyLogin(User connectedUser) {
		this.showMainContent();
	}

	@Override
	public void notifyLogout() {
		Container contentPane = this.mMainView.getContentPane();
		contentPane.removeAll();
		contentPane.add(new LoginView(this.mLoginController), BorderLayout.CENTER);
		contentPane.revalidate();
		contentPane.repaint();
	}
}