# hackAIR Decision Support API


## Description
The hackAIR Decision Support (DS) API is a dedicated software responsible for: (i) the representation of a problem (request) for decision support in a formal, comprehensible and hackAIR-ontology-compatible way; (ii) the communication between the <a href="https://platform.hackair.eu/" target="_blank">hackAIR UI (app/platform)</a>, or even other third-party DS systems, and the <a href="http://mklab.iti.gr/project/hackair-ontologies" target="_blank">ontology-based representation and reasoning knowledge base (KB)</a>, which supports the recommendation mechanism. The involved web-services were created with the adoption of state-of-the-art technologies: RESTful communication, exchange of information on the basis of JSON objects, etc. The hackAIR DS API is publicly available and may run both as an independent service or as an integrated service on the hackAIR app/platform. 


## Web-Services
Up to now, the hackAIR DS API offers the following web services through POST requests:
* _{BASE_URL}/hackAIR_project/api/dynamicPopulation_: performs the dynamic population of involved data (user profile and enviromnental data) in the hackAIR KB for further manipulation.
* _{BASE_URL}/hackAIR_project/api/requestRecommendation_: performs a step-by-step process, i.e. (i) receives a JSON object in pre-defined format, through a POST request to the service of discourse, (ii) converts the JSON data to a hackAIR-compatible ontology-based problem description language for populating new instances (user profile details and environmental related data) in the knowledge base; (iii) triggers the hackAIR reasoning mechanism for handling the available data and rules and for inferencing new knowledge, i.e. provide relevant recommendations to the users. 


### Key features 
The hackAIR DS module supports:
* Multi-threading requests (syncronized, i.e. first come first served). 
* Combined user-profiles' (primary and secondary) requests for decision support.
* Recommendation messages in three different languages: English, German and Norwegian


### JSON parameters
Below, we specify all the mandatory and optional JSON parameters that are accepted in the POST request:

Parameter | JSON Type | Mandatory(M) / Optional(O) | Accepted values
:--- | :---: | :---: | :---
`username` | object | M | any *string* value
`gender` | object | O | One of the following: *male*, *female*, *other*
`age` | object | M | any *integer* value
`locationCity` | object | M | any *string* value
`locationCountry` | object | M | any *string* value
`isPregnant` | object | O | any *boolean* value
`isSensitiveTo` | array | O | One or more of the following: *Asthma*, *Allergy*, *Cardiovascular*, *GeneralHealthProblem*
`isOutdoorJobUser` | object | O | any *boolean* value
`preferredActivities` | object | O | `preferredOutdoorActivities`
`preferredOutdoorActivities` | array | O | One or more of the following: *picnic*, *running*, *walking*, *outdoor job*, *biking*, *playingInPark*, *tennis*, *generalActivity*
`airPollutant` | object | M | Both: `airPollutantName`, `airPollutantValue`
`airPollutantName` | object | M | One of the following: *PM_AOD*, *PM10*, *PM2_5*, *PM_fused*
`airPollutantValue` | object | M | any *double* value
`preferredLanguageCode` | object | O | One of the following: *en*, *de*, *no*
`relatedProfiles` | array | O | One or more JSON objects, each of which includes the aforementioned mandatory/optional fields.


### Example JSON object

#### With primary and secondary profile description, in one single request

```
{
  "username": "Helen_Hall",
  "age":"32", 
  "locationCity": "Berlin",
  "locationCountry": "Germany",
  "isPregnant": false,
  "isSensitiveTo": ["Asthma"],
  "preferredLanguageCode": "de",
  "airPollutant": {
    "airPollutantName": "PM_fused",
    "airPollutantValue": "3.5",
  }
  "preferredActivities": {
    "preferredOutdoorActivities": ["picnic","running"]
  },
  "relatedProfiles": [{
    "username": "Helen_Hall_secondary_profile",
    "gender":"female",
    "age":"1", 
    "locationCity": "Berlin",
    "locationCountry": "Germany",
    "preferredLanguageCode": "de",
    "airPollutant": {
      "airPollutantName": "PM_fused",
      "airPollutantValue": "3.5"
    }
  }]
}
```


