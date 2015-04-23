/*
 * @author ajaykottapally
 *
 * $Id$
 */
package com.rknowsys.portal.search.elastic.liferay;

import java.util.regex.Pattern;

/**
 * //TODO Comment goes here
 */
public class WildcardSearchFields {

    public static final String svnRevision = "$Id$";

    public WildcardSearchFields(String[] fieldPatterns) {
        _fieldPatterns = fieldPatterns.clone();
    }

    public boolean isWildcardField(String fieldName) {
        if (_fieldPatterns !=null) {
            for (String pattern : _fieldPatterns) {
                if (Pattern.matches(pattern, fieldName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String[] _fieldPatterns;
}
