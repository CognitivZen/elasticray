/*
 * @author ajaykottapally
 *
 * $Id$
 */
package com.liferay.portal.search.elastic.facet;

import com.liferay.portal.kernel.search.facet.collector.TermCollector;

/**
 * //TODO Comment goes here
 */
public class ElasticTermCollector implements TermCollector {

    public static final String svnRevision = "$Id$";
    private final int _frequency;
    private final String _term;

    public ElasticTermCollector(int frequency, String term) {
        _frequency = frequency;
        _term = term;
    }

    @Override
    public int getFrequency() {
        return _frequency;
    }

    @Override
    public String getTerm() {
        return _term;
    }
}
