/*******************************************************************************
 * Copyright (c) 2014 R-Knowsys Technologies 
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

import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;

public class ElasticsearchFacetFieldCollector implements FacetCollector {

	public ElasticsearchFacetFieldCollector(Facet facet) {
		if (facet instanceof RangeFacet) {
			initialize((RangeFacet)facet);
		}
		else {
			initialize((TermsFacet)facet);
		}
	}

	@Override
	public String getFieldName() {
		return _facet.getName();
	}

	@Override
	public TermCollector getTermCollector(String term) {
		int count = 0;

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

		for (Map.Entry<String, Integer> entry : _counts.entrySet()) {
			TermCollector termCollector = new ElasticTermCollector(
                entry.getValue(), entry.getKey());

			termCollectors.add(termCollector);
		}

		_termCollectors = termCollectors;

		return _termCollectors;
	}

	protected void initialize(RangeFacet rangeFacet) {
		_facet = rangeFacet;

		for (RangeFacet.Entry entry : rangeFacet.getEntries()) {
			StringBundler sb = new StringBundler();

			sb.append("{");
			sb.append(entry.getFromAsString());
			sb.append("-");
			sb.append(entry.getToAsString());
			sb.append("}");

			_counts.put(sb.toString(), (int)entry.getCount());
		}
	}

	protected void initialize(TermsFacet termsFacet) {
		_facet = termsFacet;

		for (TermsFacet.Entry entry : termsFacet.getEntries()) {
			_counts.put(entry.getTerm().string(), entry.getCount());
		}
	}


	private Map<String, Integer> _counts =
		new ConcurrentHashMap<String, Integer>();
	private Facet _facet;
	private List<TermCollector> _termCollectors;

}