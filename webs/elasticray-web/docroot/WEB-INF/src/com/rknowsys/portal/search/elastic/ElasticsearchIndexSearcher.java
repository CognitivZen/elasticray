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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.RangeFacet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.util.*;
import com.rknowsys.portal.search.elastic.client.ClientFactory;
import com.rknowsys.portal.search.elastic.facet.ElasticsearchFacetFieldCollector;
import com.rknowsys.portal.search.elastic.facet.LiferayFacetParser;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElasticsearchIndexSearcher implements IndexSearcher {

    private ClientFactory clientFactory;
    public static final int INDEX_FILTER_SEARCH_LIMIT = GetterUtil.getInteger(
            PropsUtil.get(PropsKeys.INDEX_FILTER_SEARCH_LIMIT));

    @Override
    public Hits search(SearchContext searchContext, Query query) throws SearchException {
        try {
            int end = searchContext.getEnd();
            int start = searchContext.getStart();
            if (isFilterSearch(searchContext)) {
                //return filterSearch(searchContext, query);
                end = end - INDEX_FILTER_SEARCH_LIMIT + 5;
                if ((start < 0) || (start > end) || end < 0) {
                    return new HitsImpl();
                }
            }
            query = getPermissionQuery(searchContext, query);

            return doSearch(searchContext, query, start, end);
        } catch (Exception e) {
            throw new SearchException(e);
        }
    }

    private Hits doSearch(SearchContext searchContext, Query query, int start, int end) {
        Client client = getClient();

        SearchRequest searchRequest = prepareSearchBuilder(searchContext, query, client, start, end).request();
        _log.debug("Search query String  " + searchRequest.toString());

        _log.debug("Time Before request to ES: " + System.currentTimeMillis());
        ActionFuture<SearchResponse> future = client.search(searchRequest);
        SearchResponse searchResponse = future.actionGet();
        _log.debug("Time After response from ES: " + System.currentTimeMillis());
        updateFacetCollectors(searchContext, searchResponse);
        Hits hits = processSearchHits(
                searchResponse, query.getQueryConfig());
        _log.debug("Total responseCount  " + searchResponse.getHits().getTotalHits());
        _log.debug("Time After processSearchHits: " + System.currentTimeMillis());

        hits.setQuery(query);

        TimeValue timeValue = searchResponse.getTook();

        hits.setSearchTime((float) timeValue.getSecondsFrac());
        return hits;
    }

    private Query getPermissionQuery(SearchContext searchContext, Query query) {
        if (searchContext.getEntryClassNames() == null) {
            return query;
        }
        for (String className : searchContext.getEntryClassNames()) {
            Indexer indexer = IndexerRegistryUtil.getIndexer(className);
            if (indexer != null) {
                if (indexer.isFilterSearch() && indexer.isPermissionAware()) {
                    SearchPermissionChecker searchPermissionChecker = SearchEngineUtil.getSearchPermissionChecker();
                    query = searchPermissionChecker.getPermissionQuery(searchContext.getCompanyId(), searchContext.getGroupIds(), searchContext.getUserId(), className, query, searchContext);
                }
            }
        }
        return query;
    }

    private SearchRequestBuilder prepareSearchBuilder(SearchContext searchContext, Query query, Client client, int start, int end) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("liferay_" +
                String.valueOf(searchContext.getCompanyId()));
        addHighlights(query, searchRequestBuilder);

        QueryBuilder queryBuilder = QueryBuilders.queryString(query.toString());

        searchRequestBuilder.setQuery(queryBuilder);

        _log.debug("Query String" + queryBuilder.toString());

        searchRequestBuilder.setTypes("documents");

        addFacetCollectorsToSearch(searchContext, searchRequestBuilder);

        addSortToSearch(searchContext.getSorts(), searchRequestBuilder);


        int size = end - start;

        _log.debug("Search Start:  " + start + " Search Size: " + size);

        searchRequestBuilder.setFrom(start).setSize(size);

        return searchRequestBuilder;
    }

    private void addHighlights(Query query, SearchRequestBuilder searchRequestBuilder) {
        QueryConfig queryConfig = query.getQueryConfig();
        if (queryConfig.isHighlightEnabled()) {

            String localizedContentName = DocumentImpl.getLocalizedName(
                    queryConfig.getLocale(), Field.CONTENT);

            String localizedTitleName = DocumentImpl.getLocalizedName(
                    queryConfig.getLocale(), Field.TITLE);

            int fragmentSize = queryConfig.getHighlightFragmentSize();
            int numberOfFragments = queryConfig.getHighlightSnippetSize();
            searchRequestBuilder.addHighlightedField(Field.CONTENT, fragmentSize, numberOfFragments);
            searchRequestBuilder.addHighlightedField(Field.TITLE, fragmentSize, numberOfFragments);
            searchRequestBuilder.addHighlightedField(localizedContentName, fragmentSize, numberOfFragments);
            searchRequestBuilder.addHighlightedField(localizedTitleName, fragmentSize, numberOfFragments);

        }
    }


    private boolean isFilterSearch(SearchContext searchContext) {
        if (searchContext.getEntryClassNames() == null) {
            return false;
        }

        for (String entryClassName : searchContext.getEntryClassNames()) {
            Indexer indexer = IndexerRegistryUtil.getIndexer(entryClassName);

            if (indexer == null) {
                continue;
            }

            if (indexer.isFilterSearch()) {
                return true;
            }
        }

        return false;
    }

	/*private Hits filterSearch(SearchContext searchContext, Query query) {

        int end = searchContext.getEnd();
        int start = searchContext.getStart();
        end = end - INDEX_FILTER_SEARCH_LIMIT + 5;
        if ((start < 0) || (start > end) || end < 0) {
			return new HitsImpl();
		}
        if(query instanceof BaseBooleanQueryImpl)
		{
			addPermissionFields(searchContext, query);
		}
		return doSearch(searchContext, query, start,end);
   }*/

	/*private void addPermissionFields(SearchContext searchContext, Query query)
    {
	    BaseBooleanQueryImpl baseQuery = (BaseBooleanQueryImpl)query;
	    BooleanQuery permissionQuery = BooleanQueryFactoryUtil.create(searchContext);

	    try
		{
		  long userId = searchContext.getUserId();
		  User user = UserLocalServiceUtil.getUser(userId);
		  long[] roleIds = user.getRoleIds();

		  StringBuilder strRoles = new StringBuilder();
		  for(int ii=0; ii< roleIds.length; ii++)
		  {
			  if(ii == roleIds.length - 1)
			  {
				  strRoles.append(roleIds[ii]) ;
			  }
			  else
			  {
				  strRoles.append(roleIds[ii] + " OR ");
			  }
		  }

		  if(strRoles.toString().length() > 0)
		  {
			  permissionQuery.addTerm("roleId", strRoles.toString());
		  }

		  StringBuilder strGrps= new StringBuilder();

	      List<UserGroupRole> userGroupRoles = UserGroupRoleLocalServiceUtil.getUserGroupRoles(userId);
          int ll =0;
          int grpSize = userGroupRoles.size();
          for (UserGroupRole userGroupRole : userGroupRoles) {
	    	  long groupId = userGroupRole.getGroupId();
	    	  long roleId = userGroupRole.getRoleId();
			  if(ll == grpSize -1 )
			  {
			    strGrps.append(groupId + StringPool.DASH + roleId);
			  }
			  else
			  {
				  strGrps.append(groupId + StringPool.DASH + roleId + " OR ");
			  }
			  ll++;
	      }

		  List<UserGroup> userGroupList = UserGroupLocalServiceUtil.getUserUserGroups(userId);
		  for(int jj=0; jj< userGroupList.size(); jj++)
		  {
			  UserGroup userGroup = userGroupList.get(jj);
			  long groupId= userGroup.getGroupId();

			  List<UserGroupGroupRole> userGroupGroupRoles = UserGroupGroupRoleLocalServiceUtil.getUserGroupGroupRoles(userGroup.getUserGroupId());
			  grpSize = userGroupGroupRoles.size();
			  int kk=0;
			  for(UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles )
			  {
				  if(kk == grpSize -1 )
				  {
				    strGrps.append(groupId + StringPool.DASH + userGroupGroupRole.getRoleId());
				  }
				  else
				  {
					  strGrps.append(groupId + StringPool.DASH + userGroupGroupRole.getRoleId() + " OR ");
				  }
				  kk++;
			  }
		  }
		  if(strGrps.toString().length() > 0)
		  {
			  permissionQuery.addTerm("groupRoleId", strGrps.toString());
		  }

		  if(strRoles.toString().length() > 0 || strGrps.toString().length() > 0)
		  {
			  baseQuery.add(permissionQuery, BooleanClauseOccur.MUST);
		  }

		}
		catch(Exception excp)
		{
			_log.debug("Exception while adding permissions " + excp.getMessage());
		}
	}*/

    @Override
    public Hits search(String searchEngineId, long companyId, Query query, Sort[] sort, int start, int end) throws SearchException {

        try {
            Client client = getClient();

            SearchRequestBuilder searchRequestBuilder = client.prepareSearch("liferay_" +
                    String.valueOf(companyId));

            QueryBuilder queryBuilder = QueryBuilders.queryString(query.toString());

            _log.debug("Query String" + queryBuilder.toString());

            searchRequestBuilder.setQuery(queryBuilder);

            searchRequestBuilder.setTypes("documents");

            addSortToSearch(sort, searchRequestBuilder);


            _log.debug("Search Start:  " + start + " Search End: " + end);
            searchRequestBuilder.setFrom(start).setSize(end - start);

            SearchRequest searchRequest = searchRequestBuilder.request();

            _log.debug("Search query String  " + searchRequest.toString());


            ActionFuture<SearchResponse> future = client.search(searchRequest);

            SearchResponse searchResponse = future.actionGet();


            Hits hits = processSearchHits(
                    searchResponse, query.getQueryConfig());

            hits.setQuery(query);

            TimeValue timeValue = searchResponse.getTook();

            hits.setSearchTime((float) timeValue.getSecondsFrac());
            return hits;
        } catch (Exception e) {
            throw new SearchException(e);
        }
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

            Object val = source.get(fieldName);
            if (val == null) {
                Field field = new Field(fieldName, (String) null);
                document.add(field);
            } else if (val instanceof List) {
                String[] values = ((List<String>) val).toArray(new String[((List<String>) val).size()]);
                Field field = new Field(fieldName, values);
                document.add(field);
            } else {
                Field field = new Field(fieldName, new String[]{val.toString()});
                document.add(field);
            }
        }

        return document;
    }

    protected Hits processSearchHits(
            SearchResponse searchResponse, QueryConfig queryConfig) {

        Hits hits = new HitsImpl();
        List<Document> documents = new ArrayList<Document>();
        Set<String> queryTerms = new HashSet<String>();
        List<Float> scores = new ArrayList<Float>();
        List<String> snippets = new ArrayList<String>();
        SearchHits searchHits = searchResponse.getHits();

        if (searchHits.totalHits() > 0) {
            SearchHit[] searchHitsArray = searchHits.getHits();

            for (SearchHit searchHit : searchHitsArray) {
                Document document = processSearchHit(searchHit);
                documents.add(document);
                scores.add(searchHit.getScore());

                String snippet = StringPool.BLANK;

                if (queryConfig.isHighlightEnabled()) {
                    snippet = getSnippet(
                            searchHit, queryConfig, queryTerms,
                            searchHit.highlightFields(), Field.CONTENT);

                    if (Validator.isNull(snippet)) {
                        snippet = getSnippet(
                                searchHit, queryConfig, queryTerms,
                                searchHit.highlightFields(), Field.TITLE);
                    }

                    if (Validator.isNotNull(snippet)) {
                        snippets.add(snippet);
                    }
                }

            }
        }
        int totalHits = (int) searchHits.getTotalHits();
        _log.debug("Total Hits: " + totalHits);
        _log.debug("Total Documents size: " + documents.size());
        hits.setDocs(documents.toArray(new Document[documents.size()]));
        hits.setLength(totalHits);
        hits.setQueryTerms(queryTerms.toArray(new String[queryTerms.size()]));
        hits.setScores(scores.toArray(new Float[scores.size()]));
        hits.setSnippets(snippets.toArray(new String[snippets.size()]));

        return hits;
    }

    protected String getSnippet(
            SearchHit searchHit, QueryConfig queryConfig,
            Set<String> queryTerms,
            Map<String, HighlightField> highlights, String field) {

        if (highlights == null) {
            return StringPool.BLANK;
        }

        boolean localizedSearch = true;

        String defaultLanguageId = LocaleUtil.toLanguageId(
                LocaleUtil.getDefault());
        String queryLanguageId = LocaleUtil.toLanguageId(
                queryConfig.getLocale());

        if (defaultLanguageId.equals(queryLanguageId)) {
            localizedSearch = false;
        }

        if (localizedSearch) {
            String localizedName = DocumentImpl.getLocalizedName(
                    queryConfig.getLocale(), field);

            if (searchHit.fields().containsKey(localizedName)) {
                field = localizedName;
            }
        }
        HighlightField hField = highlights.get(field);
        if (hField == null) {
            return StringPool.BLANK;
        }

        List<String> snippets = new ArrayList<String>();
        Text[] txtArr = hField.getFragments();
        if (txtArr == null) {
            return StringPool.BLANK;
        }
        for (Text txt : txtArr) {
            snippets.add(txt.string());
        }

        String snippet = StringUtil.merge(snippets, "...");

        if (Validator.isNotNull(snippet)) {
            snippet = snippet + "...";
        } else {
            snippet = StringPool.BLANK;
        }

        Pattern pattern = Pattern.compile("<em>(.*?)</em>");

        Matcher matcher = pattern.matcher(snippet);

        while (matcher.find()) {
            queryTerms.add(matcher.group(1));
        }

        snippet = StringUtil.replace(snippet, "<em>", "");
        snippet = StringUtil.replace(snippet, "</em>", "");

        return snippet;
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

    private void addFacetCollectorsToSearch(SearchContext searchContext, SearchRequestBuilder searchRequestBuilder) {
        Map<String, Facet> facets = searchContext.getFacets();
        for (Facet facet : facets.values()) {
            FacetBuilder facetBuilder = null;
            if (facet instanceof MultiValueFacet) {
                facetBuilder = LiferayFacetParser.getFacetBuilder((MultiValueFacet) facet);
            } else if (facet instanceof RangeFacet) {
                facetBuilder = LiferayFacetParser.getFacetBuilder((RangeFacet) facet);
            }
            if (facetBuilder != null) {
                searchRequestBuilder.addFacet(facetBuilder);
            }
        }

    }

    private void addSortToSearch(Sort[] sorts, SearchRequestBuilder searchRequestBuilder) {
        if (sorts == null) {
            return;
        }
        for (Sort sort : sorts) {
            SortBuilder sortBuilder = null;
            if (sort.getType() == Sort.SCORE_TYPE) {
                sortBuilder = SortBuilders.scoreSort();
            } else {
                sortBuilder = SortBuilders.fieldSort(sort.getFieldName() + "_sortable").ignoreUnmapped(true)
                        .order(sort.isReverse() ? SortOrder.DESC : SortOrder.ASC);
            }
            searchRequestBuilder.addSort(sortBuilder);

        }
    }

    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    private Client getClient() {
        return clientFactory.getClient();
    }

    private static final Log _log = LogFactoryUtil.getLog(ElasticsearchIndexSearcher.class);


}