## Requirements - Dependencies
The hackAIR DS API is implemented in [Java EE 7](https://docs.oracle.com/javaee/7/index.html) with the adoption of [JAX-RS](http://docs.oracle.com/javaee/6/api/javax/ws/rs/package-summary.html) library. Additional dependencies are listed below:
* [Apache Jena](https://jena.apache.org/): a free and open-source Java framework for building Semantic Web and Linked Data applications.
* [SPIN API](http://topbraid.org/spin/api/): an open source Java API to enable the adoption of SPIN rules and the handling of the implemented rule-based reasoning mechanism. 
* [GlassFish Server 4.1.1](http://www.oracle.com/technetwork/middleware/glassfish/overview/index.html): an open-source application server for the Java EE platform, utilised for handling HTTP queries to the RESTful API.
* [json-simple](https://github.com/fangyidong/json-simple): a well-known java toolkit for parsing (encoding/decoding) JSON text.
* [hackAIR Knowledge Base (KB) and Reasoning Framework](http://mklab.iti.gr/project/hackair-ontologies): this regards the implemented ontological representation of the domain of discourse that handles both the semantic integration and reasoning of environmental and user-specific data, in order to provide recommendations to the hackAIR users, with respect to: (i) personal health and user preferences (activities, daily routine, etc.), and (ii) current AQ conditions of the location of interest. The hackAIR DS module utilises the sources of the hackAIR KB and reasoning framework as a background resource of information, from which it acquires the necessary semantic relations and information in order to support relevant recommendations’ provision to the users upon request for decision support. 


## Instructions
1. Install Java EE 7 and GlassFish 4.1.1 in your computer.
2. Clone the project locally in your computer.
3. **Run** Glassfish server and **deploy** [hackAIR_project.war](hackAIR_project/target) application.
4. Submit POST requests in relevant web-services, as described [here](https://github.com/MKLab-ITI/hackair-decision-support-api#web-services)

or

1. Install Java EE 7 and a common Java IDE framework.
2. Clone the project locally in your computer.
3. Import the java project to the workspace of the IDE framework.
4. Set up a Glassfish server from the IDE environment to run locally.
5. **Run** the project through the IDE utilities.
6. Submit POST requests in relevant web-services, as described [here](https://github.com/MKLab-ITI/hackair-decision-support-api#web-services)


## Resources
The official hackAIR ontology resources are available [here](http://mklab.iti.gr/project/hackair-ontologies).


## Citation
Riga M., Kontopoulos E., Karatzas K., Vrochidis S. and Kompatsiaris I. (2018), An Ontology-based Decision Support Framework for Personalised Quality of Life Recommendations. In: Dargam F., Delias P., Linden I., Mareschal B. (eds) Decision Support Systems VIII: Sustainable Data-Driven and Evidence-Based Decision Support. 4th International Conference on Decision Support System Technology (ICDSST 2018). Lecture Notes in Business Information Processing (LNBIP), Volume 313, Springer, Cham. doi: [https://doi.org/10.1007/978-3-319-90315-6_4](https://doi.org/10.1007/978-3-319-90315-6_4).


## Contact
For further details, please contact Marina Riga (mriga@iti.gr)


## Credits
The hackAIR Decision Support API was created by <a href="http://mklab.iti.gr/" target="_blank">MKLab group</a> under the scope of <a href="http://www.hackair.eu/" target="_blank">hackAIR</a> EU Horizon 2020 Project.


![mklab logo](http://mklab.iti.gr/prophet/_static/mklab_logo.png) &nbsp; &nbsp; &nbsp; <img src="./images/hackAir_logo_RGB.png" alt="hackAIR logo" width="125" height="133">