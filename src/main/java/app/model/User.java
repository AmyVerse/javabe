package app.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private String userId;
    private String name;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private boolean isActive;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // getter and setter

    // 1- userId
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 2- name
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 3-email
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // 4- password
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // 5-CreatedAt
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 6-IsActive
    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    // Utility methods for API responses
    public boolean authenticate(String password) {
        return this.password != null && this.password.equals(password);
    }

    public void updateProfile(String name, String email) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        if (email != null && !email.trim().isEmpty()) {
            this.email = email.trim();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return Objects.equals(userId, user.userId) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}
