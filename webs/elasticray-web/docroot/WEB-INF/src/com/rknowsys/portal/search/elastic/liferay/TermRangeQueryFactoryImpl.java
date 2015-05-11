/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.TermRangeQueryFactory;

/**
 * //TODO Comment goes here
 */
public class TermRangeQueryFactoryImpl implements TermRangeQueryFactory {

    public static final String svnRevision = "$Id$";

    @Override
    public TermRangeQuery create(String field, String lowerTerm, String upperTerm, boolean includesLower, boolean includesUpper) {
        return new TermRangeQueryImpl(field, lowerTerm, upperTerm, includesLower, includesUpper);
    }
}
