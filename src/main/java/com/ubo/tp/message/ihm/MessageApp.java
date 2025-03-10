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
import main.java.com.ubo.tp.message.ihm.menu.MenuDelegate;
import main.java.com.ubo.tp.message.ihm.menu.MenuView;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeController;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListController;
import main.java.com.ubo.tp.message.ihm.users.UserController;

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
	 * Contrôleur du menu.
	 */
	protected MenuController mMenuController;

	/**
	 * Délégué pour les actions du menu
	 */
	protected MenuDelegate mMenuDelegate;

	/**
	 * Contrôleur de login.
	 */
	protected LoginController mLoginController;

	/**
	 * Contrôleur de MessageCompose.
	 */
	protected MessageComposeController mMessageComposeController;

	/**
	 * Contrôleur de MessageCompose.
	 */
	protected UserController mUserController;

	/**
	 * Contrôleur de MessageCompose.
	 */
	protected MessageListController mMessageListController;

	/**
	 * Vue de login.
	 */
	protected LoginView mLoginView;

	/**
	 * Vue du contenu principal après connexion.
	 */
	protected MainContentView mMainContentView;

	/**
	 * Vue du contenu principal après connexion.
	 */
	protected MenuView mMenuView;

	/**
	 * Classe de surveillance de répertoire
	 */
	protected IWatchableDirectory mWatchableDirectory;

	/**
	 * Répertoire d'échange de l'application.
	 */
	protected String mExchangeDirectoryPath;

	/**
	 * Controller pour les notifications
	 */
	protected NotificationController mNotificationController;

	/**
	 * Constructeur.
	 *
	 */
	public MessageApp(IDatabase database, EntityManager entityManager) {
		this.mDatabase = database;
		this.mEntityManager = entityManager;
		this.mSession = new Session();
		mSession.addObserver(this);
	}

	/**
	 * Initialisation de l'application.
	 */
	public void init() {
		// Init du look and feel de l'application
		this.initLookAndFeel();

		// Initialisation du contrôleur de notification
		this.initController();

		this.initView();

		// Initialisation de l'IHM
		this.initGui();

		// Initialisation du répertoire d'échange
		this.initDirectory();
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

	protected void initController(){
		// Création du délégué pour les actions du menu
		this.mMenuDelegate = new MenuDelegate(this);
		// Initialisation des contrôleurs qui nécessitent la vue principale
		this.mLoginController = new LoginController(this.mDatabase, this.mEntityManager, this.mSession);

		this.mUserController = new UserController(this.mDatabase, this.mSession, this.mEntityManager);

		this.mMessageComposeController = new MessageComposeController(this.mDatabase, this.mEntityManager, this.mSession);

		this.mNotificationController = new NotificationController(this.mDatabase, this.mSession, null);

		this.mMessageListController = new MessageListController(this.mDatabase, this.mSession);
		// Création du contrôleur du menu
		this.mMenuController = new MenuController(this.mSession, this.mDatabase, this.mMenuDelegate);
	}

	protected void initView(){
		// Création de la vue principale
		this.mMainView = new MessageAppMainView(mSession);
		// Création de la vue de login
		this.mLoginView = new LoginView(this.mLoginController);
		// Création de la vue de contenu principal
		this.mMainContentView = new MainContentView(this.mSession, this.mNotificationController, this.mMessageComposeController, this.mMessageListController, this.mUserController);

		this.mMenuView = new MenuView(this.mMenuController, this.mSession);
	}

	/**
	 * Initialisation de l'interface graphique.
	 */
	protected void initGui() {

		// Définir la fenêtre parente pour les dialogues
		mMenuView.setParentComponent(this.mMainView);
		this.mMainView.setJMenuBar(mMenuView);

		this.mDatabase.addObserver(this.mNotificationController);

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
			if (savedPath != null && mMenuController.isValidExchangeDirectory(new File(savedPath))) {
				initDirectory(savedPath);
				return;
			}
		}

		// Si pas de configuration valide, demander à l'utilisateur
		File file = this.mMainView.showDirectoryChooser();

		// Initialiser avec le répertoire sélectionné
		if (file != null && mMenuController.isValidExchangeDirectory(file)) {
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
		// Fermer la vue
		if (mMainView != null) {
			if(mMainView.closeApp() == 0){
				this.exit();
			}
		}
	}

	public void logout(){
		if (this.getSession().getConnectedUser() != null) {
			this.getSession().disconnect();
		}
	}

	/**
	 * Exit
	 */
	public void exit() {
		// Arrêter la surveillance du répertoire si active
		if (mWatchableDirectory != null) {
			mWatchableDirectory.stopWatching();
		}
		// Quitter l'application
		System.exit(0);
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
		if (mMenuController.isValidExchangeDirectory(file)) {
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
	 * Retourne la session de l'application
	 */
	public ISession getSession() {
		return mSession;
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