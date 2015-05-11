/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactory;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.TermQueryFactory;

/**
 * //TODO Comment goes here
 */
public class BooleanClauseFactoryImpl implements BooleanClauseFactory {

    public static final String svnRevision = "$Id$";

    @Override
    public BooleanClause create(SearchContext searchContext, Query query, String occur) {
        BooleanClauseOccur booleanClauseOccur = new BooleanClauseOccurImpl(occur);
        return new BooleanClauseImpl(query, booleanClauseOccur);
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
