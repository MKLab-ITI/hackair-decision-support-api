/*
 * Copyright 2018 mriga.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 * @author Marina Riga (mriga@iti.gr)
 *
 */

package utils;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

public class OntologyUtils {


    public static final String ABox_SOURCE = "http://160.40.51.22/mklab_ontologies/hackair/ontologies/hackairABox_API";
    public static final String TBox_SOURCE = "http://160.40.51.22/mklab_ontologies/hackair/ontologies/hackairTBox_API";
    public static final String SPIN_SOURCE = "http://160.40.51.22/mklab_ontologies/hackair/ontologies/hackairSPIN_API";

    public static final String ABox_NS = ABox_SOURCE + "#";
    public static final String TBox_NS = TBox_SOURCE + "#";
    public static final String SPIN_NS = SPIN_SOURCE + "#";

    public static final String hackAIRSPIN_URI_ttl = "http://160.40.51.22/mklab_ontologies/hackair/ontologies/hackairSPIN_API.ttl";

    // Other useful prefixes
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

    public static ResultSet executeJenaSPARQLQuery(String queryString, OntModel ont) {

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ont);
        ResultSet results = qexec.execSelect();

        return results;

    }

    // ************************************************************************ //
    /**
     * ******* Specific Queries with defined SPARQL SELECT strings ********
     */
    // ************************************************************************ //
    public static ResultSet getAllRecommendationsPerUserFromModel(OntModel ont) {

        // Create a new query passing a String containing the SPARQL to execute
        String queryString
                = "PREFIX hackairTBox: <" + TBox_NS + "> "
                + "PREFIX rdf: <" + RDF_NS + ">"
                + "SELECT ?person ?recommendation ?rec_type ?rec_desc ?rec_identifier WHERE { "
                + "?person hackairTBox:isProvidedWithRecommendation ?recommendation . "
                + "?recommendation rdf:type ?rec_type . "
                + "?recommendation hackairTBox:hasDescription ?rec_desc . "
                + "?recommendation hackairTBox:hasDescriptionIdentifier ?rec_identifier . "
                + // tips of the day also have identifiers
                "} "
                + "ORDER BY ?person";

        ResultSet results = executeJenaSPARQLQuery(queryString, ont);

        return results;
    }

    public static ResultSet getRecIdentifierForSpecificRecommendationOfUser(OntModel ont, String person, String recommendationText) { //not used. outdated. 

        String queryString
                = "PREFIX hackairTBox: <" + TBox_NS + "> "
                + "PREFIX rdf: <" + RDF_NS + ">"
                + "SELECT ?tip ?tip_identifier WHERE { "
                + "<" + person + "> hackairTBox:isProvidedWithRecommendation ?tip . "
                + "?tip a hackairTBox:LimitExposureRecommendation . "
                + "?tip hackairTBox:hasDescriptionIdentifier ?tip_identifier . "
                + "?tip hackairTBox:hasDescription ?description . "
                + "FILTER (regex(str(?description), \"" + recommendationText + "\"))"
                + "} ";

        ResultSet results = executeJenaSPARQLQuery(queryString, ont);

        return results;
    }

}
