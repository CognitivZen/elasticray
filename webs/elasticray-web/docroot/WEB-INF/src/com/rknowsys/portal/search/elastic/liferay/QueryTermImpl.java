/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.QueryTerm;

/**
 * //TODO Comment goes here
 */
public class QueryTermImpl implements QueryTerm {

    public static final String svnRevision = "$Id$";

    public QueryTermImpl(String field, String value) {
        _field = field;
        _value = value;
    }

    @Override
    public String getField() {
        return _field;
    }

    @Override
    public String getValue() {
        return _value;
    }

    private String _field;
    private String _value;
}
