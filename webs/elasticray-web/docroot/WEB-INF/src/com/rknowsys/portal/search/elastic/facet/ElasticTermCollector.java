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
