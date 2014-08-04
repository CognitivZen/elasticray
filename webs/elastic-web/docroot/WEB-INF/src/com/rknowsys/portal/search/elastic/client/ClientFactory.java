/*
 * @author ajaykottapally
 *
 * $Id$
 */

package com.rknowsys.portal.search.elastic.client;

import org.elasticsearch.client.Client;

/**
 * //TODO Comment goes here
 */
public interface ClientFactory {

    public static final String svnRevision = "$Id$";

    Client getClient();
}
