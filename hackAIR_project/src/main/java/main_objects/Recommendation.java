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

package main_objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.OntologyUtils;
import static utils.OntologyUtils.ABox_NS;
import static utils.OntologyUtils.TBox_NS;
import ontology.MyModel;

public class Recommendation {

    private String recommendation_desc;
    private String recommendation_type;
    private String recommendation_desc_identifier;

    public Recommendation(String description, String type, String identifier) {
        this.recommendation_desc = description;
        this.recommendation_type = type;
        this.recommendation_desc_identifier = identifier;
    }

    public String getRecommendationDescription() {
        return this.recommendation_desc;
    }

    public String getRecommendationType() {
        return this.recommendation_type;
    }

    public String getRecommendationDescIdentifier() {
        return this.recommendation_desc_identifier;
    }

    public static JSONObject convertHashMapToJSONRecommendations(HashMap<String, ArrayList<Recommendation>> usersRecommendationsHashMap, UserProfile user, MyModel myModel) {
        // key: user (with related users included)
        // value: each recommendation inclided in a list

        JSONObject main_JSON = new JSONObject();
        JSONArray results_user_JSONArray = new JSONArray();

        // create HashMap with users and true/false values for direct/indirect users
        HashMap<String, Boolean> allRequestUsersHashMap = new HashMap<>();
        allRequestUsersHashMap.put(ABox_NS + user.getUsername(), user.getIsDirectUser()); // key: userURI, value: isDirectUser (true/false)

        HashSet<UserProfile> relatedProfiles = user.getRelatedProfiles();

        for (UserProfile relatedProfile : relatedProfiles) {
            allRequestUsersHashMap.put(ABox_NS + relatedProfile.getUsername(), relatedProfile.getIsDirectUser());
        }

        for (Map.Entry<String, ArrayList<Recommendation>> entry : usersRecommendationsHashMap.entrySet()) {

            JSONObject results_user_JSON = new JSONObject(); // new JSON object per user and his/her results

            String key = entry.getKey(); // key is the username
            String username_JSON = key.replace(ABox_NS, "");

            // fill in JSON with content
            results_user_JSON.put("username", username_JSON);
            if (allRequestUsersHashMap.containsKey(key)) {
                results_user_JSON.put("directUser", allRequestUsersHashMap.get(key));
            } else {
                results_user_JSON.put("directUser", null);
            }

            // initialisations
            ArrayList<Recommendation> recommendations = entry.getValue(); // value is the arraylist of recommendations per user
            HashMap<String, ArrayList<Recommendation>> recommendationsHashMap = new HashMap<>(); //key: type, value: arraylist of recommendations            
            JSONObject recommendations_per_user_JSON = new JSONObject();

            ArrayList<Recommendation> recommendationsOfSpecificType;

            //iterate over recommendations per user
            for (Recommendation recommendation : recommendations) {
                String rec_desc = recommendation.getRecommendationDescription();
                String rec_type = recommendation.getRecommendationType();
                String rec_identifier = recommendation.getRecommendationDescIdentifier();

                System.out.println("USER: " + username_JSON + "\t" + "TYPE: " + rec_type.replaceFirst("http://160.40.51.22/mklab_ontologies/hackair/ontologies/", "") + "\t"
                        + "ID: " + rec_identifier + "\t" + "DESC: " + rec_desc);

                if (recommendationsHashMap.containsKey(rec_type)) {
                    // already exists -> extend and submit again
                    recommendationsOfSpecificType = recommendationsHashMap.get(rec_type);
                } else {
                    // does not exist -> create and submit
                    recommendationsOfSpecificType = new ArrayList<>();
                }
                recommendationsOfSpecificType.add(recommendation);
                recommendationsHashMap.put(rec_type, recommendationsOfSpecificType);

            }

            // iterate over hashmap of type and recommendation(s)
            for (Map.Entry<String, ArrayList<Recommendation>> entry_rec : recommendationsHashMap.entrySet()) {
                String typeForJSON = entry_rec.getKey();
                ArrayList<Recommendation> recommendationsPerTypeList = entry_rec.getValue();

                JSONObject tmpLimitExposureRecommendation = new JSONObject(); // limit exposure recommendations
                JSONArray recommendations_per_type_JSONArray = new JSONArray(); //tip of the day

                // here I have the type of recommendation and the list of rec_descriptions, for the current person
                for (Recommendation tmp_recommendation : recommendationsPerTypeList) {

                    // if recommendation is "Tip of the Day"
                    if (typeForJSON.equals(OntologyUtils.TBox_NS + "TipOfTheDay")) {

                        recommendations_per_type_JSONArray.add(tmp_recommendation.getRecommendationDescription());
                        // add to JSON
                        recommendations_per_user_JSON.put(typeForJSON.replace(TBox_NS, ""), tmp_recommendation.getRecommendationDescription());

                    } // if recommendation is "Limit exposure Recommendation"
                    else if (typeForJSON.equals(OntologyUtils.TBox_NS + "LimitExposureRecommendation")) {

                        tmpLimitExposureRecommendation.put(tmp_recommendation.getRecommendationDescIdentifier(), tmp_recommendation.getRecommendationDescription());

                        // add to JSON
                        recommendations_per_user_JSON.put(typeForJSON.replace(TBox_NS, ""), tmpLimitExposureRecommendation);
                    }
                }
            }

            if (!recommendations_per_user_JSON.isEmpty()) {
                results_user_JSON.put("isProvidedWithRecommendation", recommendations_per_user_JSON);
            } else {
                results_user_JSON.put("isProvidedWithRecommendation", "");
            }

            results_user_JSONArray.add(results_user_JSON);
        }

        main_JSON.put("results", results_user_JSONArray);

        return main_JSON;

    }

}
