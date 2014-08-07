/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.client;

import java.util.Properties;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * //TODO Comment goes here
 */
public class ClientFactoryImpl implements ClientFactory {

    public static final String svnRevision = "$Id$";

    private Client client;

    private String serverIP;

    private int port;

    private Properties properties;

    @Override
    public Client getClient() {
        return client;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void destroy() {
        client.close();
    }

    public void afterPropertiesSet() {
        Settings settings = ImmutableSettings.settingsBuilder().classLoader(ClientFactoryImpl.class.getClassLoader()).
                put(properties).build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(serverIP,port));
    }
}
