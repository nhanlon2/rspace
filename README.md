# rspace exercise

###run the tests : `./gradlew clean test`

###run the application : `./gradlew bootRun`

###Rest Api:

`http://localhost:8080/samples` - shows all data belonging to user

`http://localhost:8080/sampleSummaries` - shows selected fields of samples belonging to user.
Takes an (optional) query param `expiresInLessThan` with the value being days:

e.g `http://localhost:8080/sampleSummaries?expiresInLessThan=7`

`http://localhost:8080/sampleDetails/{id}` - takes a path parameter which is the id of the sample. 

e.g `http://localhost:8080/sampleDetails/1277977` (this displays some container nesting)

Shows all details of a single sample. Some location information has been generated to help find the sample.

//TODO
 - Determine if the users find the `expiryWithinDays` query useful or confusing.
 - Investigate how locations are structured correctly for samples. Format the location display so that null doesn't show.
 - Much more investigation into the range of potential data shapes.
 - Create a simple web UI :-)

###Error handling and logging

There is a catch all error handler `ExceptionHandlerControllerAdvice`. I developed this pattern for my team which was having a problem
with errors. They were being logged multiple times per error; logging was sometimes at `error` level for predictable events such as
an item not being found. Exception details that exposed our tech stack were also leaking to clients of the Rest Api.

The error handler looks for annotations on the Exceptions it catches. If they match Http Response Statuses then this is handled
as a predicted error (unless the status is 500!), logged as `warn` and the message is trusted to be displayed to the user. Otherwise, they are unexpected exceptions
and handled as internal server errors, logged at `error` and user gets a generic apology message. The only predictable error I could forsee in the
exercise here was sample not found. I created an Exception for this with an `@ResponseStatus(HttpStatus.NOT_FOUND)` annotation.
