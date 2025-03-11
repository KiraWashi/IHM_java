package main.java.com.ubo.tp.message.ihm.messages.list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.message.IMessage;
import main.java.com.ubo.tp.message.datamodel.message.Message;
import main.java.com.ubo.tp.message.datamodel.user.IUser;
import main.java.com.ubo.tp.message.datamodel.user.User;

/**
 * Contrôleur pour la gestion de la liste des messages
 */
public class MessageListController {

    /**
     * Session active
     */
    private final ISession session;

    private final IMessage messageList;

    private final IUser userList;

    /**
     * Constructeur
     *
     * @param session Session active
     */
    public MessageListController(ISession session, IMessage message, IUser user) {
        this.session = session;
        this.messageList = message;
        this.userList = user;
    }

    /**
     * Récupère les messages pour l'utilisateur connecté
     * (ceux qu'il a envoyés et ceux des utilisateurs qu'il suit)
     *
     * @return Liste des messages pertinents
     */
    public List<Message> getRelevantMessages() {
        User currentUser = session.getConnectedUser();

        if (currentUser == null) {
            return new ArrayList<>();
        }

        // Récupération des utilisateurs suivis
        Set<String> followedTags = currentUser.getFollows();

        // Ensemble des messages à afficher
        Set<Message> relevantMessages = new HashSet<>();

        // Messages de l'utilisateur connecté
        relevantMessages.addAll(messageList.getUserMessages(currentUser));

        // Messages des utilisateurs suivis
        for (User user : userList.getUsers()) {
            if (followedTags.contains(user.getUserTag())) {
                relevantMessages.addAll(messageList.getUserMessages(user));
            }
        }

        // Conversion en liste et tri
        List<Message> messageList = new ArrayList<>(relevantMessages);
        messageList.sort(new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return Long.compare(m1.getEmissionDate(), m2.getEmissionDate());
            }
        });

        return messageList;
    }

    /**
     * Recherche des messages
     *
     * @param searchQuery Texte de recherche
     * @return Liste des messages correspondants
     */
    public List<Message> searchMessages(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return getRelevantMessages();
        }

        searchQuery = searchQuery.trim();
        Set<Message> searchResults = new HashSet<>();

        // Recherche par utilisateur (@...)
        if (searchQuery.startsWith("@")) {
            String userTag = searchQuery.substring(1);

            // Messages émis par cet utilisateur
            for (User user : userList.getUsers()) {
                if (user.getUserTag().equals(userTag)) {
                    searchResults.addAll(messageList.getUserMessages(user));
                    break;
                }
            }

            // Messages citant cet utilisateur
            searchResults.addAll(messageList.getMessagesWithUserTag(userTag));
        }
        // Recherche par tag (#...)
        else if (searchQuery.startsWith("#")) {
            String tag = searchQuery.substring(1);
            searchResults.addAll(messageList.getMessagesWithTag(tag));
        }
        // Recherche générale (union des deux critères)
        else {
            // Partie 1: Recherche par tous les utilisateurs possibles
            for (User user : userList.getUsers()) {
                if (user.getUserTag().contains(searchQuery) || user.getName().contains(searchQuery)) {
                    // Messages émis par ces utilisateurs
                    searchResults.addAll(messageList.getUserMessages(user));

                    // Messages citant ces utilisateurs
                    searchResults.addAll(messageList.getMessagesWithUserTag(user.getUserTag()));
                }
            }

            // Partie 2: Recherche pour tous les tags possibles
            // Considérer le terme comme un tag potentiel
            searchResults.addAll(messageList.getMessagesWithTag(searchQuery));
        }

        // Conversion en liste et tri
        List<Message> messageList = new ArrayList<>(searchResults);
        messageList.sort(new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                // Tri par date (plus ancien en premier comme demandé)
                return Long.compare(m1.getEmissionDate(), m2.getEmissionDate());
            }
        });

        return messageList;
    }
}