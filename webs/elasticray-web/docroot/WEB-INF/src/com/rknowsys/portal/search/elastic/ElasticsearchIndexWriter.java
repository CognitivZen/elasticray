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
package com.rknowsys.portal.search.elastic;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.rknowsys.portal.search.elastic.client.ClientFactory;

public class ElasticsearchIndexWriter implements IndexWriter {

    private ClientFactory clientFactory;

    private static final Log _log = LogFactoryUtil.getLog(ElasticsearchIndexWriter.class);


    @Override
    public void addDocument(SearchContext searchContext, Document document)
            throws SearchException {
        try {
            IndexRequestBuilder updateRequestBuilder =
                    getUpdateRequestBuilder(searchContext, document);

            Future<IndexResponse> future = updateRequestBuilder.execute();

            IndexResponse updateResponse = future.get();

        } catch (Exception e) {
            throw new SearchException(
                    "Unable to update document " + document, e);
        }

    }

    @Override
    public void addDocuments(
            SearchContext searchContext, Collection<Document> documents)
            throws SearchException {

        try {
            Client client = getClient();

            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

            for (Document document : documents) {
                IndexRequestBuilder updateRequestBuilder =
                        getUpdateRequestBuilder(searchContext, document);

                bulkRequestBuilder.add(updateRequestBuilder);
            }

            Future<BulkResponse> future = bulkRequestBuilder.execute();

            BulkResponse bulkResponse = future.get();

        } catch (Exception e) {
            throw new SearchException(
                    "Unable to update documents " + documents, e);
        }

    }

    @Override
    public void deleteDocument(SearchContext searchContext, String uid)
            throws SearchException {

        try {
            Client client = getClient();

            DeleteRequestBuilder deleteRequestBuilder = client.prepareDelete(
                    String.valueOf("liferay_" + searchContext.getCompanyId()),
                    "documents", uid);

            Future<DeleteResponse> future = deleteRequestBuilder.execute();

            DeleteResponse deleteResponse = future.get();

        } catch (Exception e) {
            throw new SearchException("Unable to delete document " + uid, e);
        }
    }

    @Override
    public void deleteDocuments(
            SearchContext searchContext, Collection<String> uids)
            throws SearchException {

        try {
            Client client = getClient();

            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

            for (String uid : uids) {
                DeleteRequestBuilder deleteRequestBuilder =
                        client.prepareDelete(
                                String.valueOf("liferay_" + searchContext.getCompanyId()),
                                "documents", uid);

                bulkRequestBuilder.add(deleteRequestBuilder);
            }

            Future<BulkResponse> future = bulkRequestBuilder.execute();

            BulkResponse bulkResponse = future.get();

        } catch (Exception e) {
            throw new SearchException("Unable to delete documents " + uids, e);
        }
    }

    @Override
    public void deletePortletDocuments(
            SearchContext searchContext, String portletId)
            throws SearchException {

        try {
            Client client = getClient();

            DeleteByQueryRequestBuilder deleteByQueryRequestBuilder =
                    client.prepareDeleteByQuery(
                            String.valueOf(searchContext.getCompanyId()));

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(
                    QueryBuilders.termQuery(Field.PORTLET_ID, portletId));

            deleteByQueryRequestBuilder.setQuery(boolQueryBuilder);

            Future<DeleteByQueryResponse> future =
                    deleteByQueryRequestBuilder.execute();

            DeleteByQueryResponse deleteByQueryResponse = future.get();

        } catch (Exception e) {
            throw new SearchException(
                    "Unable to delete data for portlet " + portletId, e);
        }
    }


    @Override
    public void updateDocument(SearchContext searchContext, Document document)
            throws SearchException {

        try {
            IndexRequestBuilder updateRequestBuilder =
                    getUpdateRequestBuilder(searchContext, document);

            Future<IndexResponse> future = updateRequestBuilder.execute();

            IndexResponse updateResponse = future.get();

        } catch (Exception e) {
            throw new SearchException(
                    "Unable to update document " + document, e);
        }
    }

