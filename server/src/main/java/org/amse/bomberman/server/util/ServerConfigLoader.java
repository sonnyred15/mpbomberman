package org.amse.bomberman.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.amse.bomberman.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ServerConfigLoader.class);

    private static final String SERVER_CONFIG_FILE_NAME = "server.config";

    public ServerConfig load() throws IOException {
        InputStream is = ServerConfigLoader.class.getClassLoader().getResourceAsStream(SERVER_CONFIG_FILE_NAME);
        Properties configProperties = new Properties();
        configProperties.load(is);
        ServerConfig config = new ServerConfig(configProperties);

        return config;
    }
}
