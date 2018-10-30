# movieExcercise

Example code created as an excercise for a job interview. 

## Configuration
Before running the service, please make sure to add the ApiKeys to both services in application.properties

```$xslt
OMDb.apiKey=
theMovieDB.apiKey=
```

## To build and run you need maven and java8 JDK 

### 1. Run jar 

In the root folder of the project run:

`mvn install` 

and then 

`java -jar /target/movies-1.0-SNAPSHOT.jar`

### 2. Execute through springboot maven goal
`mvn spring-boot:run`

### 3. IDE

IDEs such as Eclipse or IntelliJ IDEA should be able to detect how to run a springboot application and add a run configuration. Or rust rightclick the Application class and select run. Follow guides in your IDE help. 

## To end 
1. You can use the localhost:8088/actuator/shutdown endpoint

`curl -i -u admin:P@ssw0rd -X POST http://localhost:8088/actuator/shutdown`

You can change username and apssword in application.properties, or remove them to use standard login.

2. Just kill the process


## Usage

http://localhost:8080/movie/{title}?apiName={apiName}

This does execute search for movies with title. It is utilizing the API search endpoint. 

### Parameters

title - a string containing part of title we want to find
 
apiName - name of the api; two values are accepted
* theMovieDB
* openMovieDB 


### Limitations

Due to limits in API calls for theMovieDB free api, only the first page of results from theMovieDB is returned. 


 




