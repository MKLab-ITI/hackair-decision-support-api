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

import java.util.HashMap;
import static utils.OntologyUtils.TBox_NS;

public class Activity {

    private String activityLabel;
    private String activityURI;
    private String activityType;
    private String activityTypeURI;

    /**
     * Activity is for representing the main parameters of Activity class in the
     * ontology
     *
     * @param activityLabel
     * @param activityURI
     * @param activityType, here equals either "baseURI+IndoorActivity" or
     * "baseURI+OutdoorActivity
     */
    public Activity(String label, String URI, String type) {
        this.activityLabel = label;
        this.activityURI = URI;
        this.activityType = type;

        if (type.equals("indoor")) {
            this.activityTypeURI = TBox_NS + "IndoorActivity";
        } else if (type.equals("outdoor")) {
            this.activityTypeURI = TBox_NS + "OutdoorActivity";

        }
    }

    public String getActivityLabel() {
        return this.activityLabel;
    }

    public String getActivityURI() {
        return this.activityURI;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public String getActivityTypeURI() {
        return this.activityTypeURI;
    }

    /**
     * Returns a HashMap<String, Activity> that contains Indoor/Outdoor
     * Activities together with an ID_string and their relative Activity
     * instances
     *
     * @return HashMap
     */
    public static HashMap<String, Activity> createListOfDeclaredActivities() {

        HashMap<String, Activity> activityHashMap = new HashMap<>();

        // create indoor activities according to already defined classes/instances in ontology
        activityHashMap.put("bus",                  new Activity("bus",                 TBox_NS + "bus_activity", "indoor"));
        activityHashMap.put("car",                  new Activity("car",                 TBox_NS + "car_activity", "indoor"));
        activityHashMap.put("gym",                  new Activity("gym",                 TBox_NS + "gym_activity", "indoor"));
        activityHashMap.put("household",            new Activity("household",           TBox_NS + "household_activity", "indoor"));
        activityHashMap.put("office",               new Activity("office",              TBox_NS + "office_activity", "indoor"));
        activityHashMap.put("school",               new Activity("school",              TBox_NS + "school_activity", "indoor"));
        activityHashMap.put("swimming indoors",     new Activity("swimming indoors",    TBox_NS + "swimming_indoors_activity", "indoor"));

        // create outdoor activities according to already defined classes/instances in ontology
        activityHashMap.put("biking",                   new Activity("biking",                  TBox_NS + "biking_activity", "outdoor"));
        activityHashMap.put("jogging",                  new Activity("jogging",                 TBox_NS + "jogging_activity", "outdoor"));
        activityHashMap.put("motorcycling",             new Activity("motorcycling",            TBox_NS + "motorcycle_activity", "outdoor"));
        activityHashMap.put("picnic",                   new Activity("picnic",                  TBox_NS + "picnic_activity", "outdoor"));
        activityHashMap.put("playing in park",          new Activity("playing in park",         TBox_NS + "playing_in_park_activity", "outdoor"));
        activityHashMap.put("playing in playground",    new Activity("playing in playground",   TBox_NS + "playing_in_playground_activity", "outdoor"));
        activityHashMap.put("swimming outdoors",        new Activity("swimming outdoors",       TBox_NS + "swimming_outdoors_activity", "outdoor"));
        activityHashMap.put("tennis",                   new Activity("tennis",                  TBox_NS + "tennis_activity", "outdoor"));
        activityHashMap.put("walking",                  new Activity("walking",                 TBox_NS + "walking_activity", "outdoor"));
        activityHashMap.put("outdoor job",              new Activity("outdoor job",             TBox_NS + "working_activity", "outdoor"));
        activityHashMap.put("general activity",         new Activity("general activity",        TBox_NS + "sports_general_activity", "outdoor"));
        activityHashMap.put("running",                  new Activity("running",                 TBox_NS + "sports_general_activity", "outdoor"));

        return activityHashMap;

    }

}
