package app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

import io.javalin.Javalin;
import redis.clients.jedis.Jedis;

/**
 * WiPay Banking API - Minimal OOP, simple Redis storage
 * Features: Users, Bank Accounts, Transfers, Notifications, Reports
 */
public class WiPayApi {

    private static final Gson gson = new Gson();
    private static final String USERS_KEY = "wipay:users";
    private static final String ACCOUNTS_KEY = "wipay:accounts";
    private static final String TRANSACTIONS_KEY = "wipay:transactions";
    private static final String NOTIFICATIONS_KEY = "wipay:notifications";

    public static void main(String[] args) {
        int port = getPort();
        Javalin app = createJavalinApp(port);
        System.out.println("🚀 WiPay API running at http://localhost:" + port);
        setupRoutes(app);
    }

    private static int getPort() {
        String portEnv = System.getenv("PORT");
        return portEnv != null ? Integer.parseInt(portEnv) : 8080;
    }

    private static Jedis getRedisConnection() {
        String redisUrl = System.getenv("REDIS_URL");
        if (redisUrl != null && !redisUrl.isEmpty()) {
            // Parse Railway Redis URL format: redis://:password@host:port
            try {
                java.net.URI uri = new java.net.URI(redisUrl);
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 6379;
                String password = uri.getUserInfo() != null ? uri.getUserInfo().split(":")[1] : null;

                Jedis jedis = new Jedis(host, port);
                if (password != null && !password.isEmpty()) {
                    jedis.auth(password);
                }
                return jedis;
            } catch (Exception e) {
                System.err.println("Failed to parse REDIS_URL, using localhost");
            }
        }
        // Fallback to localhost
        return new Jedis("localhost", 6379);
    }

