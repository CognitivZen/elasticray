/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactory;

/**
 * //TODO Comment goes here
 */
public class BooleanQueryFactoryImpl implements BooleanQueryFactory {

    public static final String svnRevision = "$Id$";

    @Override
    public BooleanQuery create() {
        return new BooleanQueryImpl(wildcardSearchFields);
    }

    public void setWildcardSearchFields(WildcardSearchFields wildcardSearchFields) {
        this.wildcardSearchFields = wildcardSearchFields;
    }

    private WildcardSearchFields wildcardSearchFields = new WildcardSearchFields(new String[]{});
}
