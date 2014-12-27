A collection of useful Java utilities / code snippets.

Building with Gradle
====================
There is no Maven support yet, so you would have to clone this repo and build manually.

1. Install [gradle](http://www.gradle.org). Should work with any version >= 2.0.
2. Build and test:
```shell
$ gradle build
```
3. Build docs:
```shell
$ gradle javadoc
```
4. The libs and docs are found at `build/libs` and `build/docs/javadoc` respectively.

Importing to Eclipse
====================
1. Install the Maven plugin for Eclipse.
2. Import the Eclipse `.project` file.

Packages
========
Non-exhaustive list of package descriptions:

- j.algo :
    - UnionFind


- j.collections:
    - Queue and stack based on efficient circular array.
    - Ordered pair (2-tuple)


- j.io :
    - IO streams and utility classes
    - line and column number text file reader.
    - HTTP chunked input/ouput stream.
    - FileUtil: read entire text files


- j.opt :
    - Command line parsing library based on Java annotations.
    - Powerful and easy to use and configure.
    - See https://github.com/lucastan/proxyServer/blob/master/src/main/j/net/proxy/ProxyServer.java
    for a usage example.


- j.util :
    - Clipboard utilities
    - Shuffle native arrays

