/*
 * @author ajaykottapally
 *
 * $Id$
 */
package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.*;

/**
 * //TODO Comment goes here
 */
public class BooleanClauseFactoryImpl implements BooleanClauseFactory
{

    public static final String svnRevision = "$Id$";

    @Override
    public BooleanClause create(SearchContext searchContext, Query query, String occur) {
        BooleanClauseOccur booleanClauseOccur = new BooleanClauseOccurImpl(occur);
        return new BooleanClauseImpl(query,booleanClauseOccur);
    }

    @Override
    public BooleanClause create(SearchContext searchContext, String field, String value, String occur) {
        String searchEngineId = searchContext.getSearchEngineId();
        SearchEngine searchEngine = SearchEngineUtil.getSearchEngine(searchEngineId);
        TermQueryFactory termQueryFactory = searchEngine.getTermQueryFactory();
        Query query = termQueryFactory.create(field, value);
        BooleanClauseOccur booleanClauseOccur = new BooleanClauseOccurImpl(occur);
        return new BooleanClauseImpl(query, booleanClauseOccur);
    }
}
