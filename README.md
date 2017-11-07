# json-diff

<!--- badges -->
[![Build Status](https://travis-ci.org/xeniaka/json-diff.svg)](http://travis-ci.org/xeniaka/json-diff)
[![Coverage Status](https://coveralls.io/repos/github/xeniaka/json-diff/badge.svg?branch=master)](https://coveralls.io/github/xeniaka/json-diff?branch=master)

Finds the difference on provided JSON data.

## Endpoints
* POST /json-diff/{id}/left - Inserts JSON in the body of the request to the **left** position for {id}.
* POST /json-diff/{id}/right - Inserts JSON in the body of the request to the **right** position for {id}.
* GET /json-diff/{id} - Evaluates the difference between objects inserted into **right** and **left** positions for {id}.

## Run Locally

### Requirements
* [Java 8](http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html)
* [Gradle](https://gradle.org/install/)

### How to run
#### Build / Create executable
```sh
$ gradle build
```

#### Start application
```sh
$ java -jar build/libs/json-diff-0.0.1-SNAPSHOT.jar
```

#### Swagger
When running locally, JSON Diff API can be accessed via
[json-diff-api](http://localhost:8080/swagger-ui.html#/json45diff45controller).

## Usage
Insert following JSON object on the left position
```
{
  "foo":"bar"
}
```
Command:

```sh
curl -X POST --header 'Content-Type: application/base64' --header 'Accept: application/json' -d '{"foo":"bar"}' 'http://localhost:8080/json-diff/test/left'
```

Insert following JSON object on the right position
```
{
  "fee":"boo"
}
```
Command:

```sh
curl -X POST --header 'Content-Type: application/base64' --header 'Accept: application/json' -d '{"fee":"boo"}' 'http://localhost:8080/json-diff/test/right'
```
Verify the difference

```sh
curl -X GET --header 'Accept: application/json' 'http://localhost:8080/json-diff/test'
```

Result:
```
{
  "equal": false,
  "equalSize": true,
  "diffs": [
    {
      "offset": 3,
      "length": 2
    },
    {
      "offset": 9,
      "length": 2
    }
  ]
}
```
**Explanation**: The two objects show 2 differences:
1. First difference starts at position 3 and has length equals 2;
2. Second difference starts at position 9 and its length is equal 2.