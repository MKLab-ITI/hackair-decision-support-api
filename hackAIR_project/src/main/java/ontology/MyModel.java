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

package ontology;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import main_objects.Activity;
import main_objects.UserProfile;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;
import static utils.OntologyUtils.ABox_NS;
import static utils.OntologyUtils.TBox_NS;

public class MyModel implements Cloneable {

    private String modelURI;
    private OntModel ontology;

    public MyModel(String modelURI) throws FileNotFoundException {

        this.modelURI = modelURI;
        this.ontology = loadOntologyToModel();

    }

    public MyModel(String modelURI, OntModel modelOntology) {
        this.modelURI = modelURI;
        this.ontology = modelOntology;
    }

    public MyModel(MyModel passingModel) {
        this.modelURI = passingModel.getModelURI();
        this.ontology = passingModel.getOntology();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            return (MyModel) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(" Cloning not allowed. ");
            return this;
        }
    }

    private OntModel loadOntologyToModel() throws FileNotFoundException {

        // Load main file to baseModel
        Model baseModel = ModelFactory.createDefaultModel();
        baseModel.read(this.modelURI, "TURTLE");

        // Create ontModel with imports, from baseModel
        OntModel ontModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM, baseModel);

        return ontModel;

    }

    public void enrichOntologyWithFullUserProfile(UserProfile userProfile) {

        OntModel tmp_ont = enrichOntologyWithUserProfile(userProfile, this.ontology, true);
        updateOntology(tmp_ont);

        for (UserProfile relatedProfile : userProfile.getRelatedProfiles()) {
            tmp_ont = enrichOntologyWithUserProfile(relatedProfile, this.ontology, false);
            updateOntology(tmp_ont);
        }
    }

    public void runInference() throws IOException {

        StringWriter w;
        String outFormat = "TTL";

        // Create OntModel with imports
        OntModel ont = JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM, this.ontology);

        System.out.println("Model size with imports: " + ont.size());

        // Create and add Model for inferred triples
        Model ontNewTriples = ModelFactory.createDefaultModel();
        ontNewTriples.setNsPrefixes(ont);

        ont.addSubModel(ontNewTriples);

        // Register locally defined functions
        SPINModuleRegistry.get().registerAll(ont, null);

        // Run ALL inferences
        ont.enterCriticalSection(false);
        ontNewTriples.enterCriticalSection(false);
        try {
            SPINInferences.run(ont, ontNewTriples, null, null, false, null);
        } finally {
            ont.leaveCriticalSection();
            ontNewTriples.leaveCriticalSection();
        }

//        // Perform inferencing
//        w = new StringWriter();
//        ont.write(w, outFormat);
//        // Create results model
//        w = new StringWriter();
//        // Output results in Turtle
//        ontNewTriples.write(w, outFormat);
//        String newTriplesToString = w.toString();
//        
//        w.close();
        System.out.println("Inferred triples: " + ontNewTriples.size() + " , Final size: " + ont.size());

        //inferred statements - works/only disabled for less printed data
