package main.java.com.ubo.tp.message.ihm;

import java.awt.*;
import java.io.File;
import java.util.Properties;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.directory.IWatchableDirectory;
import main.java.com.ubo.tp.message.core.directory.WatchableDirectory;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.message.IMessage;
import main.java.com.ubo.tp.message.datamodel.user.IUser;
import main.java.com.ubo.tp.message.datamodel.user.User;
import main.java.com.ubo.tp.message.datamodel.message.Message;

import main.java.com.ubo.tp.message.datamodel.message.MessageList;
import main.java.com.ubo.tp.message.datamodel.notification.INotification;
import main.java.com.ubo.tp.message.datamodel.notification.NotificationList;
import main.java.com.ubo.tp.message.ihm.login.LoginController;
import main.java.com.ubo.tp.message.ihm.login.LoginView;
import main.java.com.ubo.tp.message.ihm.menu.MenuController;
import main.java.com.ubo.tp.message.ihm.menu.MenuView;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeController;
import main.java.com.ubo.tp.message.ihm.messages.compose.MessageComposeView;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListController;
import main.java.com.ubo.tp.message.ihm.messages.list.MessageListView;
import main.java.com.ubo.tp.message.ihm.notifications.NotificationController;
import main.java.com.ubo.tp.message.ihm.notifications.NotificationView;
import main.java.com.ubo.tp.message.ihm.users.UserController;
import main.java.com.ubo.tp.message.ihm.users.UserListView;

import javax.swing.UIManager;


/**
 * Classe principale l'application.
 *
 * @author S.Lucas
 */
public class MessageApp implements ISessionObserver, Actions {
	/**
	 * Base de données.
	 */
	protected IDatabase mDatabase;

	protected IMessage mMessageList;

	protected IUser mUserList;

	/**
	 * Liste des notifications
	 */
	protected INotification mNotificationList;

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

	protected MessageComposeView messageComposeView;

	protected MessageListView messageListView;

	protected UserListView userListView;

	protected NotificationView notificationView;


	/**
	 * Constructeur.
	 *
	 */
	public MessageApp(IDatabase database, IMessage message, IUser user, EntityManager entityManager) {
		this.mDatabase = database;
		this.mEntityManager = entityManager;
		this.mSession = new Session();
		this.mSession.addObserver(this);
		this.mMessageList = new MessageList();
		this.mNotificationList = new NotificationList();
		this.mMessageList = message;
		this.mUserList = user;
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
		// Initialisation des contrôleurs qui nécessitent la vue principale
		this.mLoginController = new LoginController(this.mEntityManager, this.mSession, this.mUserList);

		this.mUserController = new UserController(this.mSession, this.mEntityManager, this.mUserList);

		this.mMessageComposeController = new MessageComposeController(this.mEntityManager, this.mSession, this.mMessageList);

		this.mNotificationController = new NotificationController(this.mDatabase, this.mSession, null);

		this.mMessageListController = new MessageListController(this.mSession, this.mMessageList, this.mUserList);
		// Création du contrôleur du menu
		this.mMenuController = new MenuController(this.mSession, this.mMessageList, mUserList, this);
	}

	protected void initView(){
		// Création de la vue principale
		this.mMainView = new MessageAppMainView();
		// Création de la vue de login
		this.mLoginView = new LoginView(this.mLoginController);

		this.messageListView = new MessageListView(this.mMessageListController, this.mSession, this.mMessageList);

		this.messageComposeView = new MessageComposeView(this.mMessageComposeController, this.mSession);

		this.userListView = new UserListView(this.mUserController, this.mSession, this.mUserList);

		this.notificationView = new NotificationView(this.mNotificationController);

		// Création de la vue de contenu principal
		this.mMainContentView = new MainContentView(this.mSession, this.mNotificationController, this.messageListView, this.messageComposeView, this.userListView, this.notificationView);

		this.mMenuView = new MenuView(this.mMenuController, this.mSession);

		this.messageComposeView = new MessageComposeView(this.mMessageComposeController, this.mSession);
		this.messageListView = new MessageListView(this.mMessageListController, this.mSession, this.mMessageList);
		this.userListView = new UserListView(this.mUserController, this.mSession, this.mUserList);
		this.notificationView = new NotificationView(this.mNotificationController);
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



	@Override
	public void logout(){
		if (this.mSession.getConnectedUser() != null) {
			this.mSession.disconnect();
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
	 * @param directoryPath chemain absolue pour le dossier
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

	@Override
	public void exitApplication() {
		// Fermer la vue
		if (mMainView != null) {
			if(mMainView.closeApp() == 0){
				this.exit();
			}
		}
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


	@Override
	public void notifyLogin(User connectedUser) {
		this.mMessageList.refreshMessage();
		this.mUserList.refreshUser();
		this.mMainContentView.updateUIState();
		this.mMainView.login(connectedUser);
		this.showMainContent();
	}

	@Override
	public void notifyLogout() {
		Container contentPane = this.mMainView.getContentPane();
		contentPane.removeAll();
		contentPane.add(new LoginView(this.mLoginController), BorderLayout.CENTER);
		contentPane.revalidate();
		contentPane.repaint();
		this.mMainView.logout();
		this.mMainContentView.updateUIState();
	}


}