package org.amse.bomberman.server;

import java.util.Properties;

public class ServerConfig {
    private final Properties config;

    public ServerConfig(Properties config) {
        this.config = config;
    }

    public int getPort() {
        return Integer.valueOf(config.getProperty("server.port", "100500"));
    }
    
    public String getServerType() {
        return config.getProperty("server.type", "simple");
    }
    
    @Override
    public String toString() {
        return config.toString();
    }
}
