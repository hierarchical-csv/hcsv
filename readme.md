# Hierarchical CSV

## General Information

### What is Hierarchical CSV?

Hierarchical CSV is a Java dependency build on [OpenCSV](https://opencsv.sourceforge.net/) _(Starting from version v5.7.1)_ in order to enable developers to parse hierarchical CSV files easily without custom development.

### What are its features?

In addition to all OpenCSV [features](https://opencsv.sourceforge.net/#features), Hierarchical CSV enables you to:

* Parse a CSV file into multiple Java bean types.
* Detect hierarchical relations between bean types.
* Get a ready-to-use Beans corresponding to the whole CSV file with minimum code.

## Contents

* [General Information](#general-information)
  * [What is Hierarchical CSV?](#what-is-hierarchical-csv)
  * [What are its features?](#what-are-its-features)
* [Including Dependency](#including-dependency)
  * [Java Version Requirements](#java-version-requirements)
* [Usage](#usage)
  * [Preparing potential beans](#preparing-potential-beans)
    * [Annotate Class as Bean](#annotate-class-as-bean)
    * [Map CSV columns to Bean attributes](#map-csv-columns-to-bean-attributes)
    * [Parent-Child relation](#parent-child-relation)
  * [Reading a CSV file](#reading-a-csv-file)
    * [Instantiating a reader](#instantiating-a-reader)
* [Reporting Issues](#reporting-issues)
* [Frequently Asked Questions](#frequently-asked-questions)
* [Permanent contributors](#permanent-contributors)
* [License](#license)

## Including Dependency

To use Hierarchical CSV in your project, you should add it as a Maven dependency:

```xml
<dependency>
    <groupId>io.github.hierarchical-csv</groupId>
    <artifactId>hcsv-core</artifactId>
    <version>1.0.0</version>
    <!-- Please refer to Versions section to check available versions -->
</dependency>
```

### Java Version Requirements
| io.github.hierarchical-csv | com.opencsv | Minimum Java Version supported |
|----------------------------|-------------|--------------------------------|
| &lt; 1.0.0                 | &gt;= 5.7.0 | 11                             |


## Usage

This dependency can only be used to read CSV files that have a column used to identify line's type. It also supports only OpenCSV `CsvBindByPosition` strategy.

For example, the first column in the following CSV snippet is used to identify a CSV bean where:

| code | bean |
|------|------|
| U    | User |
| C    | Car  |

In the following CSV snippet, the first line would be converted to a `User` bean. While the two other lines would be converted to `Car` beans:
```csv
U;1;Alex Philippe;1994-09-12;Engineer
C;1;9831;Toyota;0387146661;100 KG
C;1;9747;KIA;19481984;80 KG
```

### Preparing potential beans

### Annotate Class as Bean
Each bean should have the `@HCSVBean` annotation, which has two parameters:
* `codePosition`: the CSV column to be used to identify the bean (starting from zero). _Defaults to zero_.
* `codeValue`: the value of the CSV column used to identify the bean.

Example:
```java
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "0002")
public class AddressLine {
    //...
}
```

Corresponding CSV line would be like:
```csv
0002;5 Av. Anatole France;75007;Paris
```

### Map CSV columns to Bean attributes
Each attribute in the bean should have the `@CsvBindByPosition` provided by the OpenCSV. Knowing that any OpenCSV column type annotation can be used (like the `` annotation or others).

```java
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

import java.util.Date;

@HCSVBean(codePosition = 0, codeValue = "0000")
public class Header {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvDate("yyyy-MM-dd")
    @CsvBindByPosition(position = 1)
    private Date date;
    
    // Getters and setters
    
}
```

Corresponding CSV line would be like:
```csv
0;2023-03-01
```

### Parent-Child relation

The first prerequisite to establish a parent-child relation, is that the parent should have one attribute annotated with `@CsvKey`. The attribute is preferred to be a `String` or implements/overrides the `toString()` method.

The second prerequisite is that the child should have an attribute with the exact same type as its parents `@CsvKey` attribute with the parent's type as parameter, and this attribute should be annotated with `@CsvParentKeyPart`. The `@CsvKey` along with `@CsvParentKeyPart` play the role of Primary-Key and Foreign-Key on a Database approach. This annotation has an `order` attribute that defaults to zero. It helps to decide in case of an N level child (N > 1) the order of the corresponding part in the composition of the parent's key.

The third prerequisite is that the parent should have an attribute depending on child nature:
* Parent has one-and-only-one child of type `T`: the attribute should have the type of the child. The attribute must have the `@CsvChild` annotation.
* Parent has a collection of children of type `T`: the attribute should have a collection type with the generic of `T` (like `List<T>`). The collection must be initialized in the constructor.

**Example 1**: We have parent user (bean: `User`) that could have a child address (bean: `Address`) and a list of devices (bean: `Device`).

```java
//=============================
//          User Bean
//=============================
import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvChildList;
import io.github.hierarchicalcsv.core.annotation.CsvKey;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "01")
public class User {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvKey
    @CsvBindByPosition(position = 1)
    private String userCode;
  
    @CsvBindByPosition(position = 2)
    private String userName;
  
    @CsvChild
    private Address address;
  
    @CsvChildList
    private List<Device> devices;
    
    // Getters and setters
    
}
```

```java
//=============================
//          Address Bean
//=============================
import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvParentKeyPart;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "02")
public class Address {

    @CsvBindByPosition(position = 0, required = true)
    private String code;
  
    @CsvParentKeyPart(User.class)
    @CsvBindByPosition(position = 1)
    private String userCode;
  
    @CsvBindByPosition(position = 2)
    private String country;
  
    @CsvBindByPosition(position = 3)
    private String postalCode;
    
    // Getters and setters
    
}
```

```java
//=============================
//          Device Bean
//=============================
import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvParentKeyPart;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "03")
public class Device {

    @CsvBindByPosition(position = 0, required = true)
    private String code;
  
    @CsvParentKeyPart(User.class)
    @CsvBindByPosition(position = 1)
    private String userCode;
  
    @CsvBindByPosition(position = 2)
    private String type;

    @CsvBindByPosition(position = 3)
    private String model;
    
    // Getters and setters
    
}
```

The corresponding CSV file would be:
```csv
01;00001;Ross Geller
02;00001;Newyork;00818
03;00001;Beeper;Motorolla
03;00001;Watch;Casio
01;01098;Chandler Bing
02;00001;Tulsa;48199
03;00001;Phone;Samsung
```

**Example 2**: We have parent movie (bean: `Movie`) that could have a list of actors (bean: `Actor`) and each actor could have a list of scenes (bean: `Scene`).

```java
//=============================
//          Movie Bean
//=============================
import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvChildList;
import io.github.hierarchicalcsv.core.annotation.CsvKey;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "mov")
public class Movie {

    @CsvBindByPosition(position = 0, required = true)
    private String code;

    @CsvKey
    @CsvBindByPosition(position = 1)
    private String movieName;
  
    @CsvBindByPosition(position = 2)
    private int year;

    @CsvChildList
    private List<Actor> actors;
    
    // Getters and setters
    
}
```

```java
//=============================
//          Actor Bean
//=============================
import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvChildList;
import io.github.hierarchicalcsv.core.annotation.CsvKey;
import io.github.hierarchicalcsv.core.annotation.CsvParentKeyPart;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "act")
public class Address {

    @CsvBindByPosition(position = 0, required = true)
    private String code;
  
    @CsvParentKeyPart(Movie.class)
    @CsvBindByPosition(position = 1)
    private String movieName;

    @CsvKey
    @CsvBindByPosition(position = 2)
    private String actorName;
  
    @CsvBindByPosition(position = 3)
    private int age;
    
    // Getters and setters
    
}
```

```java
//=============================
//          Scene Bean
//=============================
import com.opencsv.bean.CsvBindByPosition;
import io.github.hierarchicalcsv.core.annotation.CsvChildList;
import io.github.hierarchicalcsv.core.annotation.CsvParentKeyPart;
import io.github.hierarchicalcsv.core.annotation.HCSVBean;

@HCSVBean(codePosition = 0, codeValue = "sce")
public class Address {

  @CsvBindByPosition(position = 0, required = true)
  private String code;

  @CsvParentKeyPart(value = Movie.class, order = 0)
  @CsvBindByPosition(position = 1)
  private String movieName;

  @CsvParentKeyPart(value = Actor.class, order = 1)
  @CsvBindByPosition(position = 2)
  private String actorName;

  @CsvBindByPosition(position = 3)
  private int sceneNumber;

  @CsvBindByPosition(position = 4)
  private String location;

  // Getters and setters

}
```

The corresponding CSV file would be:
```csv
mov;Minions;2015
act;Minions;Steve Carrel;60
sce;Minions;Steve Carrel;9149771;Street
sce;Minions;Steve Carrel;3091941;Bathroom
act;Minions;Sandra Bullock;58
sce;Minions;Sandra Bullock;1301939;Roof
```

### Reading a CSV file

For this section, we suppose that you have a valid structure with correctly annotated beans and attributes and an accessible CSV file.

### Instantiating a reader

The only way to create a `HCSVReader` is through the builder class `HCSVReaderBuilder`. The builder has the following methods:

* Hierarchical generic methods and upgraded OpenCSV methods:
  * Constructor `HCSVReaderBuilder(Reader)`: where the `Reader` is any `java.io` reader implementation.
  * Skipping lines `withSkipLines(int)`: number of lines to skip from the beginning of the file.
  * Error locale `withErrorLocale(Locale)`: Locale used in thrown exceptions. Defaults to system's Locale.
  * Exception handler `withExceptionHandler(CsvExceptionHandler)`: Default CSV exception handler (from OpenCSV) if not override in `CsvBeanType` spec.
  * Listener `withListener(CsvLineProcessListener)`: A listener to be executed before and after line parsing.
  * Ignore unknown beans `withIgnoreUnknownBeanType(boolean)`: Whether to ignore a bean if the Reader isn't able to recognise.
  * Beans configuration: tells `HCSVReader` to parse corresponding bean type. Could be done in different ways:
    * `withBeanClass(Class<?>)`: creates and adds to the Reader a minimal `CsvBeanType` for the class type with default configuration.
    * `withBeanClasses(List<Class<?>>)`: creates and adds to the Reader a list of minimal `CsvBeanType` for corresponding class types with default configuration.
    * `withBeanType(CsvBeanType<?>)`: adds to the reader the passed `CsvBeanType`.
    * `withBeanTypes(List<CsvBeanType<?>>)`: adds to the reader the passed list of `CsvBeanType`.
* Generic OpenCSV Methods:
  * CSV Praser **_(mandatory to be called)_** `withCSVParser(ICSVParser)`: [ICSVParser](https://opencsv.sourceforge.net/apidocs/com/opencsv/ICSVParser.html).
  * Carriage return `withKeepCarriageReturn(boolean)`: [CSVReaderBuilder.withKeepCarriageReturn()](https://opencsv.sourceforge.net/apidocs/com/opencsv/CSVReaderBuilder.html#withKeepCarriageReturn-boolean-)
  * Verify reader `withVerifyReader(boolean)`: [CSVReaderBuilder.withVerifyReader()](https://opencsv.sourceforge.net/apidocs/com/opencsv/CSVReaderBuilder.html#withVerifyReader-boolean-)
  * Verify null field `withFieldAsNull(boolean)`: [CSVReaderBuilder.withFieldAsNull()](https://opencsv.sourceforge.net/apidocs/com/opencsv/CSVReaderBuilder.html#withFieldAsNull-com.opencsv.enums.CSVReaderNullFieldIndicator-)
  * Line validator `withLineValidator(LineValidator)`: [CSVReaderBuilder.withLineValidator()](https://opencsv.sourceforge.net/apidocs/com/opencsv/CSVReaderBuilder.html#withLineValidator-com.opencsv.validators.LineValidator-)
  * Line validator `withRowValidator(RowValidator)`: [CSVReaderBuilder.withRowValidator()](https://opencsv.sourceforge.net/apidocs/com/opencsv/CSVReaderBuilder.html#withRowValidator-com.opencsv.validators.RowValidator-)

Code to instantiate a HCSVReader:

Minimal code:
```java
import com.opencsv.CSVParserBuilder;
import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;
import io.github.hierarchicalcsv.core.HCSVReader;
import io.github.hierarchicalcsv.core.HCSVReaderBuilder;
import io.github.hierarchicalcsv.core.model.CsvBeanType;
import io.github.hierarchicalcsv.core.model.CsvLineProcessListener;
import io.github.hierarchicalcsv.coretest.structure.model.correct.*;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class MyClass {

    public List<Object> readAll() {
        try(FileReader fileReader = new FileReader(new File(this.getClass().getResource("/my-file.csv").toURI()));
            HCSVReader hcsvReader = new HCSVReaderBuilder(fileReader)
                    .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                    .withBeanTypes(beansList) // beansList is a list of 'CsvBeanType' Objects
                    .build()) {
            return hcsvReader.readFile();
        }
    }
    
}
```

A full example:
```java
import com.opencsv.CSVParserBuilder;
import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;
import io.github.hierarchicalcsv.core.HCSVReader;
import io.github.hierarchicalcsv.core.HCSVReaderBuilder;
import io.github.hierarchicalcsv.core.model.CsvBeanType;
import io.github.hierarchicalcsv.core.model.CsvLineProcessListener;
import io.github.hierarchicalcsv.coretest.structure.model.correct.*;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class MyClass {
    // considering that we have the beans (in hierarchy):
    //  - HeaderLine
    //  - UserLine
    //      |- ProductLine (collection)
    //      |- AddressLine (collection)
    //          |- RegionLine
    //  - FooterLine
    public void myMethod() {
        // LeadingZerosListener is defined below.
        LeadingZerosListener lineProcessListener = new LeadingZerosListener();
        try(FileReader fileReader = new FileReader(new File(this.getClass().getResource("/my-file.csv").toURI()));
            HCSVReader hcsvReader = new HCSVReaderBuilder(fileReader)
                    .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                    .withBeanType(new CsvBeanType<>(HeaderLine.class))
                    .withBeanClass(UserLine.class)
                    .withBeanClasses(Collections.singletonList(FooterLine.class))
                    .withBeanType(new CsvBeanType<>(RegionLine.class))
                    .withBeanTypes(Collections.singletonList(new CsvBeanType<>(AddressLine.class)))
                    // using OpenCSV CsvToBeanFilter
                    .withBeanType(new CsvBeanType<>(ProductLine.class, line ->
                            line != null && line.length >2 && (line[2].startsWith("A") || line[2].startsWith("B"))))
                    // Unknown lines will be ignored
                    .withIgnoreUnknownBeanType(true)
                    // First two lines would be skipped
                    .withSkipLines(2)
                    // The listener (LeadingZerosListener)
                    .withListener(lineProcessListener)
                    .build()) {
            // List contains all returned beans
            var list = hcsvReader.readFile();
            //...
        }
    }
    //...
    static class LeadingZerosListener implements CsvLineProcessListener {

        protected int afterLineProcessCallCount = 0;
        protected int filteredBeansCallCount = 0;
  
        @Override
        public void beforeLineProcess(long lineNumber, String[] line) {
            if(line != null && line.length > 0) {
                String code = line[0];
                line[0] = code.length() == 4? code: "0".repeat(4 - code.length()) + code;
            }
        }
  
        @Override
        public void afterLineProcess(long lineNumber, Object csvBean, BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue, boolean filteredBean) {
            afterLineProcessCallCount++;
            if(filteredBean) {
                filteredBeansCallCount++;
            }
        }
    }
}
```

## Reporting Issues


Hierarchical CSV uses GitHub’s integrated issue tracking system to record bugs and feature requests. If you want to raise an issue, please follow the recommendations below:

* Before you log a bug, please search the issue tracker to see if someone has already reported the problem.

* If the issue doesn’t already exist, create a new issue.

* Please provide as much information as possible with the issue report. And possibly classes hierarchy and example CSV file.

* If you need to paste code or include a stack trace, use Markdown. ``` escapes before and after your text.

* If possible, try to create a test case or project that replicates the problem and attach it to the issue.

_(Please note that this part is inspired from Spring Boot Github page ^\_^)._

## Frequently Asked Questions

_(Would bee fed when questions are asked)_

## Permanent contributors

* Fawaz Ibrahim ([fawaz-ibrahim](https://github.com/fawaz-ibrahim))

## License
Hierarchical CSV is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).