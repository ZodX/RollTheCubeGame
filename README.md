# Roll The Cube

This is a logical game which I have to implement for a project in my Softvare Engeneering class.

## Game Rules

You are playing with a cube which has 1 red side and 5 blue sides.
You have to get to the goal field by rolling without touching the ground with the red side of the cube. 
The map has blocked fields, where are other cubes sitting so the player has to bypass those fields.
The player's cube spawns with the red side facing up.

## Game represetation

  * Open fields are represented with 6.
  * Blocked fields are represented with 7.
  * Goal field('s) is(/are) represented with 8.
  * Other designating elements are represented with [9..11].
  * The player's cube is represented with [0..5]
	* 0: The red side.
	* 1..5: The blue sides.
  * In the matrix representation the player's cube can be found by the number of the current side facing up("to the sky").
  * The scores of the players are stored in a JPA database.

## Project dependencies

  * OpenJFX
  * JPA
  * Maven Plugins
    * Javadoc
    * JXR
    * Checkstyle
    * Surefire Report
    * Clover
  * SLF4J
    * Log4j 2

## Requirements

Building the project requires JDK 11 or later and [Apache Maven](https://maven.apache.org/).

## Getting started

Use the following command in the base dicertory of the project to create the jar file:

```bash
$ mvn package
```

For creating the site showing details use:

```bash
$ mvn site
```

For running the game from terminal:

```bash
$ mvn exec:java
```

> NOTE The path to the project should contain only english letters. (US-ASCII)
