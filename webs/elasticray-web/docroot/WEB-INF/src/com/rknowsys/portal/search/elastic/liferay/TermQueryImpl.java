/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.TermQuery;

/**
 * //TODO Comment goes here
 */
public class TermQueryImpl extends QueryImpl implements TermQuery {

    public static final String svnRevision = "$Id$";


    public TermQueryImpl(String field, String value) {
        _queryTerm = new QueryTermImpl(field, value);
        _termQueryBuilder = QueryBuilders.termQuery(field, value.toLowerCase());
    }

    public TermQueryImpl(String field, long value) {
        _queryTerm = new QueryTermImpl(field, Long.toString(value));
        _termQueryBuilder = QueryBuilders.termQuery(field, value);
    }

    @Override
    public QueryTerm getQueryTerm() {
        return _queryTerm;
    }

    @Override
    public Object getWrappedQuery() {
        return _termQueryBuilder;
    }


    private QueryTerm        _queryTerm;
    private TermQueryBuilder _termQueryBuilder;
}
