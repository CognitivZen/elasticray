/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermQueryFactory;

/**
 * //TODO Comment goes here
 */
public class TermQueryFactoryImpl implements TermQueryFactory {

    public static final String svnRevision = "$Id$";

    @Override
    public TermQuery create(String field, long value) {
        return new TermQueryImpl(field, value);
    }

    @Override
    public TermQuery create(String field, String value) {
        return new TermQueryImpl(field, value);
    }
}
