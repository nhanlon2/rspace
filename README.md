# rspace exercise

###run the tests : `./gradlew clean test`

###run the application : `./gradlew bootRun`

###Rest Api:

`http://localhost:8080/` - shows all data belonging to user

`http://localhost:8080/sampleSummaries` - shows selected fields of samples belonging to user
takes an (optional) query param `expiresInLessThan` with the value being days:

e.g `http://localhost:8080/sampleSummaries?expiresInLessThan=7`

`http://localhost:8080/sampleDetails/{id}` - takes a path parameter which is the id of the sample. 

e.g `http://localhost:8080/sampleDetails/1277964` (this displays some container nesting)

Shows all details of a single sample. Some location information has been generated to help find the sample.

//TODO
 - Investigate how locations are structured correctly for samples. Format the location display so that null doesn't show.
 - Create a simple web UI :-)
