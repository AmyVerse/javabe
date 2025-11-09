package app;

import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Use the User class defined in Users.java
import app.Users.User; 

public class RedisStorage {

    // Unique key to store the entire list of users as a JSON string
    private static final String USERS_DATA_KEY = "app:all_users_json";
    
    // Type token for Gson to correctly deserialize List<User>
    private static final Type USER_LIST_TYPE = new TypeToken<List<User>>() {}.getType();

    // 💡 IMPORTANT: Get connection string from Railway environment variable
    private static Jedis getConnection() {
        // Reads the connection string from the environment set by Railway
        String redisUrl = System.getenv("REDIS_URL"); 
        if (redisUrl == null) {
            // Fallback for local testing (replace with your local redis URL)
            // If you don't use local Redis, remove this line.
            // redisUrl = "redis://localhost:6379"; 
            System.err.println("REDIS_URL environment variable is not set!");
            return null;
        }
        return new Jedis(redisUrl);
    }

    /**
     * Loads the entire List<User> from Redis.
     * Returns an empty list if the key is not found or an error occurs.
     */
    public static List<User> loadUsers() {
        try (Jedis jedis = getConnection()) {
            if (jedis == null) return new ArrayList<>();
            
            String jsonString = jedis.get(USERS_DATA_KEY);

            if (jsonString == null || jsonString.isEmpty()) {
                return new ArrayList<>(); // Return empty list if no data is stored
            }

            Gson gson = new Gson();
            return gson.fromJson(jsonString, USER_LIST_TYPE);
            
        } catch (Exception e) {
            System.err.println("Error loading users from Redis: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves the entire List<User> to Redis as a single JSON string.
     */
    public static void saveUsers(List<User> users) {
        try (Jedis jedis = getConnection()) {
            if (jedis == null) return;
            
            Gson gson = new Gson();
            String jsonString = gson.toJson(users);
            
            // SET stores the JSON string under the single USERS_DATA_KEY
            jedis.set(USERS_DATA_KEY, jsonString);
            
        } catch (Exception e) {
            System.err.println("Error saving users to Redis: " + e.getMessage());
        }
    }
}