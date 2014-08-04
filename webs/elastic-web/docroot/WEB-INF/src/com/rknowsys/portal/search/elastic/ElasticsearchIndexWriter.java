/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.rknowsys.portal.search.elastic;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
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

/**
 * @author Michael C. Han
 * @author Milen Dyankov
 */
public class ElasticsearchIndexWriter implements IndexWriter {

    private ClientFactory clientFactory;

    @Override
    public void addDocument(SearchContext searchContext, Document document)
            throws SearchException {
        try {
            UpdateRequestBuilder updateRequestBuilder =
                    getUpdateRequestBuilder(
                            "LiferayDocuments", searchContext, document);

            Future<UpdateResponse> future = updateRequestBuilder.execute();

            UpdateResponse updateResponse = future.get();

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
                UpdateRequestBuilder updateRequestBuilder =
                        getUpdateRequestBuilder(
                                "LiferayDocuments", searchContext, document);

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
                    String.valueOf(searchContext.getCompanyId()),
                    "LiferayDocuments", uid);

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
                                String.valueOf(searchContext.getCompanyId()),
                                "LiferayDocuments", uid);

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
            UpdateRequestBuilder updateRequestBuilder =
                    getUpdateRequestBuilder(
                            "LiferayDocuments", searchContext, document);

            Future<UpdateResponse> future = updateRequestBuilder.execute();

            UpdateResponse updateResponse = future.get();

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
                UpdateRequestBuilder updateRequestBuilder =
                        getUpdateRequestBuilder(
                                "LiferayDocuments", searchContext, document);

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
                for (String value : field.getValues()) {
                    if (Validator.isNull(value)) {
                        continue;
                    }

                    xContentBuilder.field(name, value.trim());
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

    private UpdateRequestBuilder getUpdateRequestBuilder(
            String documentType, SearchContext searchContext, Document document)
            throws IOException {

        Client client = getClient();

        UpdateRequestBuilder updateRequestBuilder = client.prepareUpdate(
                String.valueOf(searchContext.getCompanyId()), documentType,
                document.getUID());

        String elasticSearchDocument = getElasticsearchDocument(document);

        updateRequestBuilder.setDoc(elasticSearchDocument);
        updateRequestBuilder.setDocAsUpsert(true);

        return updateRequestBuilder;
    }


}