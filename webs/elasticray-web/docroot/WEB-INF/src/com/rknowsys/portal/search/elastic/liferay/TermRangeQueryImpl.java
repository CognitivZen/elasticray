/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import com.liferay.portal.kernel.search.TermRangeQuery;

/**
 * //TODO Comment goes here
 */
public class TermRangeQueryImpl extends QueryImpl implements TermRangeQuery {

    public static final String svnRevision = "$Id$";

    public TermRangeQueryImpl(String field, String lowerTerm, String upperTerm, boolean includesLower, boolean includesUpper) {
        _field = field;
        _lowerTerm = lowerTerm;
        _upperTerm = upperTerm;
        _includesLower = includesLower;
        _includesUpper = includesUpper;
        _rangeQueryBuilder = QueryBuilders.rangeQuery(field);
        if (includesLower) {
            _rangeQueryBuilder = _rangeQueryBuilder.gte(lowerTerm.toLowerCase());
        } else {
            _rangeQueryBuilder = _rangeQueryBuilder.gt(lowerTerm.toLowerCase());
        }
        if (includesUpper) {
            _rangeQueryBuilder = _rangeQueryBuilder.gte(upperTerm.toLowerCase());
        } else {
            _rangeQueryBuilder = _rangeQueryBuilder.gt(upperTerm.toLowerCase());
        }
    }

    @Override
    public String getField() {
        return _field;
    }

    @Override
    public String getLowerTerm() {
        return _lowerTerm;
    }

    @Override
    public String getUpperTerm() {
        return _upperTerm;
    }

    @Override
    public boolean includesLower() {
        return _includesLower;
    }

    @Override
    public boolean includesUpper() {
        return _includesUpper;
    }


    @Override
    public Object getWrappedQuery() {
        return _rangeQueryBuilder;
    }


    private String            _field;
    private String            _lowerTerm;
    private String            _upperTerm;
    private boolean           _includesLower;
    private boolean           _includesUpper;
    private RangeQueryBuilder _rangeQueryBuilder;

}
