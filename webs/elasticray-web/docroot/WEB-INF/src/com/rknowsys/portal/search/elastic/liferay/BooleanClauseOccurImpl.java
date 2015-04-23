/*
 * @author ajaykottapally
 *
 * $Id$
 */
package com.rknowsys.portal.search.elastic.liferay;

import com.liferay.portal.kernel.search.BooleanClauseOccur;

/**
 * //TODO Comment goes here
 */
public class BooleanClauseOccurImpl implements BooleanClauseOccur {

    public static final String svnRevision = "$Id$";

    public BooleanClauseOccurImpl(String name) {
        _name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BooleanClauseOccur)) {
            return false;
        }

        BooleanClauseOccur booleanClauseOccur = (BooleanClauseOccur)obj;

        String name = booleanClauseOccur.getName();

        if (_name.equals(name)) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String getName() {
        return _name;
    }

    private String _name;
}
