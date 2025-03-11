package main.java.com.ubo.tp.message;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.Database;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.datamodel.message.IMessage;
import main.java.com.ubo.tp.message.datamodel.message.MessageList;
import main.java.com.ubo.tp.message.datamodel.user.IUser;
import main.java.com.ubo.tp.message.datamodel.user.UserList;
import main.java.com.ubo.tp.message.ihm.MessageApp;
import main.mock.MessageAppMock;

/**
 * Classe de lancement de l'application.
 *
 * @author S.Lucas
 */
public class MessageAppLauncher {

	/**
	 * Indique si le mode bouchoné est activé.
	 */
	protected static boolean IS_MOCK_ENABLED = false;

	/**
	 * Launcher.
	 *
	 * @param args arg
	 */
	public static void main(String[] args) {

		IDatabase database = new Database();
		IUser user = new UserList();
		IMessage message = new MessageList();

		EntityManager entityManager = new EntityManager(message, user);

		if (IS_MOCK_ENABLED) {
			MessageAppMock mock = new MessageAppMock(database, entityManager);
			mock.showGUI();
		}

		MessageApp messageApp = new MessageApp(database, message, user, entityManager);
		messageApp.init();
		messageApp.show();

	}
}