    private static Javalin createJavalinApp(int port) {
        return Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(corsConfig -> corsConfig.anyHost()));
        }).start("0.0.0.0", port);
    }

    private static void setupRoutes(Javalin app) {
        // ===== HEALTH CHECK =====
        app.get("/api/ping", ctx -> ctx.json(Map.of("status", "OK")));

        // ===== USER ENDPOINTS =====
        app.post("/api/users", ctx -> {
            Map<String, Object> body = gson.fromJson(ctx.body(), Map.class);
            String firstName = (String) body.get("firstName");
            String lastName = (String) body.get("lastName");
            double balance = ((Number) body.getOrDefault("balance", 0)).doubleValue();

            String userId = firstName.toLowerCase() + "@wipay";
            Map<String, Object> user = new HashMap<>();
            user.put("id", userId);
            user.put("firstName", firstName);
            user.put("lastName", lastName);
            user.put("balance", balance);
            user.put("createdAt", LocalDateTime.now().toString());

            try (Jedis jedis = getRedisConnection()) {
                jedis.hset(USERS_KEY, userId, gson.toJson(user));
            }
            ctx.status(201).json(user);
        });

        app.get("/api/users", ctx -> {
            try (Jedis jedis = getRedisConnection()) {
                Map<String, String> users = jedis.hgetAll(USERS_KEY);
                List<Map<String, Object>> userList = new ArrayList<>();
                for (String userJson : users.values()) {
                    userList.add(gson.fromJson(userJson, Map.class));
                }
                ctx.json(userList);
            }
        });

        app.get("/api/users/{userId}", ctx -> {
            String userId = ctx.pathParam("userId");
            try (Jedis jedis = getRedisConnection()) {
                String userJson = jedis.hget(USERS_KEY, userId);
                if (userJson == null) {
                    ctx.status(404).json(Map.of("error", "User not found"));
                } else {
                    ctx.json(gson.fromJson(userJson, Map.class));
                }
            }
        });

        // ===== BANK ACCOUNTS ENDPOINTS =====
        app.post("/api/accounts", ctx -> {
            Map<String, Object> body = gson.fromJson(ctx.body(), Map.class);
            String userId = (String) body.get("userId");
            String accountNumber = (String) body.get("accountNumber");
            String bankName = (String) body.get("bankName");
            double balance = ((Number) body.getOrDefault("balance", 0)).doubleValue();

            Map<String, Object> account = new HashMap<>();
            account.put("id", UUID.randomUUID().toString());
            account.put("userId", userId);
            account.put("accountNumber", accountNumber);
            account.put("bankName", bankName);
            account.put("balance", balance);
            account.put("createdAt", LocalDateTime.now().toString());

            try (Jedis jedis = getRedisConnection()) {
                jedis.hset(ACCOUNTS_KEY, account.get("id").toString(), gson.toJson(account));
                // Update user balance
                String userJson = jedis.hget(USERS_KEY, userId);
                if (userJson != null) {
                    Map<String, Object> user = gson.fromJson(userJson, Map.class);
                    user.put("balance", balance);
                    jedis.hset(USERS_KEY, userId, gson.toJson(user));
                }
            }
            ctx.status(201).json(account);
        });

        app.get("/api/accounts", ctx -> {
            try (Jedis jedis = getRedisConnection()) {
                Map<String, String> accounts = jedis.hgetAll(ACCOUNTS_KEY);
                List<Map<String, Object>> accountList = new ArrayList<>();
                for (String accountJson : accounts.values()) {
                    accountList.add(gson.fromJson(accountJson, Map.class));
                }
                ctx.json(accountList);
            }
        });

        app.get("/api/accounts/{userId}", ctx -> {
            String userId = ctx.pathParam("userId");
            try (Jedis jedis = getRedisConnection()) {
                Map<String, String> accounts = jedis.hgetAll(ACCOUNTS_KEY);
                List<Map<String, Object>> userAccounts = new ArrayList<>();
                for (String accountJson : accounts.values()) {
                    Map<String, Object> acc = gson.fromJson(accountJson, Map.class);
                    if (userId.equals(acc.get("userId"))) {
                        userAccounts.add(acc);
                    }
                }
                ctx.json(userAccounts);
            }
        });

        // ===== CONTACTS ENDPOINT =====
        app.get("/api/contacts", ctx -> {
            try (Jedis jedis = getRedisConnection()) {
                Map<String, String> users = jedis.hgetAll(USERS_KEY);
                List<Map<String, Object>> contacts = new ArrayList<>();
                for (String userJson : users.values()) {
                    Map<String, Object> user = gson.fromJson(userJson, Map.class);
                    Map<String, Object> contact = new HashMap<>();
                    contact.put("id", user.get("id"));
                    contact.put("name", user.get("firstName") + " " + user.get("lastName"));
                    contact.put("paymentId", user.get("id"));
                    contacts.add(contact);
                }
                ctx.json(contacts);
            }
        });

        // ===== TRANSFER/TRANSACTION ENDPOINT =====
        app.post("/api/transfer", ctx -> {
            Map<String, Object> body = gson.fromJson(ctx.body(), Map.class);
            String fromUserId = (String) body.get("fromUserId");
            String toUserId = (String) body.get("toUserId");
            double amount = ((Number) body.get("amount")).doubleValue();

            try (Jedis jedis = getRedisConnection()) {
                // Get sender
                String senderJson = jedis.hget(USERS_KEY, fromUserId);
                if (senderJson == null) {
                    ctx.status(404).json(Map.of("error", "Sender not found"));
                    return;
                }

                // Get receiver
                String receiverJson = jedis.hget(USERS_KEY, toUserId);
                if (receiverJson == null) {
                    ctx.status(404).json(Map.of("error", "Receiver not found"));
                    return;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> sender = gson.fromJson(senderJson, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> receiver = gson.fromJson(receiverJson, Map.class);

                double senderBalance = ((Number) sender.get("balance")).doubleValue();
                double receiverBalance = ((Number) receiver.get("balance")).doubleValue();

                // Check balance
                if (senderBalance < amount) {
                    ctx.status(400).json(Map.of("error", "Insufficient balance"));
                    return;
                }

                // Create transaction
                Map<String, Object> transaction = new HashMap<>();
                String txnId = UUID.randomUUID().toString();
                transaction.put("id", txnId);
                transaction.put("fromUserId", fromUserId);
                transaction.put("toUserId", toUserId);
                transaction.put("amount", amount);
                transaction.put("timestamp", LocalDateTime.now().toString());
                transaction.put("status", "completed");

                // Update balances
                sender.put("balance", senderBalance - amount);
                receiver.put("balance", receiverBalance + amount);

                // Save all changes
                jedis.hset(USERS_KEY, fromUserId, gson.toJson(sender));
                jedis.hset(USERS_KEY, toUserId, gson.toJson(receiver));
                jedis.hset(TRANSACTIONS_KEY, txnId, gson.toJson(transaction));

                // Create notifications
                String notificationSender = String.format("%s sent ₹%.2f to %s",
                        fromUserId, amount, toUserId);
                String notificationReceiver = String.format("Received ₹%.2f from %s",
                        amount, fromUserId);

                jedis.lpush(NOTIFICATIONS_KEY + ":" + fromUserId,
                        gson.toJson(
                                Map.of("message", notificationSender, "timestamp", LocalDateTime.now().toString())));
                jedis.lpush(NOTIFICATIONS_KEY + ":" + toUserId,
                        gson.toJson(
                                Map.of("message", notificationReceiver, "timestamp", LocalDateTime.now().toString())));

                ctx.status(201).json(transaction);
            }
        });

        // ===== ACCOUNT-SPECIFIC TRANSFER ENDPOINT =====
        app.post("/api/transfer-account", ctx -> {
            Map<String, Object> body = gson.fromJson(ctx.body(), Map.class);
            String fromAccountId = (String) body.get("fromAccountId");
            String toAccountId = (String) body.get("toAccountId");
            double amount = ((Number) body.get("amount")).doubleValue();

            try (Jedis jedis = getRedisConnection()) {
                // Get sender account
                String fromAccJson = jedis.hget(ACCOUNTS_KEY, fromAccountId);
                if (fromAccJson == null) {
                    ctx.status(404).json(Map.of("error", "Sender account not found"));
                    return;
                }

                // Get receiver account
                String toAccJson = jedis.hget(ACCOUNTS_KEY, toAccountId);
                if (toAccJson == null) {
                    ctx.status(404).json(Map.of("error", "Receiver account not found"));
                    return;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> fromAccount = gson.fromJson(fromAccJson, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> toAccount = gson.fromJson(toAccJson, Map.class);

                double fromBalance = ((Number) fromAccount.get("balance")).doubleValue();
                double toBalance = ((Number) toAccount.get("balance")).doubleValue();

                // Check balance
                if (fromBalance < amount) {
                    ctx.status(400).json(Map.of("error", "Insufficient balance in sender account"));
                    return;
                }

                // Create transaction
                Map<String, Object> transaction = new HashMap<>();
                String txnId = UUID.randomUUID().toString();
                transaction.put("id", txnId);
                transaction.put("fromAccountId", fromAccountId);
                transaction.put("toAccountId", toAccountId);
                transaction.put("fromUserId", fromAccount.get("userId"));
                transaction.put("toUserId", toAccount.get("userId"));
                transaction.put("amount", amount);
                transaction.put("timestamp", LocalDateTime.now().toString());
                transaction.put("status", "completed");

                // Update account balances
                fromAccount.put("balance", fromBalance - amount);
                toAccount.put("balance", toBalance + amount);

                // Save all changes
                jedis.hset(ACCOUNTS_KEY, fromAccountId, gson.toJson(fromAccount));
                jedis.hset(ACCOUNTS_KEY, toAccountId, gson.toJson(toAccount));
                jedis.hset(TRANSACTIONS_KEY, txnId, gson.toJson(transaction));

                // Create notifications
                String fromUserId = (String) fromAccount.get("userId");
                String toUserId = (String) toAccount.get("userId");
                String fromAccNumber = (String) fromAccount.get("accountNumber");
                String toAccNumber = (String) toAccount.get("accountNumber");

                String notificationSender = String.format("Transfer: Sent ₹%.2f from %s to account %s",
                        amount, fromAccNumber, toAccNumber);
                String notificationReceiver = String.format("Transfer: Received ₹%.2f to %s from account %s",
                        amount, toAccNumber, fromAccNumber);

                jedis.lpush(NOTIFICATIONS_KEY + ":" + fromUserId,
                        gson.toJson(Map.of("message", notificationSender, "timestamp", LocalDateTime.now().toString())));
                jedis.lpush(NOTIFICATIONS_KEY + ":" + toUserId,
                        gson.toJson(Map.of("message", notificationReceiver, "timestamp", LocalDateTime.now().toString())));

                ctx.status(201).json(transaction);
            }
        });

        // ===== NOTIFICATIONS ENDPOINT =====
        app.get("/api/notifications/{userId}", ctx -> {
            String userId = ctx.pathParam("userId");
            try (Jedis jedis = getRedisConnection()) {
                List<String> notifs = jedis.lrange(NOTIFICATIONS_KEY + ":" + userId, 0, -1);
                List<Map<String, Object>> notifications = new ArrayList<>();
                for (String notifJson : notifs) {
                    notifications.add(gson.fromJson(notifJson, Map.class));
                }
                ctx.json(notifications);
            }
        });

        // ===== REPORTS ENDPOINT =====
        app.get("/api/reports/{userId}", ctx -> {
            String userId = ctx.pathParam("userId");
            try (Jedis jedis = getRedisConnection()) {
                // Get user
                String userJson = jedis.hget(USERS_KEY, userId);
                if (userJson == null) {
                    ctx.status(404).json(Map.of("error", "User not found"));
                    return;
                }

                Map<String, String> transactions = jedis.hgetAll(TRANSACTIONS_KEY);
                List<Map<String, Object>> userTransactions = new ArrayList<>();
                for (String txJson : transactions.values()) {
                    Map<String, Object> tx = gson.fromJson(txJson, Map.class);
                    if (userId.equals(tx.get("fromUserId")) || userId.equals(tx.get("toUserId"))) {
                        userTransactions.add(tx);
                    }
                }

                double totalSent = 0;
                double totalReceived = 0;

                for (Map<String, Object> tx : userTransactions) {
                    double amount = ((Number) tx.get("amount")).doubleValue();
                    if (userId.equals(tx.get("fromUserId"))) {
                        totalSent += amount;
                    } else {
                        totalReceived += amount;
                    }
                }

                Map<String, Object> report = new HashMap<>();
                report.put("userId", userId);
                report.put("totalTransactions", userTransactions.size());
                report.put("totalSent", totalSent);
                report.put("totalReceived", totalReceived);
                report.put("currentBalance", gson.fromJson(userJson, Map.class).get("balance"));
                report.put("transactions", userTransactions);

                ctx.json(report);
            }
        });
    }
}
