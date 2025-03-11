package main.java.com.ubo.tp.message.core.session;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Session de l'application.
 *
 * @author S.Lucas
 */
public class Session implements ISession {

	/**
	 * Utilisateur connecté
	 */
	protected User mConnectedUser;

	/**
	 * Liste des observateurs de la session.
	 * Utilisation de CopyOnWriteArrayList pour éviter les ConcurrentModificationException
	 */
	protected List<ISessionObserver> mObservers = new CopyOnWriteArrayList<>();

	@Override
	public void addObserver(ISessionObserver observer) {
		this.mObservers.add(observer);
	}

	@Override
	public void removeObserver(ISessionObserver observer) {
		this.mObservers.remove(observer);
	}

	@Override
	public User getConnectedUser() {
		return mConnectedUser;
	}

	@Override
	public void connect(User connectedUser) {
		this.mConnectedUser = connectedUser;

		// Notification aux observateurs
		for (ISessionObserver observer : mObservers) {
			observer.notifyLogin(connectedUser);
		}
	}

	@Override
	public void disconnect() {
		this.mConnectedUser = null;

		// Notification aux observateurs
		for (ISessionObserver observer : mObservers) {
			observer.notifyLogout();
		}
	}
}