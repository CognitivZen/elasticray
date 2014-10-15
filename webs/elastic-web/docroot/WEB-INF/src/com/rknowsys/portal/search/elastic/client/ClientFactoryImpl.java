package com.rknowsys.portal.search.elastic.client;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse;
import org.elasticsearch.action.search.SearchResponse;
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

    private static final Log _log = LogFactoryUtil.getLog(ClientFactoryImpl.class);
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

        GetIndexTemplatesResponse gitr = client.admin().indices().prepareGetTemplates(Liferay_Template + "*").execute().actionGet();
        boolean cont = false;
        boolean exists = false;
        String templateName = "";
        String version = "";
        for (IndexTemplateMetaData indexTemplateMetaData : gitr.getIndexTemplates()) {
            if (indexTemplateMetaData.getName().startsWith(Liferay_Template)) {
                templateName = indexTemplateMetaData.getName();
                exists = true;
                break;
            }
        }
        if (templateName.length() > Liferay_Template.length() + 1) {
            version = templateName.substring(Liferay_Template.length() + 1);
        } else {
            cont = true;
        }
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
            JSONObject jsonObect = JSONFactoryUtil.createJSONObject(sb.toString());
            String newVersion = jsonObect.getString("version");
            if (cont || newVersion.compareTo(version) > 0) {
                JSONObject template = jsonObect.getJSONObject("template");
                if (exists) {
                    client.admin().indices().prepareDeleteTemplate(Liferay_Template + "*").execute().actionGet();
                }
                client.admin().indices().preparePutTemplate(Liferay_Template + "_" + newVersion).setSource(template.toString()).execute().actionGet();

                client.admin().indices().prepareDelete("liferay_*").execute().actionGet();

                _log.warn("Please reIndex all search indices in server administration");
            }


        } catch (Exception ignored) {

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
