/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.liferay;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.util.Version;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * //TODO Comment goes here
 */
public class BooleanQueryImpl extends QueryImpl implements BooleanQuery {

    public static final String svnRevision = "$Id$";

    public BooleanQueryImpl(WildcardSearchFields wildcardSearchFields) {
        _boolQueryBuilder = QueryBuilders.boolQuery();
        _wildcardSearchFields = wildcardSearchFields;
    }

    @Override
    public void add(Query query, BooleanClauseOccur booleanClauseOccur) throws ParseException {
        add(query, booleanClauseOccur.getName());
    }

    @Override
    public void add(Query query, String occur) throws ParseException {
        if (occur.equals(BooleanClauseOccur.MUST.getName())) {
            _boolQueryBuilder = _boolQueryBuilder.must(QueryTranslatorUtil.translate(query));
        } else if (occur.equals(BooleanClauseOccur.MUST_NOT.getName())) {
            _boolQueryBuilder = _boolQueryBuilder.mustNot(QueryTranslatorUtil.translate(query));
        } else if (occur.equals(BooleanClauseOccur.SHOULD.getName())) {
            _boolQueryBuilder = _boolQueryBuilder.should(QueryTranslatorUtil.translate(query));
        }
    }

    @Override
    public void addExactTerm(String field, boolean value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, Boolean value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, double value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, Double value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, int value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, Integer value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, long value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, Long value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, short value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, Short value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addExactTerm(String field, String value) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addNumericRangeTerm(String field, int startValue, int endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(startValue).to(endValue));
    }

    @Override
    public void addNumericRangeTerm(String field, Integer startValue, Integer endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(startValue).to(endValue));
    }

    @Override
    public void addNumericRangeTerm(String field, long startValue, long endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(startValue).to(endValue));
    }

    @Override
    public void addNumericRangeTerm(String field, Long startValue, Long endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(startValue).to(endValue));
    }

    @Override
    public void addNumericRangeTerm(String field, short startValue, short endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(startValue).to(endValue));
    }

    @Override
    public void addNumericRangeTerm(String field, Short startValue, Short endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(startValue).to(endValue));
    }

    @Override
    public void addRangeTerm(String field, int startValue, int endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(Integer.toString(startValue)).to(Integer.toString(endValue)));
    }

    @Override
    public void addRangeTerm(String field, Integer startValue, Integer endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(Integer.toString(startValue)).to(Integer.toString(endValue)));
    }

    @Override
    public void addRangeTerm(String field, long startValue, long endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(Long.toString(startValue)).to(Long.toString(endValue)));
    }

    @Override
    public void addRangeTerm(String field, Long startValue, Long endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(Long.toString(startValue)).to(Long.toString(endValue)));
    }

    @Override
    public void addRangeTerm(String field, short startValue, short endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(Short.toString(startValue)).to(Short.toString(endValue)));
    }

    @Override
    public void addRangeTerm(String field, Short startValue, Short endValue) {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.rangeQuery(field).from(Short.toString(startValue)).to(Short.toString(endValue)));
    }

    @Override
    public void addRangeTerm(String field, String startValue, String endValue) {
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
        if (!startValue.equals("*")) {
            rangeQueryBuilder = rangeQueryBuilder.from(startValue);
        }
        if (!endValue.equals("*")) {
            rangeQueryBuilder = rangeQueryBuilder.to(endValue);
        }
        _boolQueryBuilder = _boolQueryBuilder.should(rangeQueryBuilder);
    }

    @Override
    public void addRequiredTerm(String field, boolean value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, Boolean value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, double value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, Double value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, int value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, Integer value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, long value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, Long value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, short value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, Short value) {
        _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addRequiredTerm(String field, String value) {
        try {
            addTerm(field, value, false, BooleanClauseOccur.MUST);
        } catch (Exception e) {
        }

    }

    @Override
    public void addRequiredTerm(String field, String value, boolean like) {
        try {
            addTerm(field, value, like, BooleanClauseOccur.MUST);
        } catch (Exception e) {
        }
    }

    @Override
    public void addTerm(String field, long value) throws ParseException {
        _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
    }

    @Override
    public void addTerm(String field, String value) throws ParseException {
        addTerm(field, value, false);
    }

    @Override
    public void addTerm(String field, String value, boolean like) throws ParseException {
        addTerm(field, value, like, BooleanClauseOccur.SHOULD);
    }

    @Override
    public void addTerm(String field, String value, boolean like, BooleanClauseOccur booleanClauseOccur) throws ParseException {
        if (_wildcardSearchFields.isWildcardField(field)) {
            like = true;
        }
        Analyzer analyzer = ANALYZER_MAP.get(field);
        if (analyzer == null) {
            analyzer = new StandardAnalyzer();
            ANALYZER_MAP.put(field,analyzer);
        }
        QueryParser queryParser = new QueryParser(field,analyzer);

        value = value.toLowerCase();
        if (like) {
            value = StringUtil.replace(value, StringPool.PERCENT, StringPool.BLANK);
            if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
                _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.wildcardQuery(field, "*" + value + "*"));
            } else if (booleanClauseOccur.equals(BooleanClauseOccur.MUST_NOT)) {
                _boolQueryBuilder = _boolQueryBuilder.mustNot(QueryBuilders.wildcardQuery(field, "*" + value + "*"));
            } else if (booleanClauseOccur.equals(BooleanClauseOccur.SHOULD)) {
                _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.wildcardQuery(field, "*" + value + "*"));
            }

        } else {
            if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
                _boolQueryBuilder = _boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
            } else if (booleanClauseOccur.equals(BooleanClauseOccur.MUST_NOT)) {
                _boolQueryBuilder = _boolQueryBuilder.mustNot(QueryBuilders.termQuery(field, value));
            } else if (booleanClauseOccur.equals(BooleanClauseOccur.SHOULD)) {
                _boolQueryBuilder = _boolQueryBuilder.should(QueryBuilders.termQuery(field, value));
            }
        }
    }

    @Override
    public void addTerms(String[] fields, String values) throws ParseException {
        for (String field : fields) {
            addTerm(field, values);
        }
    }

    @Override
    public void addTerms(String[] fields, String value, boolean like) throws ParseException {
        for (String field : fields) {
            addTerm(field, value, like);
        }
    }

    @Override
    public List<BooleanClause> clauses() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasClauses() {
        return _boolQueryBuilder.hasClauses();
    }


    @Override
    public Object getWrappedQuery() {
        return _boolQueryBuilder;
    }


    private BoolQueryBuilder     _boolQueryBuilder;
    private WildcardSearchFields _wildcardSearchFields;
    private static final Map<String, Analyzer> ANALYZER_MAP = new ConcurrentHashMap<String, Analyzer>();
}
