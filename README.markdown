IMPORTANT NOTE: Please note that I am no longer maintaining plugboard - I decided early in 2012 that my time
was better spent helping out with compojure-rest, which we have now renamed to 'liberator'.

Liberator can be found here: http://clojure-liberator.github.com

# plugboard
by Malcolm Sparks

## Introduction

Plugboard is a library that sits above Compojure and helps web applications comply fully with the HTTP standard. For a better
description of what this means, please [see here](http://webmachine.basho.com/diagram.html).

## Building

To run the demos you will first need to build plugboard using
Maven. Previous releases used leiningen but unfortunately leiningen
has many incompatibilities with advanced Maven configurations (for
example, it doesn't yet honor the M2_HOME environment variable, nor
does it cope well with individual settings.xml files. We will
reinstate support for leiningen as and when we feel it is more
mature. Fortunately, much of the simplicity of leiningen can be
acheived by using Maven in a certain way).

First, run mvn 'mvn dependency:copy-dependencies' to pull in all the required dependencies. (This is done in preference to using clojure:run. At the time of writing, clojure:run doesn't work particularly well. Firstly, it doesn't work unless mvn package has already been called. Secondly, it runs off Clojure source files that have been copied into the target/ directory, which means often you thinking you're making a change to a source file and you're not).

console> mvn dependency:copy-dependencies

## Running the demos

The demos can be run by the following command :-

console> rundemos

(On Unix you run ./rundemos).

Now open a browser at http://localhost:8082

## Understanding the demo code

The demo code is in the test directory. Start with the main.clj file which is located here :-

test/plugboard/demos/main.clj

Plugboard allows you to create a ring handler that returns a complete HTTP response. The response generator (get-response) needs
only the initial request and a 'plugboard'. You create a single plugboard by composing various plugboards together with the
(merge-plugboards) function.

## How it works

Most simple web frameworks and libraries ask the developer to determine the HTTP response status and headers along with the entity
body. The result is that most web applications do not fully implement their responsbilities from the HTTP specification (RFC 2616).

Plugboard is different. Plugboard has a built-in state machine which determines the HTTP status and response headers on behalf of
the application. The developer can then concentrate on the response body.

Of course, sometimes a developer needs to control the response headers. For example, a developer may wish to restrict part of the
website to authorized users. In this case it is possible for the developer to override the decisions that are made in the state
machine by 'plugging in' decision functions at any point.

It turns out that this is very useful. It means that web applications can add many features by layering a set of plugboard
overlays. The overall effect is to raise the level of abstraction above the HTTP protocol itself while at the same time delegating
responsibility for adhering to the HTTP standard to the library itself.

## License

Plugboard is free software and is licensed under the GNU Affero General Public License (AGPL) version 3. This license agreement can
be found in the LICENSE file.

## Feedback

I hope you like plugboard and find it useful for your own applications. Plugboard is a 'work in progress' and needs your help to
evolve and mature. Please feel free to write to me at malcolm@congreve.com with any comments, ideas and suggestions you may have.

Malcolm Sparks
Plugboard author and maintainer.
