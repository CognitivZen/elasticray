
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