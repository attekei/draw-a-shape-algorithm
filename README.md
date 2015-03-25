# draw-a-shape-algorithm

[![Build Status](https://travis-ci.org/attrck/draw-a-shape-algorithm.svg?branch=master)](https://travis-ci.org/attrck/draw-a-shape-algorithm) [![Coverage Status](https://img.shields.io/coveralls/attrck/drawing-app-algorithm.svg)](https://coveralls.io/r/attrck/drawing-app-algorithm)

## What is this?

* A point cloud based algorithm for comparing similarity of two outline pictures. It manipulates the first picture by translating, rotating and scaling it to match the second picture as well as possible. The resulting  image is as close as the second image as possible and thus usable for further comparison.
* An accompanied web API giving an easy-to-access interface to the algorithm. It also provides a mechanism for collecting user estimates of how well images match together.

[Draw a Shape](https://github.com/attrck/draw-a-shape) is a complete Android game which uses the web API for comparing a user drawing and a model image of the drawn shape.

## Examples

See `TestPlotter` for an use example.

## Setup project

### Clone repository

Intellij IDEA toolbar: `VCS -> Checkout from Version Control -> Git`.
Repo address is `https://github.com/attrck/draw-a-shape-algorithm`.

### Install sbt and dependencies

[Install sbt](http://www.scala-sbt.org/0.13/tutorial/Setup.html). Then run `sbt update` in project folder to install dependencies.

### Create Intellij IDEA project

Run `sbt gen-idea` in project folder.

## Build a JAR (for Java interoperability)

Run `sbt proguard:proguard` in project folder to build JAR file of the algorithm (without API). It contains the necessary parts of Scala standard library for getting the algorithm work in Java projects.

## Deploy to Heroku

The project is configured to be easily deployed to Heroku. Project assumes that you have a MongoLab MongoDB database running.
