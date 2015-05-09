package com.rknowsys.portal.search.elastic.util;/*
 * @author ajaykottapally
 *
 * \$Id$
 */

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

public class Utilities {

    private static final String ES_INDEX_NAME_PREFIX = "liferay_" + GetterUtil.getString(
            PropsUtil.get("elasticsearch.index.name"), "elasticray");

    public static String getIndexName(long companyId) {
        return ES_INDEX_NAME_PREFIX + "_" + companyId;
    }

    public static String getIndexName(SearchContext searchContext) {
        return ES_INDEX_NAME_PREFIX + "_" + searchContext.getCompanyId();
    }
}
