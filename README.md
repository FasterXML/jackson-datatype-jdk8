Project for [Jackson](https://github.com/FasterXML/jackson) module (jar)
that adds supports for JDK datatypes included in version 8 which can not be directly
supported by core databind due to baseline being JDK 6.

## Status

[![Build Status](https://fasterxml.ci.cloudbees.com/job/jackson-datatype-jdk8-master/badge/icon)](https://fasterxml.ci.cloudbees.com/job/jackson-datatype-jdk8-master/)

This is a new module; first official version is not yet released!

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jdk8</artifactId>
  <version>2.4.0</version>
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
