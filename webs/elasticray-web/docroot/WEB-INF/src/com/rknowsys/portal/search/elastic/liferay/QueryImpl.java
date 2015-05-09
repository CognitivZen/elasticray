/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;

/**
 * //TODO Comment goes here
 */
public abstract class QueryImpl implements Query {

    public static final String svnRevision = "$Id$";

    @Override
    public QueryConfig getQueryConfig() {
        if (_queryConfig == null) {
            _queryConfig = new QueryConfig();
        }

        return _queryConfig;
    }

    @Override
    public String toString() {
        return getWrappedQuery().toString();
    }


    @Override
    public void setQueryConfig(QueryConfig queryConfig) {
        _queryConfig = queryConfig;
    }

    private QueryConfig _queryConfig;
}
