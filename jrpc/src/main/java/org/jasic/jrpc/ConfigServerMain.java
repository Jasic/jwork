package org.jasic.jrpc;

import org.jasic.jrpc.rserver.ConfigServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author 菜鹰.
 * @Date 2014/12/17
 */
public class ConfigServerMain {

    private static Logger logger = LoggerFactory.getLogger(ConfigServerMain.class);

    public static void main(String[] args) {
        logger.info(">> start catserver success````");
        ConfigServer catServer = new ConfigServer();
        catServer.service();
    }
}
