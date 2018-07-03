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

package services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import main_objects.Recommendation;
import main_objects.UserProfile;
import ontology.MyModel;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.OntologyUtils;
import static utils.OntologyUtils.hackAIRSPIN_URI_ttl;

@Path("/") 
public class Services {

    @Context
    private HttpServletRequest request;

    @GET
    public String printStatus() {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yyyy");
        Date date = new Date();

        String msg = "The service is up and running...";
        System.out.println(">>>>> CALL API: Status ok. \t User: " + request.getRemoteAddr() + "\t Time: " + dateFormat.format(date) + " <<<<<");

        return msg;
    }

    
    @POST
    @Path("/requestRecommendation")
    @Consumes("application/json")
    @Produces("application/json")
    public synchronized Response requestRecommendationForUserProfile(String userJSON) throws ParseException, IOException, InterruptedException {

        File homedir = new File(System.getProperty("user.home"));
        File lock = new File(homedir, "hackair.lock");

        while (lock.exists()) {
            Thread.currentThread().sleep(500);
        }

        lock.createNewFile();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yyyy");
        Date date = new Date();
        System.out.println(">>>>> userJSON: " + userJSON.toString());
        System.out.println(">>>>> Call API: requestRecommendation. \t User: " + request.getRemoteAddr() + "\t Time: " + dateFormat.format(date) + " <<<<<");

        JSONObject userJSONObject = null;
        try {
            userJSONObject = (JSONObject) new JSONParser().parse(userJSON);
        } catch (ParseException ex) {
            JSONObject main_JSON = new JSONObject();
            main_JSON.put("error", "JSON syntax error");

            return Response.status(Response.Status.BAD_REQUEST).entity(main_JSON.toString()).build();
        }

        UserProfile userProfile = UserProfile.createUserWithRelatedProfilesFromJSONobject(userJSONObject);
        // Print users as String with all values concatenated
        System.out.println(userProfile.toString());

        /* Handle ontology related processes */
        date.setTime(System.currentTimeMillis());
        System.out.println("Loading model.. " + date);
        MyModel mmyModel = new MyModel(hackAIRSPIN_URI_ttl);
        date.setTime(System.currentTimeMillis());
        System.out.println("Model loaded.. " + date);
        date.setTime(System.currentTimeMillis());
        System.out.println("Enriching model.. " + date);
        // Enrich ontology with user profile
        mmyModel.enrichOntologyWithFullUserProfile(userProfile);
        date.setTime(System.currentTimeMillis());
        System.out.println("Model enriched.. " + date);
//            MyModel.printOutAllStatements(myModel.getOntology());
        // Run inferencing for the enriched ontology
        date.setTime(System.currentTimeMillis());
        System.out.println("Starting inferencing.. " + date);
        mmyModel.runInference();
        date.setTime(System.currentTimeMillis());
        System.out.println("Inferencing done.. " + date);
        // Print all statements
//            MyModel.printOutAllStatements(myModel.getOntology());

        /* Handle SPARQL queries and related results */
        // Query for all recommendations that exist in the ontology            
        ResultSet results = OntologyUtils.getAllRecommendationsPerUserFromModel(mmyModel.getOntology());

        HashMap<String, ArrayList<Recommendation>> recommendation_results = new HashMap<String, ArrayList<Recommendation>>(); // key: username, value: list of recommendations

        String username;

        while (results.hasNext()) {
            QuerySolution binding = results.next();

            username = binding.get("person").toString();
            ArrayList<Recommendation> tmp_list;

            // if username is already included in the HashMap then add new Recommendation to existing list
            if (recommendation_results.containsKey(username)) {
                tmp_list = recommendation_results.get(username);
            } // else create new Recommendation list and add new pair to HashMap
            else {
                tmp_list = new ArrayList<Recommendation>();
            }
            String tmp_msg = binding.get("rec_desc").toString();
            String tmp_msg_id = binding.get("rec_identifier").toString();
            tmp_list.add(new Recommendation(tmp_msg.substring(0, tmp_msg.length() - 3), binding.get("rec_type").toString(), tmp_msg_id)); // removes last 3 characters (@lc where lc is language code)
            recommendation_results.put(username, tmp_list);

        }

        JSONObject main_JSON = Recommendation.convertHashMapToJSONRecommendations(recommendation_results, userProfile, mmyModel);         // note: true false should be for any user, so it should be added in the user profile

        lock.delete();

        if (main_JSON.isEmpty()) {
            main_JSON.put("warning", "No content found");

            return Response.status(Response.Status.NO_CONTENT).entity(main_JSON.toString()).build();

        } else {
            return Response.status(Response.Status.OK).entity(main_JSON.toString()).build();
        }

    }

    // ---------------------------------------------------------------------------------------------------------------- //
    @POST
    @Path("/dynamicPopulation")
    @Consumes("application/json")
    public synchronized Response dynamicPopulationOfUserData(String userJSON) throws ParseException, FileNotFoundException {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yyyy");
        Date date = new Date();
        System.out.println(">>>>> CALL API: dynamicPopulation. \t User: " + request.getRemoteAddr() + "\t Time: " + dateFormat.format(date) + " <<<<<");

        JSONObject main_JSON = new JSONObject();

        try {
            JSONObject userJSONObject = (JSONObject) new JSONParser().parse(userJSON);

            UserProfile userProfile = UserProfile.createUserWithRelatedProfilesFromJSONobject(userJSONObject);

            /* Handle ontology related processes */
            MyModel myModel = new MyModel(hackAIRSPIN_URI_ttl);
            // Enrich ontology with user profile
            myModel.enrichOntologyWithFullUserProfile(userProfile);

        } catch (ParseException ex) {
            main_JSON.put("error", "JSON syntax error");
            return Response.status(Response.Status.BAD_REQUEST).entity(main_JSON.toString()).build();
        }

        main_JSON.put("ok", "Data are sucessfully populated in the ontology");

        return Response.status(Response.Status.CREATED).entity(main_JSON.toString()).build();

    }

}
