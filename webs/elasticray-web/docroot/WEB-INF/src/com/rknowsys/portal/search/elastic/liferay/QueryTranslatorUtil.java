/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import org.elasticsearch.index.query.QueryBuilder;

import com.liferay.portal.kernel.search.Query;

/**
 * //TODO Comment goes here
 */
public class QueryTranslatorUtil {

    public static final String svnRevision = "$Id$";

    public static QueryBuilder translate(Query query) {
        if (query instanceof QueryImpl) {
            return (QueryBuilder) query.getWrappedQuery();
        } else {
            return null;
        }
    }
}
