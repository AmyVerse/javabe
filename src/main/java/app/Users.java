package app;

import java.util.List;

// NOTE: File IO imports are now removed
// import java.io.BufferedReader;
// import java.io.File;
// ...

public class Users {
    // 💡 REMOVED: private static final String USERS_FILE = "users.txt";

    // User data structure (remains the same)
    public static class User {
        private String name;
        private String email;
        private String password;

        public User(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }

        // Getters (remain the same)
        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return String.format("User{name='%s', email='%s'}", name, email);
        }
    }

    // 💡 MODIFIED: Delegates loading to RedisStorage
    private static List<User> loadUsers() {
        return RedisStorage.loadUsers();
    }

    // 💡 MODIFIED: Delegates saving to RedisStorage
    private static void saveUsers(List<User> users) {
        RedisStorage.saveUsers(users);
    }

    // Check authentication - returns User if valid, null if invalid
    public static User checkAuthentication(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        List<User> users = loadUsers(); // Calls RedisStorage.loadUsers()
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // Create new user - returns true if created, false if email exists
    public static boolean createUser(String name, String email, String password) {
        if (name == null || email == null || password == null) {
            return false;
        }

        // Check if user already exists
        List<User> users = loadUsers(); // Calls RedisStorage.loadUsers()
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return false; // Email already exists
            }
        }

        // Add new user
        users.add(new User(name, email, password));
        saveUsers(users); // Calls RedisStorage.saveUsers()
        return true;
    }

    // ... (userExists and getUserByEmail remain the same, as they call loadUsers)
    public static boolean userExists(String email) {
        if (email == null) {
            return false;
        }

        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public static User getUserByEmail(String email) {
        if (email == null) {
            return null;
        }

        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
}