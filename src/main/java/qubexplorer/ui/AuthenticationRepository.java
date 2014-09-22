package qubexplorer.ui;

import java.util.HashMap;
import java.util.Map;
import qubexplorer.UserCredentials;

/**
 *
 * @author Victor
 */
public class AuthenticationRepository {

    private final Map<String, Map<String, UserCredentials>> cache = new HashMap<>();

    public UserCredentials getAuthentication(String serverUrl, String resourceKey) {
        if (cache.containsKey(serverUrl)) {
            if (cache.get(serverUrl).containsKey(resourceKey)) {
                return cache.get(serverUrl).get(resourceKey);
            } else {
                return cache.get(serverUrl).get(null);
            }
        } else {
            return null;
        }
    }

    public void saveAuthentication(String serverUrl, String resourceKey, UserCredentials authentication) {
        if (!cache.containsKey(serverUrl)) {
            cache.put(serverUrl, new HashMap<String, UserCredentials>());
        }
        cache.get(serverUrl).put(null, authentication);
        if (resourceKey != null) {
            cache.get(serverUrl).put(resourceKey, authentication);
        }
    }

    private static AuthenticationRepository repository;

    public static synchronized AuthenticationRepository getInstance() {
        if (repository == null) {
            repository = new AuthenticationRepository();
        }
        return repository;
    }

}
