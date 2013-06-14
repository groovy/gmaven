<!--

    Copyright (c) 2007-2013, the original author or authors.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
# Script Execution

Groovy script execution is the main purpose of GMaven and is available with the [execute](execute-mojo.html) goal.
A script can be executed as part of a build (attached to a lifecycle phase) or directly via the command-line.

This goal is not bound to a [lifecycle phase](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Lifecycle_Reference)
by default, so the phase must always be configured, and the phase will depend highly on what the purpose is that you are
executing a script for.

All context [variables](variables.html) are availble for use in executed scripts.

## Source

The configuration of the _source_ of the script to execute can be configured to be one of:

* File
* URL
* Inline

This value can be specified by the `source` configuration parameter or by the `source` property.

### File

Files can be used, and for most complex usage recommend, as the source of the script to execute:

    <plugin>
      <groupId>org.codehaus.gmaven</groupId>
      <artifactId>groovy-maven-plugin</artifactId>
        <executions>
          <execution>
            <phase>generate-resources</phase>
            <goals>
              <goal>execute</goal>
            </goals>
            <configuration>
              <source>${project.basedir}/src/main/script/myscript.groovy</source>
            </configuration>
          </execution>
      </executions>
    </plugin>

or:

    mvn groovy:execute -Dsource=src/main/script/myscript.groovy

This form only works when the file actually _exists_ and if the file is missing it could cause
GMaven to treat the source as _inline_ instead.

### URL

URL sources allow the script to be resolved by a URL, which can be any URL which the JVM can resolve.

    <plugin>
      <groupId>org.codehaus.gmaven</groupId>
      <artifactId>groovy-maven-plugin</artifactId>
        <executions>
          <execution>
            <phase>generate-resources</phase>
            <goals>
              <goal>execute</goal>
            </goals>
            <configuration>
              <source>http://mydomain.com/myscript.groovy</source>
            </configuration>
          </execution>
      </executions>
    </plugin>

or:

    mvn groovy:execute -Dsource=http://mydomain.com/myscript.groovy

### Inline

Inline allows scripts to be defined inside of project files.

    <plugin>
      <groupId>org.codehaus.gmaven</groupId>
      <artifactId>groovy-maven-plugin</artifactId>
        <executions>
          <execution>
            <phase>generate-resources</phase>
            <goals>
              <goal>execute</goal>
            </goals>
            <configuration>
              <source>
                println 'Hello'
              </source>
            </configuration>
          </execution>
      </executions>
    </plugin>

or:

    mvn groovy:execute -Dsource="println 'Hello'"

#### Maven interpolation and GStrings

While this form may be the simplest to use for many cases, there are some pitfalls to be aware of.  Specifically
use of [GStrings](http://groovy.codehaus.org/Strings+and+GString) (special String-like constructs available to Groovy),
can cause some unexpected behavior due to Maven interpolation syntax `${}` overloading the GString syntax.

When Maven loads a project, it very early on, interpolates the content of the POM to replace properties and simple
expressions with string values.  This is very common and very handy, but it does provide some complication when
Maven attempts to resolve an expression which was expected to be resolved by a GString.

Scripts which make heavy use of GStrings should seriously consider using a File-based source configuration to
avoid this potential complication.

## Properties and Defaults

Additional customization of the execution variable `properties` are available for scripts run with `execute`.

Script executions can have _overriding_ properties by setting the `properties` configuration parameter.
Values set here will take precendence over any other property definition (user, system, project, defaults, etc).

    <plugin>
      <groupId>org.codehaus.gmaven</groupId>
      <artifactId>groovy-maven-plugin</artifactId>
        <executions>
          <execution>
            <phase>generate-resources</phase>
            <goals>
              <goal>execute</goal>
            </goals>
            <configuration>
              <properties>
                <name>Xenu</name>
              </properties>
              <source>
                println 'Hello ' + properties['name']
              </source>
            </configuration>
          </execution>
      </executions>
    </plugin>

This example will always print `Hello Xenu` even if `mvn -Dname=Steve` or if the project
defined a property of the same name.

Script executions can also have _default_ properties by setting the `defaults` configuration parameter.
Values set here will only be used if no other property for the same nme definition exists.

    <plugin>
      <groupId>org.codehaus.gmaven</groupId>
      <artifactId>groovy-maven-plugin</artifactId>
      <executions>
        <execution>
          <phase>generate-resources</phase>
          <goals>
            <goal>execute</goal>
          </goals>
          <configuration>
            <defaults>
              <name>Xenu</name>
            </defaults>
            <source>
              println 'Hello ' + properties['name']
            </source>
          </configuration>
        </execution>
      </executions>
    </plugin>

Here, with out any other definition of `name`, will print `Hello Xenu`, but if instead was invoked with
`mvn -Dname=Jason` would print `Hello Jason` instead.
