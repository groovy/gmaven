<!--

    Copyright (c) 2006-present the original author or authors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
# Scriptpath

The `scriptpath` is a path where GMaven will search for additional Groovy sources to resolve when executing a Groovy script.

This configuration applies to all goals.  This means that you can access your external script via the `console`
and `shell` goals as well as `execute`.

### Explicit

For example if you have a class named `Helper` defined in a file named `${project.basedir}/src/main/script/Helper.groovy`
your scripts can use that class if you configure the script path to include `${project.basedir}/src/main/script`:

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
            <scriptpath>
              <path>${project.basedir}/src/main/script</path>
            </scriptpath>
            <source>
              import Helper
              def h = new Helper()
            </source>
          </configuration>
        </execution>
      </executions>
    </plugin>

### Peer

Additionally if using a file-based source configuration, GMaven will automatically look for
matching classes _nexus to_ the source file.

So if you had a `${project.basedir}/src/main/script/Main.groovy` with something like:

    import Helper
    def h = new Helper()

... then this configuration would be sufficent to allow the source file script to access to
`${project.basedir}/src/main/script/Helper.groovy` script:

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
            <source>${project.basedir}/src/main/script/Main.groovy</source>
          </configuration>
        </execution>
      </executions>
    </plugin>

### Command-line

Configure the `scriptpath` property on the mvn command-line with a _comma seperated_ list of entries:

    mvn -Dscriptpath=dir1,dir2,dir3
