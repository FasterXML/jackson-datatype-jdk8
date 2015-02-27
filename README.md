Project for [Jackson](https://github.com/FasterXML/jackson) module (jar)
that adds supports for JDK datatypes included in version 8 which can not be directly
supported by core databind due to baseline being JDK 6, excluding following:


* New Date types are supported by [JSR-310 module](../../../jackson-datatype-jsr310)
* Support for parameter names is via [Parameter Names](../../../jackson-module-parameter-names) module

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-datatype-jdk8.svg)](https://travis-ci.org/FasterXML/jackson-datatype-jdk8)

First public version is 2.4.3: module is still somewhat experimental but planned to be stable with 2.5.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jdk8</artifactId>
  <version>2.4.3</version>
</dependency>    
```

(or whatever version is most up-to-date at the moment)

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new Jdk8Module());
```

after which functionality is available for all normal Jackson operations:
you can read JSON into supported JDK8 types, as well as write values of such types as JSON, so that for example:

```java
// TODO: real example
Optional<String> str = ...;
String json = mapper.writeValueAsString(str);
```

## More

See [Wiki](../../wiki) for more information (javadocs, downloads).
