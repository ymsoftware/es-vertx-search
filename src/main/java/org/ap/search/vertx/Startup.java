package org.ap.search.vertx;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by ymetelkin on 7/16/15.
 */
public class Startup {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        System.out.println("Deploying verticles...");

        vertx.executeBlocking(future -> {
            String configPath = null;

            if (args != null) {
                Optional<String> test = Arrays
                        .stream(args)
                        .filter(s -> s.startsWith("-config"))
                        .findFirst();

                if (test.isPresent()) {
                    String[] tokens = test.get().split(":");
                    if (tokens.length == 2) {
                        configPath = tokens[1];
                    }
                }

            }

            if (configPath == null) {
                configPath = "verticles.json";
            }

            vertx.fileSystem().readFile(configPath, result -> {
                if (result.succeeded()) {
                    JsonObject jo = new JsonObject(result.result().toString());
                    JsonArray ja = jo.getJsonArray("verticles");
                    for (int i = 0; i < ja.size(); i++) {
                        deployVerticle(vertx, ja.getJsonObject(i));
                    }

                } else {
                    System.err.println("Failed to read config file: " + result.cause());
                }
            });

            future.complete();
        }, res -> {
            System.out.println("Verticles deployed " + (res.succeeded() ? "successfully." : "unsuccessfully."));
        });
    }

    private static void deployVerticle(Vertx vertx, JsonObject config) {
        String verticleId = config.getString("id");

        DeploymentOptions options = new DeploymentOptions();

        int instances = config.getInteger("instances", 1);
        if (instances > 1) {
            options.setInstances(instances);
        }

        config = config.getJsonObject("config", null);
        if (config != null) {
            options.setConfig(config);
        }

        System.out.println("Deploying " + verticleId + "...");

        vertx.deployVerticle(verticleId, options, res -> {
            if (res.succeeded()) {
                System.out.println("Deployed: " + res.result());
            } else {
                System.out.println("Deployment failed: " + res.result());
            }
        });
    }
}
