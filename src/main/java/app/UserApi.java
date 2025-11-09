package app;

import java.util.HashMap;
import java.util.Map;

import io.javalin.Javalin;

public class UserApi {
    public static void main(String[] args) {
        // 1. Initialize Javalin and start on port 8080
        Javalin app = Javalin.create(config -> {
            // Configuration for CORS (Allows all origins, methods, and headers)
            config.requestLogger.http((ctx, ms) -> {
                // Optional: Basic request logging
                System.out.println(ctx.method() + " " + ctx.path() + " -> " + ctx.status());
            });
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(corsConfig -> { // Use addRule in modern Javalin
                    corsConfig.anyHost(); // Allows requests from *any* origin
                });
            });
        }).start(8080);

        System.out.println("Javalin API running at http://localhost:8080");

        // 2. GET /api/ping
        app.get("/api/ping", ctx -> {
            ctx.result("Server running OK");
            ctx.status(200);
        });

        // 3. POST /api/createUser
        app.post("/api/createUser", ctx -> {
            // Javalin's body() method gets the request body as a string
            Map<String, String> data = parse(ctx.body());

            boolean created = Users.createUser(data.get("name"), data.get("email"), data.get("password"));

            // Use json() helper for automatic JSON content type and serialization
            ctx.json(Map.of("success", created));
        });

        // 4. POST /api/login
        app.post("/api/login", ctx -> {
            Map<String, String> data = parse(ctx.body());

            // Assuming Users.java has User.getName() and User.getEmail()
            var user = Users.checkAuthentication(data.get("email"), data.get("password"));

            if (user != null) {
                // Return success and user name
                ctx.json(Map.of("success", true, "name", user.getName()));
            } else {
                // Return failure
                ctx.json(Map.of("success", false));
            }
        });

        // 5. GET /api/user/:email
        app.get("/api/user/{email}", ctx -> {
            // Use pathParam() to get variables from the URL
            String email = ctx.pathParam("email");
            var user = Users.getUserByEmail(email);

            if (user == null) {
                ctx.status(404);
                ctx.json(Map.of("error", "not found"));
            } else {
                // Return specific user details
                ctx.json(Map.of(
                        "name", user.getName(),
                        "email", user.getEmail()));
            }
        });
    }

    // Existing minimal JSON parser (Assuming it handles the simple format needed)
    private static Map<String, String> parse(String body) {
        Map<String, String> map = new HashMap<>();
        // Note: Javalin handles JSON parsing automatically if you map the body to a
        // POJO.
        // Keeping your custom parser here for minimal changes to your logic.
        body = body.replaceAll("[{}\" ]", "");
        for (String pair : body.split(",")) {
            String[] kv = pair.split(":");
            if (kv.length == 2)
                map.put(kv[0], kv[1]);
        }
        return map;
    }
}