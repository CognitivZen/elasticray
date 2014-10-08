package com.rknowsys.portal.search.elastic.client;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * //TODO Comment goes here
 */
public class ClientFactoryImpl implements ClientFactory {

    public static final String svnRevision = "$Id$";

    public static final String Liferay_Template = "liferay_template";

    private Client client;


    @Override
    public Client getClient() {
        return client;
    }

    public void destroy() {
        client.close();
    }

    public void afterPropertiesSet() {

        Properties properties = PropsUtil.getProperties("elasticsearch.", true);

        String serverIP = properties.getProperty("serverIP", "localhost");
        int port = GetterUtil.get(properties.getProperty("portNumber"), 9300);

        Settings settings = ImmutableSettings.settingsBuilder().classLoader(ClientFactoryImpl.class.getClassLoader()).
                put(properties).build();

        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(serverIP, port));

        GetIndexTemplatesResponse gitr = client.admin().indices().prepareGetTemplates("Liferay_Template").execute().actionGet();
        boolean exists = false;

        for (IndexTemplateMetaData indexTemplateMetaData : gitr.getIndexTemplates()) {
            if (indexTemplateMetaData.getName().equals(Liferay_Template)) {
                exists = true;
            }
        }
        if (!exists) {
            BufferedReader br = null;
            try {
                InputStream is = ClientFactoryImpl.class.getClassLoader().getResourceAsStream("com/rknowsys/portal/search/elastic/template.json");
                br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                client.admin().indices().preparePutTemplate(Liferay_Template).setSource(sb.toString()).execute().actionGet();

            } catch (IOException ignored) {

            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }


    }
}
