drawing-app-algorithm
=====================
[![Build Status](https://travis-ci.org/attrck/drawing-app-algorithm.svg?branch=master)](https://travis-ci.org/attrck/drawing-app-algorithm) [![Coverage Status](https://img.shields.io/coveralls/attrck/drawing-app-algorithm.svg)](https://coveralls.io/r/attrck/drawing-app-algorithm)
## About

*The development of algorithm is still in early phase. The text below describes the intended functionality.*

An point cloud based algorithm for comparing similarity of two pictures. Written in Scala and prepared for interoperability with Java. Features include:

* Manipulation of the first picture by translating, rotating and scaling it to match the second picture as well as possible. The target is to make those two pictures easily comparable.
* Comparision algorithm that takes two images and returns a number in scale 0-100 telling how similar those pictures are.

Created for the needs of master's thesis of Ilmari Arnkil.

## Example of use

Algorithm can be easily used from an Android app:

```java
  int[] drawedPixels, examplePixels;
  canvasView.getDrawingCache().getPixels(drawedPixels);
  exampleView.getDrawingCache().getPixels(examplePixels);

  PointCloud drawedCloud = PointCloud.fromImagePixelArray(pixels, canvasView.width(), Color.BLACK);

  PointCloud normalizedDrawedCloud = drawedCloud
    .centerToOrigoByGravity()
    .scaleByMean()
    .downsample();

  PointCloud normalizedExampleCloud = exampleCloud
    .centerToOrigoByGravity()
    .scaleByMean()
    .downsample();
  
  PointCloud matchingDrawedCloud = normalizedDrawedCloud
    .runCMAES(normalizedExampleCloud);

  int[] newPixels = matchingDrawedCloud.toImagePixelArray(Color.WHITE, Color.BLACK);
  int matchResult = matchingDrawedCloud.compareTo(normalizedExampleCloud);
  
  // show match result etc.
```

## Setup project

#### Clone repository

Intellij IDEA toolbar: `VCS -> Checkout from Version Control -> Git` Repo address is `https://github.com/atk-partio/ilmomasiina.git`.

#### Install sbt and dependencies

[Install sbt](http://www.scala-sbt.org/0.13/tutorial/Setup.html). Then run `sbt update` in project folder to install dependencies.

#### Create Intellij IDEA project

Run `sbt gen-idea` in project folder.

## Build for Android

Run `sbt proguard:proguard` in project folder to build the JAR file. Copy it from `target/scala-2.11/proguard/drawing-app-algoritm_2.11-1.0.jar` to Android project `app/lib` directory and replace the existing jar.

## Licence

We should decide licence for source code ASAP. :)

