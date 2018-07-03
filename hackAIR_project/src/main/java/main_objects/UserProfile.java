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

import java.util.HashSet;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UserProfile {

    private String username;
    private String gender;
    private Integer age;
    private String locationCity;
    private String locationCountry;
    private Boolean isPregnant;
    private Boolean isOutdoorJobUser;
    private Boolean walkingWithBaby;
    private HashSet<String> isSensitiveTo;
    private HashSet<String> meansOfTransport;
    private HashSet<String> predefinedIndoorActivities;
    private HashSet<String> predefinedOutdoorActivities;

    private String airPollutantName;
    private Double airPollutantValue;

    private HashSet<UserProfile> relatedProfiles;

    private Boolean isDirectUser;

    private String preferredLanguageCode;

    public UserProfile(Boolean isDirectUser) {

        this.username = "";
        this.gender = "";
        this.age = 999;
        this.locationCity = "";
        this.locationCountry = "";
        this.isPregnant = false;
        this.isOutdoorJobUser = false;

        this.isSensitiveTo = new HashSet<String>();

        this.meansOfTransport = new HashSet<String>();

        this.predefinedIndoorActivities = new HashSet<String>();
        this.predefinedOutdoorActivities = new HashSet<String>();

        this.airPollutantName = "";
        this.airPollutantValue = 999.9;

        this.relatedProfiles = new HashSet<UserProfile>();

        this.isDirectUser = isDirectUser;

        this.preferredLanguageCode = "en"; // by default

        this.walkingWithBaby = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public Boolean getIsPregnant() {
        return isPregnant;
    }

    public void setIsPregnant(Boolean isPregnant) {
        this.isPregnant = isPregnant;
    }

    public Boolean getIsOutdoorJobUser() {
        return isOutdoorJobUser;
    }

    public void setIsOutdoorJobUser(Boolean isOutdoorJobUser) {
        this.isOutdoorJobUser = isOutdoorJobUser;
    }

    public HashSet<String> getIsSensitiveTo() {
        return isSensitiveTo;
    }

    public void setIsSensitiveTo(HashSet<String> isSensitiveTo) {
        this.isSensitiveTo = isSensitiveTo;
    }

    public void addIsSensitiveToItem(String isSensitiveTo) {
        this.isSensitiveTo.add(isSensitiveTo);
    }

    public HashSet<String> getMeansOfTransport() {
        return meansOfTransport;
    }

    public void setMeansOfTransport(HashSet<String> meansOfTransport) {
        this.meansOfTransport = meansOfTransport;
    }

    public void addMeanOfTransportItem(String meanOfTransport) {
        this.meansOfTransport.add(meanOfTransport);
    }

    public HashSet<String> getPredefinedIndoorActivities() {
        return predefinedIndoorActivities;
    }

    public void setPredefinedIndoorActivities(HashSet<String> predefinedIndoorActivities) {
        this.predefinedIndoorActivities = predefinedIndoorActivities;
    }

    public void addPredefinedIndoorActivity(String indoorActivity) {
        this.predefinedIndoorActivities.add(indoorActivity);
    }

    public HashSet<String> getPredefinedOutdoorActivities() {
        return predefinedOutdoorActivities;
    }

    public void setPredefinedOutdoorActivities(HashSet<String> predefinedOutdoorActivities) {
        this.predefinedOutdoorActivities = predefinedOutdoorActivities;
    }

    public void addPredefinedOutdoorActivity(String outdoorActivity) {
        this.predefinedOutdoorActivities.add(outdoorActivity.toLowerCase());
    }

    public String getAirPollutantName() {
        return airPollutantName;
    }

    public void setAirPollutantName(String airPollutantName) {
        this.airPollutantName = airPollutantName;
    }

    public Double getAirPollutantValue() {
        return airPollutantValue;
    }

    public void setAirPollutantValue(Double airPollutantValue) {
        this.airPollutantValue = airPollutantValue;
    }

    public HashSet<UserProfile> getRelatedProfiles() {
        return relatedProfiles;
    }

    public void setRelatedProfiles(HashSet<UserProfile> relatedProfiles) {
        this.relatedProfiles = relatedProfiles;
    }

    public void addRelatedProfile(UserProfile relatedProfile) {
        this.relatedProfiles.add(relatedProfile);
    }

    public Boolean getIsDirectUser() {
        return this.isDirectUser;
    }

    public String getPreferredLanguageCode() {
        return this.preferredLanguageCode;
    }

    public void setPreferredLanguageCode(String tmpPreferredLanguageCode) {
        this.preferredLanguageCode = tmpPreferredLanguageCode;
    }

    public Boolean getWalkingWithBaby() {
        return walkingWithBaby;
    }

    public void setWalkingWithBaby(Boolean walkingWithBaby) {
        this.walkingWithBaby = walkingWithBaby;
    }

    public static UserProfile createUserProfileFromJSONobject(JSONObject jsonObject, Boolean isDirectUser) {

        UserProfile user = new UserProfile(isDirectUser);

        // get/set username
        if (jsonObject.containsKey("username")) {
            if ((!"".equals(jsonObject.get("username").toString()))) {
                user.setUsername(jsonObject.get("username").toString().trim());
            }
        }

        // get/set gender
        if (jsonObject.containsKey("gender")) {
            if ((!"".equals(jsonObject.get("gender").toString()))) {
                user.setGender(jsonObject.get("gender").toString().trim());
            }
        }
        // get/set age
        if (jsonObject.containsKey("age")) {
            user.setAge(Integer.parseInt(jsonObject.get("age").toString().trim()));
        }
        // get/set location city
        if (jsonObject.containsKey("locationCity")) {
            if ((!"".equals(jsonObject.get("locationCity").toString()))) {
                user.setLocationCity(jsonObject.get("locationCity").toString().trim());
            }
        }
        // get/set location country
        if (jsonObject.containsKey("locationCountry")) {
            if ((!"".equals(jsonObject.get("locationCountry").toString()))) {
                user.setLocationCountry(jsonObject.get("locationCountry").toString().trim());
            }
        }
        // get/set is pregnant
        if (jsonObject.containsKey("isPregnant")) {
            user.setIsPregnant(Boolean.parseBoolean(jsonObject.get("isPregnant").toString().trim()));
        }
        // get/set is outdoor job user
        if (jsonObject.containsKey("isOutdoorJobUser")) {
            user.setIsOutdoorJobUser(Boolean.parseBoolean(jsonObject.get("isOutdoorJobUser").toString().trim()));
        }
        // get/set is sensitive to
        if (jsonObject.containsKey("isSensitiveTo")) {
            JSONArray healthProblems = (JSONArray) jsonObject.get("isSensitiveTo");
            Iterator<String> iterator = healthProblems.iterator();
            while (iterator.hasNext()) {
                user.addIsSensitiveToItem(iterator.next().trim());
            }
        }
        // get/set uses means of transport
//        if (jsonObject.containsKey("meansOfTransport")) {
//            JSONArray meansOfTransportArray = (JSONArray) jsonObject.get("meansOfTransport");
//            Iterator<String> iterator = meansOfTransportArray.iterator();
//            while (iterator.hasNext()) {
////                if (!"".equals(iterator.next().toString())){
//                user.addMeanOfTransportItem(iterator.next());
////                }
//            }
//        }
        // get/set predefined activities
        if (jsonObject.containsKey("preferredActivities")) {
            JSONObject jsonPredefinedActivities = (JSONObject) jsonObject.get("preferredActivities");
//            if (jsonPredefinedActivities.containsKey("preferredIndoorActivities")) {
//                JSONArray indoorActivitiesArray = (JSONArray) jsonPredefinedActivities.get("preferredIndoorActivities");
//                Iterator<String> iterator = indoorActivitiesArray.iterator();
//                while (iterator.hasNext()) {
//                    user.addPredefinedIndoorActivity(iterator.next());
//                }
//            }
            if (jsonPredefinedActivities.containsKey("preferredOutdoorActivities")) {
                JSONArray outdoorActivitiesArray = (JSONArray) jsonPredefinedActivities.get("preferredOutdoorActivities");
                Iterator<String> iterator = outdoorActivitiesArray.iterator();
                while (iterator.hasNext()) {

                    String tmp_activity = iterator.next().trim().toLowerCase();
                    user.addPredefinedOutdoorActivity(tmp_activity);
                    if (tmp_activity.equals("walking with baby")) {
                        user.setWalkingWithBaby(Boolean.TRUE);
                    }
                }
            }
        }
        // get/set air pollutant name and value
        if (jsonObject.containsKey("airPollutant")) {
            JSONObject jsonAirPollutant = (JSONObject) jsonObject.get("airPollutant");
            if (jsonAirPollutant.containsKey("airPollutantName")) {
                if (!"".equals(jsonAirPollutant.get("airPollutantName").toString())) {
                    user.setAirPollutantName(jsonAirPollutant.get("airPollutantName").toString().trim());
                }
            }
            if (jsonAirPollutant.containsKey("airPollutantValue")) {
                user.setAirPollutantValue(Double.parseDouble(jsonAirPollutant.get("airPollutantValue").toString().trim()));
            }
        }

        // get/set preferredLanguage
        if (jsonObject.containsKey("preferredLanguageCode")) {
            if ((!"".equals(jsonObject.get("preferredLanguageCode").toString()))) {
                user.setPreferredLanguageCode(jsonObject.get("preferredLanguageCode").toString().toLowerCase().trim()); //always to lowercase
            }
        }

        return user;

    }

    public static UserProfile createUserWithRelatedProfilesFromJSONobject(JSONObject JSONObject) {

        UserProfile directUser = createUserProfileFromJSONobject(JSONObject, Boolean.TRUE); //true: isDirectUser

        // Find if related profiles are defined in JSONObject and create relevant UserProfile objects.
        if (JSONObject.containsKey("relatedProfiles")) {

            JSONArray JSONRelatedProfilesArray = (JSONArray) JSONObject.get("relatedProfiles");

            Iterator<JSONObject> iterator = JSONRelatedProfilesArray.iterator();
            while (iterator.hasNext()) {
                // Create profile of indirect user
                UserProfile tmpRelatedProfile = createUserProfileFromJSONobject(iterator.next(), Boolean.FALSE); //false: isIndirectUser
                // Attach related profiles to the directUser
                directUser.addRelatedProfile(tmpRelatedProfile);
            }
        }

        return directUser;

    }

    @Override
    // Print values of UserProfile, with no specific format -just concatenating them in a string
    public String toString() {
        
        String directUser = toString(this, true);
        
        String indirectUsers = "";        
        for (UserProfile relatedProfile : this.getRelatedProfiles()) {            
            indirectUsers = indirectUsers + "\n"
                    + toString(relatedProfile, false);
        }

        return directUser + "\n" + indirectUsers;

    }
    
    public String toString(UserProfile userProfile, Boolean directUser){
        
        String userToString = "";
        
        userToString
                = "\tDirectUser: " + userProfile.getIsDirectUser()
                + ", name: " + userProfile.getUsername()
                + ", gender: " + userProfile.getGender()
                + ", age: " + userProfile.getAge().toString()
                + ", location: " + userProfile.getLocationCity() + "_" + userProfile.getLocationCountry()
                + ", isPregnant: " + userProfile.getIsPregnant().toString()
                + ", isOutdoorJobUser: " + userProfile.getIsOutdoorJobUser().toString()
                + ", isSensitiveTo: " + userProfile.getIsSensitiveTo().toString()
//                + ", getsMeansOfTransport: " + userProfile.getMeansOfTransport().toString()
                + ", hasPreferredActivities: " + userProfile.getPredefinedIndoorActivities() + "_" + userProfile.getPredefinedOutdoorActivities()
                + ", airPollutantNameValue: " + userProfile.getAirPollutantName() + "_" + userProfile.getAirPollutantValue()
                + ", hasPreferredLang: " + userProfile.getPreferredLanguageCode()
                + ", walkingWithBaby: " + userProfile.walkingWithBaby;
        
        return userToString;
    }

}
