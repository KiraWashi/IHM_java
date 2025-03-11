package main.java.com.ubo.tp.message.common;

import java.io.File;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Classe de gestion des conversion des objets entre le datamodel et les
 * fichiers de propriété.
 *
 * @author S.Lucas
 */
public class DataFilesManager {

	public static final User UNKNOWN_USER = new User(Constants.UNKNONWN_USER_UUID, "<Inconnu>", "--", "<Inconnu>",
			new HashSet<>(), "");

	/**
	 * Clé du fichier de propriété pour l'attribut uuid
	 */
	public static final String PROPERTY_KEY_USER_UUID = "UUID";

	/**
	 * Clé du fichier de propriété pour l'attribut tag
	 */
	public static final String PROPERTY_KEY_USER_TAG = "Tag";

	/**
	 * Clé du fichier de propriété pour l'attribut password
	 */
	public static final String PROPERTY_KEY_USER_PASSWORD = "This_is_not_the_password";

	/**
	 * Clé du fichier de propriété pour l'attribut name
	 */
	public static final String PROPERTY_KEY_USER_NAME = "Name";

	/**
	 * Clé du fichier de propriété pour l'attribut follows
	 */
	public static final String PROPERTY_KEY_USER_FOLLOWS = "Follows";

	/**
	 * Clé du fichier de propriété pour l'attribut Avatar
	 */
	public static final String PROPERTY_KEY_USER_AVATAR = "Avatar";

	/**
	 * Clé du fichier de propriété pour l'attribut Avatar
	 */
	public static final String PROPERTY_FOLLOW_SEPARATOR = ";";

	/**
	 * Clé du fichier de propriété pour l'attribut name
	 */
	public static final String PROPERTY_KEY_MESSAGE_UUID = "UUID";

	/**
	 * Clé du fichier de propriété pour l'attribut follows
	 */
	public static final String PROPERTY_KEY_MESSAGE_SENDER = "Sender";

	/**
	 * Clé du fichier de propriété pour l'attribut Avatar
	 */
	public static final String PROPERTY_KEY_MESSAGE_DATE = "Date";

	/**
	 * Clé du fichier de propriété pour l'attribut Avatar
	 */
	public static final String PROPERTY_KEY_MESSAGE_TEXT = "Text";

	/**
	 * Lecture du fichier de propriété pour un {@link User}
	 *
	 */
	public static User readUser(File userFile) {
		User user = null;

		if (userFile != null && userFile.getName().endsWith(Constants.USER_FILE_EXTENSION) && userFile.exists()) {
			Properties properties = PropertiesManager.loadProperties(userFile.getAbsolutePath());

			String uuid = properties.getProperty(PROPERTY_KEY_USER_UUID, UUID.randomUUID().toString());
			String tag = properties.getProperty(PROPERTY_KEY_USER_TAG, "NoTag");
			String password = decrypt(properties.getProperty(PROPERTY_KEY_USER_PASSWORD, "NoPassword"));
			String name = properties.getProperty(PROPERTY_KEY_USER_NAME, "NoName");
			String follows = properties.getProperty(PROPERTY_KEY_USER_FOLLOWS, "");
			String avatar = properties.getProperty(PROPERTY_KEY_USER_AVATAR, "");

			String[] followsArray = follows.split(PROPERTY_FOLLOW_SEPARATOR);
			Set<String> followsAsSet = new HashSet<>();
			for (String follow : followsArray) {
				followsAsSet.add(follow);
			}

			user = new User(UUID.fromString(uuid), tag, password, name, followsAsSet, avatar);
		}

		return user;
	}

	/**
	 * Génération d'un fichier pour un utilisateur ({@link User}).
	 *
	 * @param user         , Utilisateur à générer.
	 * @param destFileName , Fichier de destination.
	 */
	public static void writeUserFile(User user, String destFileName) {
		Properties properties = new Properties();

		properties.setProperty(PROPERTY_KEY_USER_UUID, user.getUuid().toString());
		properties.setProperty(PROPERTY_KEY_USER_TAG, user.getUserTag());
		properties.setProperty(PROPERTY_KEY_USER_PASSWORD, encrypt(user.getUserPassword()));
		properties.setProperty(PROPERTY_KEY_USER_NAME, user.getName());
		String follows = "";
		Set<String> followsAsSet = user.getFollows();
		for (String follow : followsAsSet) {
			follows += follow + PROPERTY_FOLLOW_SEPARATOR;
		}
		properties.setProperty(PROPERTY_KEY_USER_FOLLOWS, follows);
		properties.setProperty(PROPERTY_KEY_USER_AVATAR, user.getAvatarPath());

		PropertiesManager.writeProperties(properties, destFileName);
	}

	/**
	 * Lecture du fichier de propriété pour un {@link Message}
	 *
	 * @param messageFile
	 * @param userMap
	 */
	public static Message readMessage(File messageFile, Map<UUID, User> userMap) {
		Message message = null;

		if (messageFile != null && messageFile.getName().endsWith(Constants.MESSAGE_FILE_EXTENSION)
				&& messageFile.exists()) {
			Properties properties = PropertiesManager.loadProperties(messageFile.getAbsolutePath());

			String uuid = properties.getProperty(PROPERTY_KEY_MESSAGE_UUID, UUID.randomUUID().toString());
			String senderUuid = properties.getProperty(PROPERTY_KEY_MESSAGE_SENDER,
					Constants.UNKNONWN_USER_UUID.toString());
			String emissionDateStr = properties.getProperty(PROPERTY_KEY_MESSAGE_DATE, "0");
			String text = properties.getProperty(PROPERTY_KEY_MESSAGE_TEXT, "NoText");

			User sender = getUserFromUuid(senderUuid, userMap);
			long emissionDate = Long.valueOf(emissionDateStr);

			message = new Message(UUID.fromString(uuid), sender, emissionDate, text);
		}

		return message;
	}

	/**
	 * Génération d'un fichier pour un Message ({@link Message}).
	 *
	 * @param message      , MEssage à générer.
	 * @param destFileName , Fichier de destination.
	 */
	public static void writeMessageFile(Message message, String destFileName) {
		Properties properties = new Properties();

		properties.setProperty(PROPERTY_KEY_MESSAGE_UUID, message.getUuid().toString());
		properties.setProperty(PROPERTY_KEY_MESSAGE_SENDER, message.getSender().getUuid().toString());
		properties.setProperty(PROPERTY_KEY_MESSAGE_DATE, String.valueOf(message.getEmissionDate()));
		properties.setProperty(PROPERTY_KEY_MESSAGE_TEXT, message.getText());

		PropertiesManager.writeProperties(properties, destFileName);
	}

	/**
	 * Récupération de l'utilisateur identifié.
	 * 
	 * @param uuid
	 * @param userMap
	 * @return
	 */
	protected static User getUserFromUuid(String uuid, Map<UUID, User> userMap) {
		// Récupération de l'utilisateur en fonction de l'UUID
		User user = userMap.get(UUID.fromString(uuid));
		if (user == null) {
			user = userMap.get(Constants.UNKNONWN_USER_UUID);
		}

		return user;
	}

	public static String encrypt(String data) {
		return Base64.getEncoder().encodeToString(data.getBytes());
	}

	public static String decrypt(String encryptedData) {
		byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
		return new String(decodedBytes);
	}
}
