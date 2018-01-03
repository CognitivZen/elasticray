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

package com.rknowsys.portal.search.elastic.facet;

import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

public class ElasticsearchFacetFieldCollector implements FacetCollector {

	public ElasticsearchFacetFieldCollector(Aggregation facet) {
		if (facet instanceof Range) {
			initialize((Range)facet);
		}
		else {
			initialize((Terms)facet);
		}
	}

	@Override
	public String getFieldName() {
		return _facet.getName();
	}

	@Override
	public TermCollector getTermCollector(String term) {
		long count = 0;

		if (_counts.containsKey(term)) {
			count = _counts.get(term);
		}

		return new ElasticTermCollector(count, term);
	}

	@Override
	public List<TermCollector> getTermCollectors() {
		if (_termCollectors != null) {
			return _termCollectors;
		}

		List<TermCollector> termCollectors = new ArrayList<TermCollector>();

		for (Map.Entry<String, Long> entry : _counts.entrySet()) {
			TermCollector termCollector = new ElasticTermCollector(
                entry.getValue(), entry.getKey());

			termCollectors.add(termCollector);
		}

		_termCollectors = termCollectors;

		return _termCollectors;
	}

	protected void initialize(Range rangeFacet) {
		_facet = rangeFacet;

		for (Range.Bucket entry : rangeFacet.getBuckets()) {
			StringBundler sb = new StringBundler();

			sb.append("{");
			sb.append(entry.getFromAsString());
			sb.append("-");
			sb.append(entry.getToAsString());
			sb.append("}");

			_counts.put(sb.toString(), entry.getDocCount());
		}
	}

	protected void initialize(Terms termsFacet) {
		_facet = termsFacet;

		for (Terms.Bucket entry : termsFacet.getBuckets()) {
			_counts.put(entry.getKeyAsString(), entry.getDocCount());
		}
	}


	private Map<String, Long> _counts =
		new ConcurrentHashMap<String, Long>();
	private Aggregation _facet;
	private List<TermCollector> _termCollectors;

}