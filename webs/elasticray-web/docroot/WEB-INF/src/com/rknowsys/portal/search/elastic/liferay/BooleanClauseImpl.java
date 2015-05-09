/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Query;

/**
 * //TODO Comment goes here
 */
public class BooleanClauseImpl implements BooleanClause {

    public static final String svnRevision = "$Id$";

    public BooleanClauseImpl(Query query, BooleanClauseOccur booleanClauseOccur) {
        _query = query;
        _booleanClauseOccur = booleanClauseOccur;
    }

    @Override
    public BooleanClauseOccur getBooleanClauseOccur() {
        return _booleanClauseOccur;
    }

    @Override
    public Query getQuery() {
        return _query;
    }

    private Query              _query;
    private BooleanClauseOccur _booleanClauseOccur;
}
