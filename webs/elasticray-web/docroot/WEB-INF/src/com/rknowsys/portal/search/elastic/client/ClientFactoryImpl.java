/*******************************************************************************
 * Copyright (c) 2014 R-Knowsys Technologies, http://www.rknowsys.com
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 *******************************************************************************/
package com.rknowsys.portal.search.elastic.client;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import java.security.MessageDigest;
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
                sb.append(line.trim());
                line = br.readLine();
            }
            byte[] tempBytes = sb.toString().getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(tempBytes);
            StringBuilder sb2 = new StringBuilder();
            for (byte b : array) {
                sb2.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            String newVersion = sb2.toString();
            if (cont || !newVersion.equals(version)) {
                if (exists) {
                    client.admin().indices().prepareDeleteTemplate(Liferay_Template + "*").execute().actionGet();
                }
                client.admin().indices().preparePutTemplate(Liferay_Template + "_" + newVersion).setSource(sb.toString()).execute().actionGet();

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
