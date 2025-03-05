package main.java.com.ubo.tp.message.ihm.messages.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion de la liste des messages
 */
public class MessageListController {

    /**
     * Base de données de l'application
     */
    private IDatabase database;

    /**
     * Session active
     */
    private ISession session;

    /**
     * Constructeur
     *
     * @param database Base de données
     * @param session Session active
     */
    public MessageListController(IDatabase database, ISession session) {
        this.database = database;
        this.session = session;
    }

    /**
     * Récupère tous les messages
     *
     * @return Liste des messages
     */
    public List<Message> getAllMessages() {
        Set<Message> messageSet = database.getMessages();
        List<Message> messageList = new ArrayList<>(messageSet);

        // Tri par date (plus récent en premier)
        Collections.sort(messageList, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return Long.compare(m1.getEmissionDate(), m2.getEmissionDate());
            }
        });

        return messageList;
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
        relevantMessages.addAll(database.getUserMessages(currentUser));

        // Messages des utilisateurs suivis
        for (User user : database.getUsers()) {
            if (followedTags.contains(user.getUserTag())) {
                relevantMessages.addAll(database.getUserMessages(user));
            }
        }

        // Conversion en liste et tri
        List<Message> messageList = new ArrayList<>(relevantMessages);
        Collections.sort(messageList, new Comparator<Message>() {
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
            for (User user : database.getUsers()) {
                if (user.getUserTag().equals(userTag)) {
                    searchResults.addAll(database.getUserMessages(user));
                    break;
                }
            }

            // Messages citant cet utilisateur
            searchResults.addAll(database.getMessagesWithUserTag(userTag));
        }
        // Recherche par tag (#...)
        else if (searchQuery.startsWith("#")) {
            String tag = searchQuery.substring(1);
            searchResults.addAll(database.getMessagesWithTag(tag));
        }
        // Recherche générale (union des deux critères)
        else {
            // Partie 1: Recherche par tous les utilisateurs possibles
            for (User user : database.getUsers()) {
                if (user.getUserTag().contains(searchQuery) || user.getName().contains(searchQuery)) {
                    // Messages émis par ces utilisateurs
                    searchResults.addAll(database.getUserMessages(user));

                    // Messages citant ces utilisateurs
                    searchResults.addAll(database.getMessagesWithUserTag(user.getUserTag()));
                }
            }

            // Partie 2: Recherche pour tous les tags possibles
            // Considérer le terme comme un tag potentiel
            searchResults.addAll(database.getMessagesWithTag(searchQuery));
        }

        // Conversion en liste et tri
        List<Message> messageList = new ArrayList<>(searchResults);
        Collections.sort(messageList, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                // Tri par date (plus ancien en premier comme demandé)
                return Long.compare(m1.getEmissionDate(), m2.getEmissionDate());
            }
        });

        return messageList;
    }
}