//        StmtIterator stmtIterator = ontNewTriples.listStatements();
//        while (stmtIterator.hasNext()) {
//            Statement statement = stmtIterator.next();
//            System.out.println(statement);
//        }
        // Update ontology of MyModel with inferred triples
        updateOntology(ont);

    }

    public static void printOutAllStatements(OntModel ont) {

        System.out.println(ont.size() + "************************************");
        StmtIterator stmtIterator = ont.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            System.out.println(statement);
        }
        System.out.println("************************************");

    }

    public String getModelURI() {
        return this.modelURI;
    }

    public OntModel getOntology() {
        return this.ontology;
    }

    private void updateOntology(OntModel myUpdatedOntology) {
        this.ontology = myUpdatedOntology;
    }

    // ************************************************************************ //
    /**
     * ******* Populating the ontology with userProfile ********
     */
    // ************************************************************************ //
    public OntModel enrichOntologyWithUserProfile(UserProfile userProfile, OntModel ont, Boolean isDirect) {

//        printOutAllStatements(ont);
//        System.out.println("Ontology passed? - " + ont.size());
//        ExtendedIterator<OntClass> list_classes = ont.listClasses();
//        ExtendedIterator<Individual> list_individuals = ont.listIndividuals();
//        System.out.println("*** Print list of classes");
//        while (list_classes.hasNext()) {
//            System.out.println(list_classes.next().toString());
//        }
//        System.out.println("*** Print list of individuals");
//        while (list_individuals.hasNext()) {
//            System.out.println(list_individuals.next().toString());
//        }
        // Create new Instance of a Person
        OntClass classPerson = ont.getOntClass(TBox_NS + "Person");
        Individual person = ont.createIndividual(ABox_NS + userProfile.getUsername(), classPerson);
        person.addRDFType(classPerson);

        // Add person's name as rdfs:label
        Literal personLabel = ont.createTypedLiteral(userProfile.getUsername());
        person.addLabel(personLabel);

        // Direct or Indirect user
        OntClass direct;
        if (isDirect) {
            direct = ont.getOntClass(TBox_NS + "DirectUser");
        } else {
            direct = ont.getOntClass(TBox_NS + "IndirectUser");
        }
        person.addRDFType(direct);

        //Add person's age
        if (999 != userProfile.getAge()) {
            Literal personAge = ont.createTypedLiteral(userProfile.getAge());
            Property ageProperty = ont.getProperty(TBox_NS + "hasAge");
            person.addProperty(ageProperty, personAge);
        }

        //Add person's gender
        if (!"".equals(userProfile.getGender())) {
//            Literal personGender = ontology.createTypedLiteral(user.getGender());
            Individual genderIndividual = null;
            Property genderProperty = ont.getProperty(TBox_NS + "hasGender");
            if (userProfile.getGender().contains("female")) {
                genderIndividual = ont.getIndividual(TBox_NS + "female");
            } else if (userProfile.getGender().contains("male")) {
                genderIndividual = ont.getIndividual(TBox_NS + "male");
            } else if (userProfile.getGender().contains("other")) {
                genderIndividual = ont.getIndividual(TBox_NS + "other");
            }
            person.addProperty(genderProperty, genderIndividual);
        }

        //Add person's preferred language: predefined language code = en (English)
        // TODO: add other languages in case of additional codes
        Individual languageCodeInOntology = null;
        switch (userProfile.getPreferredLanguageCode()) {
            case "no":
                languageCodeInOntology = ont.getIndividual(TBox_NS + "norwegian_language");
                break;
            case "de":
                languageCodeInOntology = ont.getIndividual(TBox_NS + "german_language");
                break;
            default:
                // we assume that no other languages are supported and results are returned in english
                languageCodeInOntology = ont.getIndividual(TBox_NS + "english_language");
                break;
        }
        Property preferredLanguageProperty = ont.getProperty(TBox_NS + "hasPreferredLanguage");
        person.addProperty(preferredLanguageProperty, languageCodeInOntology);

        //Add if person is pregnant (true/false)
        Literal personPregnant = ont.createTypedLiteral(userProfile.getIsPregnant());
        Property pregnantProperty = ont.getProperty(TBox_NS + "isPregnant");
        person.addProperty(pregnantProperty, personPregnant);

        // Add person's location city and country
//        if ((!"".equals(userProfile.getLocationCity())) && (!"".equals(userProfile.getLocationCountry()))){
        OntClass classLocationCity = ont.getOntClass(TBox_NS + "LocationCity");
        Individual locationCity_indiv = ont.createIndividual(ABox_NS + "location_" + userProfile.getUsername() + "_" + userProfile.getLocationCity(), classLocationCity);
        OntClass classLocationCountry = ont.getOntClass(TBox_NS + "LocationCountry");
        Individual locationCountry_indiv = ont.createIndividual(ABox_NS + "location_" + userProfile.getUsername() + "_" + userProfile.getLocationCountry(), classLocationCountry);
        // Attach location to person
        Property hasLocationProperty = ont.getProperty(TBox_NS + "hasLocation");
        person.addProperty(hasLocationProperty, locationCity_indiv);
        person.addProperty(hasLocationProperty, locationCountry_indiv);

        // Connect location city with observation 
        OntClass classObservation = ont.getOntClass(TBox_NS + "EnvironmentalData");
        Individual observation_indiv = ont.createIndividual(ABox_NS + "observation_" + userProfile.getUsername() + "_" + userProfile.getLocationCity(), classObservation);
        Property hasObservationProperty = ont.getProperty(TBox_NS + "hasEnvironmentalData");
        // Enrich observation with pollutant type and value
        if ("PM_AOD".equals(userProfile.getAirPollutantName())) {
            //add specific EnvironmentalData type details
            OntClass specificClassObservation = ont.getOntClass(TBox_NS + "AODEnvironmentalData");
            observation_indiv.addRDFType(specificClassObservation);
            Property hasEnvironmentalDataType = ont.getProperty(TBox_NS + "hasEnvironmentalDataType");
            Individual PM_AOD_indiv = ont.getIndividual(TBox_NS + "PM_AOD");
            observation_indiv.addProperty(hasEnvironmentalDataType, PM_AOD_indiv);
            if (999.9 != userProfile.getAirPollutantValue()) {  //compare two values with == was checked!
                Property hasNumericalValueProperty = ont.getProperty(TBox_NS + "hasNumericalValue");
                OntClass classValue = ont.getOntClass(TBox_NS + "AODValue");
//                    Literal AOD_value = ont.createTypedLiteral(userProfile.getAirPollutantValue());
                Individual PM_AOD_Value_indiv = ont.createIndividual(ABox_NS + "value_for_observation_" + userProfile.getUsername() + "_" + userProfile.getLocationCity(), classValue);
                // add value to Value instance
                Property hasValueValue = ont.getProperty(TBox_NS + "hasValueValue");
                Literal obs_value = ont.createTypedLiteral(userProfile.getAirPollutantValue());
                PM_AOD_Value_indiv.addProperty(hasValueValue, obs_value);
                // add instance of Value to Observation
                observation_indiv.addProperty(hasNumericalValueProperty, PM_AOD_Value_indiv);
                // Finally attach observation to location (?location :hasObservation ?observation)
                locationCity_indiv.addProperty(hasObservationProperty, observation_indiv);
            }

        } else if ("PM10".equals(userProfile.getAirPollutantName())) {
            //add specific EnvironmentalData type details
            OntClass specificClassObservation = ont.getOntClass(TBox_NS + "AirPollutantEnvironmentalData");
            observation_indiv.addRDFType(specificClassObservation);
            Property hasEnvironmentalDataType = ont.getProperty(TBox_NS + "hasEnvironmentalDataType");
            Individual PM_AOD_indiv = ont.getIndividual(TBox_NS + "PM10");
            observation_indiv.addProperty(hasEnvironmentalDataType, PM_AOD_indiv);
            if (999.9 != userProfile.getAirPollutantValue()) {
                Property hasNumericalValueProperty = ont.getProperty(TBox_NS + "hasNumericalValue");
                OntClass classValue = ont.getOntClass(TBox_NS + "AirPollutantValue");
//                    Literal AOD_value = ont.createTypedLiteral(userProfile.getAirPollutantValue());
                Individual PM_AOD_Value_indiv = ont.createIndividual(ABox_NS + "value_for_observation_" + userProfile.getUsername() + "_" + userProfile.getLocationCity(), classValue);
                // add value to Value instance
                Property hasValueValue = ont.getProperty(TBox_NS + "hasValueValue");
                Literal obs_value = ont.createTypedLiteral(userProfile.getAirPollutantValue());
                PM_AOD_Value_indiv.addProperty(hasValueValue, obs_value);
                // add instance of Value to Observation
                observation_indiv.addProperty(hasNumericalValueProperty, PM_AOD_Value_indiv);
                // Finally attach observation to location (?location :hasObservation ?observation)
                locationCity_indiv.addProperty(hasObservationProperty, observation_indiv);
            }
        } else if ("PM2_5".equals(userProfile.getAirPollutantName())) {
            //add specific EnvironmentalData type details
            OntClass specificClassObservation = ont.getOntClass(TBox_NS + "AirPollutantEnvironmentalData");
            observation_indiv.addRDFType(specificClassObservation);
            Property hasEnvironmentalDataType = ont.getProperty(TBox_NS + "hasEnvironmentalDataType");
            Individual PM_AOD_indiv = ont.getIndividual(TBox_NS + "PM2p5");
            observation_indiv.addProperty(hasEnvironmentalDataType, PM_AOD_indiv);
            if (999.9 != userProfile.getAirPollutantValue()) {
                Property hasNumericalValueProperty = ont.getProperty(TBox_NS + "hasNumericalValue");
                OntClass classValue = ont.getOntClass(TBox_NS + "AirPollutantValue");
//                    Literal AOD_value = ont.createTypedLiteral(userProfile.getAirPollutantValue());
                Individual PM_AOD_Value_indiv = ont.createIndividual(ABox_NS + "value_for_observation_" + userProfile.getUsername() + "_" + userProfile.getLocationCity(), classValue);
                // add value to Value instance
                Property hasValueValue = ont.getProperty(TBox_NS + "hasValueValue");
                Literal obs_value = ont.createTypedLiteral(userProfile.getAirPollutantValue());
                PM_AOD_Value_indiv.addProperty(hasValueValue, obs_value);
                // add instance of Value to Observation
                observation_indiv.addProperty(hasNumericalValueProperty, PM_AOD_Value_indiv);
                // Finally attach observation to location (?location :hasObservation ?observation)
                locationCity_indiv.addProperty(hasObservationProperty, observation_indiv);
            }
        } else if ("PM_fused".equals(userProfile.getAirPollutantName())) {
            //add specific EnvironmentalData type details
            OntClass specificClassObservation = ont.getOntClass(TBox_NS + "AirPollutantEnvironmentalData");
            observation_indiv.addRDFType(specificClassObservation);
            Property hasEnvironmentalDataType = ont.getProperty(TBox_NS + "hasEnvironmentalDataType");
            Individual PM_fused_indiv = ont.getIndividual(TBox_NS + "PM_fused");
            observation_indiv.addProperty(hasEnvironmentalDataType, PM_fused_indiv);
            if (999.9 != userProfile.getAirPollutantValue()) {
                Property hasNumericalValueProperty = ont.getProperty(TBox_NS + "hasNumericalValue");
                OntClass classValue = ont.getOntClass(TBox_NS + "AirPollutantValue");
//                    Literal AOD_value = ont.createTypedLiteral(userProfile.getAirPollutantValue());
                Individual PM_AOD_Value_indiv = ont.createIndividual(ABox_NS + "value_for_observation_" + userProfile.getUsername() + "_" + userProfile.getLocationCity(), classValue);
                // add value to Value instance
                Property hasValueValue = ont.getProperty(TBox_NS + "hasValueValue");
                Literal obs_value = ont.createTypedLiteral(userProfile.getAirPollutantValue());
                PM_AOD_Value_indiv.addProperty(hasValueValue, obs_value);
                // add instance of Value to Observation
                observation_indiv.addProperty(hasNumericalValueProperty, PM_AOD_Value_indiv);
                // Finally attach observation to location (?location :hasObservation ?observation)
                locationCity_indiv.addProperty(hasObservationProperty, observation_indiv);
            }
        }

//        }
        // Add if user is outdoor job user or sports user // TODO: Sports user should be added in the JSON request first and then populated in the ontology        
        Literal personWorksOutdoors = ont.createTypedLiteral(userProfile.getIsOutdoorJobUser());
        Property worksOutdoorsProperty = ont.getProperty(TBox_NS + "worksOutdoors");
        person.addProperty(worksOutdoorsProperty, personWorksOutdoors);

        // Add isSensitiveTo values in the ontology
        for (String userSensitivity : userProfile.getIsSensitiveTo()) {
            // every value should be already defined as an instance of Health Problem in the ontology
            // At the moment we suppose that every health problem is treated the same for recommendations. 
            // So we create a new instance of HealthProblem with the current sensitivity and this person will be of SensitiveHealthPerson as well
            OntClass classHealthProblem = ont.getOntClass(TBox_NS + "HealthProblem");
            Property isSensitiveToProperty = ont.getProperty(TBox_NS + "isSensitiveTo");
            Individual sensitivity_indiv = ont.createIndividual(ABox_NS + userSensitivity, classHealthProblem);
            person.addProperty(isSensitiveToProperty, sensitivity_indiv);

        }

        // Activity related calculations
        HashMap<String, Activity> activitiesHashMap = Activity.createListOfDeclaredActivities();
        Property hasPreferredActivityProperty = ont.getProperty(TBox_NS + "hasPreferredActivity");
        Individual activityIndiv;
        // Add preferred indoor activities of the user in the ontology
//        for (String indoorActivity: userProfile.getPredefinedIndoorActivities()){
//            activityIndiv = ont.getIndividual( 
//                    activitiesHashMap.get(indoorActivity).getActivityURI() 
//            );
//            person.addProperty(hasPreferredActivityProperty, activityIndiv);            
//        }
        // Add preferred outdoor activities of the user in the ontology
        for (String outdoorActivity : userProfile.getPredefinedOutdoorActivities()) {
            if (!outdoorActivity.equals("walking with baby")) {
                activityIndiv = ont.getIndividual(
                        activitiesHashMap.get(outdoorActivity).getActivityURI()
                );
                person.addProperty(hasPreferredActivityProperty, activityIndiv);

            }

        }

        // Create new hackairTbox:Request for the new person
        OntClass classRequest = ont.getOntClass(TBox_NS + "Request");
        Individual request_indiv = ont.createIndividual(ABox_NS + "request_" + userProfile.getUsername(), classRequest);
        // Attach person to request_individual
        Property requestToPersonProperty = ont.getProperty(TBox_NS + "involvesPerson");
        request_indiv.addProperty(requestToPersonProperty, person);
        // Attach location to request_individual
        Property requestToLocationProperty = ont.getProperty(TBox_NS + "involvesLocation");
        request_indiv.addProperty(requestToLocationProperty, locationCity_indiv);

        return ont;
    }

}
