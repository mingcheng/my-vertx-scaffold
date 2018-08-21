package com.gracecode.scaffold;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class Launcher extends io.vertx.core.Launcher {
    private static Logger logger = LoggerFactory.getLogger(Launcher.class.getName());

    public static void main(String[] args) {
        try {
            new Launcher().dispatch(args);
        } catch (RuntimeException e) {
            logger.fatal(e.getMessage(), e);
        }
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        super.beforeDeployingVerticle(deploymentOptions);

        try {
            Optional.of(deploymentOptions.getConfig());
        } catch (NullPointerException e) {
            logger.info("Default deployment configure is empty, initialize.");
            deploymentOptions.setConfig(new JsonObject());
        }

        deploymentOptions.getConfig().mergeIn(getConfiguration());
    }


    /**
     * Read Project's Configure from application.json.
     *
     * @return JsonObject
     */
    private JsonObject getConfiguration() {
        try {
            URL url = Resources.getResource("application.json");
            logger.debug("Load project's configure from %s.", url.getFile());
            return new JsonObject(Resources.toString(url, Charsets.UTF_8));
        } catch (NullPointerException | IOException | DecodeException e) {
            logger.error(e.getMessage());
            return new JsonObject();
        }
    }
}
