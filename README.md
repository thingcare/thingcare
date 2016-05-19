# thingcare
[![Build Status](https://travis-ci.org/thingcare/thingcare.svg?branch=master)](https://travis-ci.org/thingcare/thingcare)
[![Code Climate](https://codeclimate.com/github/thingcare/thingcare/badges/gpa.svg)](https://codeclimate.com/github/thingcare/thingcare)
[![Test Coverage](https://codeclimate.com/github/thingcare/thingcare/badges/coverage.svg)](https://codeclimate.com/github/thingcare/thingcare/coverage)
[![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/thingcare/thingcare)

This application was generated using JHipster, you can find documentation and help at [https://jhipster.github.io](https://jhipster.github.io).

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:


## Building for production

To optimize the thingcare client for production, run:

    ./mvnw -Pprod clean package

To ensure everything worked, run:

    java -jar target/*.war --spring.profiles.active=prod

## Continuous Integration

To setup this project in Jenkins, use the following configuration:

* Project name: `thingcare`
* Source Code Management
    * Git Repository: `git@github.com:xxxx/thingcare.git`
    * Branches to build: `*/master`
    * Additional Behaviours: `Wipe out repository & force clone`
* Build Triggers
    * Poll SCM / Schedule: `H/5 * * * *`
* Build
    * Invoke Maven / Tasks: `-Pprod clean package`
* Post-build Actions
    * Publish JUnit test result report / Test Report XMLs: `build/test-results/*.xml`

[JHipster]: https://jhipster.github.io/