    @Override
    public void updateDocuments(
            SearchContext searchContext, Collection<Document> documents)
            throws SearchException {

        try {
            Client client = getClient();

            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

            for (Document document : documents) {
                IndexRequestBuilder updateRequestBuilder =
                        getUpdateRequestBuilder(
                                 searchContext, document);

                bulkRequestBuilder.add(updateRequestBuilder);
            }

            Future<BulkResponse> future = bulkRequestBuilder.execute();

            BulkResponse bulkResponse = future.get();

        } catch (Exception e) {
            throw new SearchException(
                    "Unable to update documents " + documents, e);
        }
    }

    @Override
    public void clearQuerySuggestionDictionaryIndexes(
            SearchContext searchContext) {
    }

    @Override
    public void clearSpellCheckerDictionaryIndexes(
            SearchContext searchContext) {
    }

    @Override
    public void indexKeyword(
            SearchContext searchContext, float weight, String keywordType) {
    }

    @Override
    public void indexQuerySuggestionDictionaries(SearchContext searchContext) {
    }

    @Override
    public void indexQuerySuggestionDictionary(SearchContext searchContext) {
    }

    @Override
    public void indexSpellCheckerDictionaries(SearchContext searchContext) {
    }

    @Override
    public void indexSpellCheckerDictionary(SearchContext searchContext) {
    }

    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    private Client getClient() {
        return clientFactory.getClient();
    }


    private String getElasticsearchDocument(Document document)
            throws IOException {

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();

        xContentBuilder.startObject();

        Map<String, Field> fields = document.getFields();

        for (Field field : fields.values()) {
            String name = field.getName();

            if (!field.isLocalized()) {

                if (field.isNumeric()) {
                    Class clazz = field.getNumericClass();
                    if (clazz.equals(Double.class)) {
                        double[] values = GetterUtil.getDoubleValues(field.getValues());
                        if (values.length > 1) {
                            xContentBuilder.field(name, values);
                        } else {
                            xContentBuilder.field(name,values[0]);
                        }
                    }
                    else if (clazz.equals(Float.class)) {
                        float[] values = GetterUtil.getFloatValues(field.getValues());
                        if (values.length > 1) {
                            xContentBuilder.field(name, values);
                        } else {
                            xContentBuilder.field(name,values[0]);
                        }
                    }
                    else if (clazz.equals(Integer.class)) {
                        int[] values = GetterUtil.getIntegerValues(field.getValues());
                        if (values.length > 1) {
                            xContentBuilder.field(name, values);
                        } else {
                            xContentBuilder.field(name,values[0]);
                        }
                    } else {
                        long[] values = GetterUtil.getLongValues(field.getValues());
                        if (values.length > 1) {
                            xContentBuilder.field(name, values);
                        } else {
                            xContentBuilder.field(name,values[0]);
                        }
                    }
                } else {

                    if (field.getValues().length > 1) {
                        xContentBuilder.field(name, field.getValues());
                    } else {
                        xContentBuilder.field(name, field.getValue());
                    }
                }
            } else {
                Map<Locale, String> localizedValues =
                        field.getLocalizedValues();

                for (Map.Entry<Locale, String> entry :
                        localizedValues.entrySet()) {

                    String value = entry.getValue();

                    if (Validator.isNull(value)) {
                        continue;
                    }

                    Locale locale = entry.getKey();

                    String languageId = LocaleUtil.toLanguageId(locale);

                    String defaultLanguageId = LocaleUtil.toLanguageId(
                            LocaleUtil.getDefault());

                    if (languageId.equals(defaultLanguageId)) {
                        xContentBuilder.field(name, value.trim());
                    }

                    String localizedName = DocumentImpl.getLocalizedName(LocaleUtil.fromLanguageId(languageId), name);

                    xContentBuilder.field(localizedName, value.trim());
                }
            }
        }

        xContentBuilder.endObject();

        return xContentBuilder.string();
    }

    private IndexRequestBuilder getUpdateRequestBuilder(
            SearchContext searchContext, Document document)
            throws IOException {

        Client client = getClient();
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(
                String.valueOf("liferay_" + searchContext.getCompanyId()), "documents").setId(document.getUID());


        /*UpdateRequestBuilder updateRequestBuilder = client.prepareUpdate(
                String.valueOf("liferay_" + searchContext.getCompanyId()), "documents",
                document.getUID());
*/
        String elasticSearchDocument = getElasticsearchDocument(document);

        indexRequestBuilder.setSource(elasticSearchDocument);
        //indexRequestBuilder.setDocAsUpsert(true);

        return indexRequestBuilder;
    }



}