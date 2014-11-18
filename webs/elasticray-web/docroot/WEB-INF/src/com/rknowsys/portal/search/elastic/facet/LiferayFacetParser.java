/*******************************************************************************
 * Copyright (c) 2014 R-Knowsys Technologies, http://www.rknowsys.com
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 *******************************************************************************/
/*
 * @author ajaykottapally
 *
 * $Id$
 */
package com.rknowsys.portal.search.elastic.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.RangeFacet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.util.*;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.filter.FilterFacetBuilder;
import org.elasticsearch.search.facet.range.RangeFacetBuilder;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

/**
 * //TODO Comment goes here
 */
public class LiferayFacetParser {

    private static final Log _log = LogFactoryUtil.getLog(LiferayFacetParser.class);

    public static FacetBuilder getFacetBuilder(RangeFacet rangeFacet) {
        FacetConfiguration facetConfiguration = rangeFacet.getFacetConfiguration();
        RangeFacetBuilder rangeFacetBuilder = FacetBuilders.rangeFacet(facetConfiguration.getFieldName());
        JSONObject facetData = facetConfiguration.getData();
        if (rangeFacet.isStatic()) {
            return null;
        }

        if (facetData.has("ranges")) {
            JSONArray rangesJSONArray = facetData.getJSONArray("ranges");
            if (rangesJSONArray != null) {
                rangeFacetBuilder.field(facetConfiguration.getFieldName());
                for ( int i =0; i < rangesJSONArray.length(); i++) {
                    JSONObject rangeJSONObject = rangesJSONArray.getJSONObject(i);
                    String rangeString = rangeJSONObject.getString("range");
                    String[] range = RangeParserUtil.parserRange(rangeString);
                    rangeFacetBuilder.addRange(range[0], range[1]);
                }
                return rangeFacetBuilder;
            } else {
                return null;
            }

        } else {
            return null;
        }


    }

    public static FacetBuilder getFacetBuilder(MultiValueFacet multiValueFacet) {
        FacetConfiguration facetConfiguration = multiValueFacet.getFacetConfiguration();
        JSONObject facetData = facetConfiguration.getData();
        String[] values = null;
        if (multiValueFacet.isStatic()) {
            return null;
        }
        TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet(facetConfiguration.getFieldName());
        if (facetData.has("maxTerms")) {
            termsFacetBuilder.size(facetData.getInt("maxTerms"));
        }
        termsFacetBuilder.field(facetConfiguration.getFieldName());
        return termsFacetBuilder;
    }
}
