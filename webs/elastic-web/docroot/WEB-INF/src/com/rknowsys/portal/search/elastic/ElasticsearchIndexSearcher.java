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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facets;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.util.StringPool;
import com.rknowsys.portal.search.elastic.client.ClientFactory;
import com.rknowsys.portal.search.elastic.facet.ElasticsearchFacetFieldCollector;

/**
 * @author Michael C. Han
 * @author Milen Dyankov
 */
public class ElasticsearchIndexSearcher implements IndexSearcher {

    private ClientFactory clientFactory;

    @Override
    public Hits search(SearchContext searchContext, Query query) {
        Client client = getClient();

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(
                String.valueOf(searchContext.getCompanyId()));

        QueryBuilder queryBuilder = QueryBuilders.queryString(query.toString());

        searchRequestBuilder.setQuery(queryBuilder);

        searchRequestBuilder.setTypes("LiferayDocuments");

        SearchRequest searchRequest = searchRequestBuilder.request();

        ActionFuture<SearchResponse> future = client.search(searchRequest);

        SearchResponse searchResponse = future.actionGet();

        updateFacetCollectors(searchContext, searchResponse);

        Hits hits = processSearchHits(
                searchResponse.getHits(), query.getQueryConfig());

        hits.setQuery(query);

        TimeValue timeValue = searchResponse.getTook();

        hits.setSearchTime((float) timeValue.getSecondsFrac());
        return hits;
    }

    @Override
    public Hits search(String searchEngineId, long companyId, Query query, Sort[] sort, int start, int end) throws SearchException {
        Client client = getClient();

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(
                String.valueOf(companyId));

        QueryBuilder queryBuilder = QueryBuilders.queryString(query.toString());

        searchRequestBuilder.setQuery(queryBuilder);

        searchRequestBuilder.setTypes("LiferayDocuments");

        SearchRequest searchRequest = searchRequestBuilder.request();

        ActionFuture<SearchResponse> future = client.search(searchRequest);

        SearchResponse searchResponse = future.actionGet();


        Hits hits = processSearchHits(
                searchResponse.getHits(), query.getQueryConfig());

        hits.setQuery(query);

        TimeValue timeValue = searchResponse.getTook();

        hits.setSearchTime((float) timeValue.getSecondsFrac());
        return hits;
    }

    @Override
    public String spellCheckKeywords(SearchContext searchContext) {
        return StringPool.BLANK;
    }

    @Override
    public Map<String, List<String>> spellCheckKeywords(
            SearchContext searchContext, int max) {

        return Collections.emptyMap();
    }

    @Override
    public String[] suggestKeywordQueries(
            SearchContext searchContext, int max) {

        return new String[0];
    }


    protected Document processSearchHit(SearchHit hit) {
        Document document = new DocumentImpl();

        Map<String, Object> source = hit.getSource();

        for (String fieldName :
                source.keySet()) {

            String val = (String) source.get(fieldName);
            Field field = new Field(
                    fieldName,
                    new String[]{val}
            );

            document.add(field);

        }

        return document;
    }

    protected Hits processSearchHits(
            SearchHits searchHits, QueryConfig queryConfig) {

        Hits hits = new HitsImpl();

        List<Document> documents = new ArrayList<Document>();
        Set<String> queryTerms = new HashSet<String>();
        List<Float> scores = new ArrayList<Float>();

        if (searchHits.totalHits() > 0) {
            SearchHit[] searchHitsArray = searchHits.getHits();

            for (SearchHit searchHit : searchHitsArray) {
                Document document = processSearchHit(searchHit);
                documents.add(document);
                scores.add(searchHit.getScore());
            }
        }

        hits.setDocs(documents.toArray(new Document[documents.size()]));
        hits.setLength((int) searchHits.getTotalHits());
        hits.setQueryTerms(queryTerms.toArray(new String[queryTerms.size()]));
        hits.setScores(scores.toArray(new Float[scores.size()]));

        return hits;
    }

    protected void updateFacetCollectors(
            SearchContext searchContext, SearchResponse searchResponse) {

        Map<String, Facet> facetsMap = searchContext.getFacets();

        for (Facet facet : facetsMap.values()) {
            if (facet.isStatic()) {
                continue;
            }

            Facets facets = searchResponse.getFacets();

            org.elasticsearch.search.facet.Facet elasticsearchFacet =
                    facets.facet(facet.getFieldName());

            FacetCollector facetCollector =
                    new ElasticsearchFacetFieldCollector(elasticsearchFacet);

            facet.setFacetCollector(facetCollector);
        }
    }

    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    private Client getClient() {
        return clientFactory.getClient();
    }


}