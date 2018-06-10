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
# Classpath

When executing a goal with a Maven project, GMaven can provide access to the classpath of the project for a
particular scope.  This is controlled by the `classpathScope` parameter.

For example, to execute a script and include the `runtime` scope dependencencies on the classpath:

    <plugin>
      <groupId>org.codehaus.gmaven</groupId>
      <artifactId>groovy-maven-plugin</artifactId>
      <executions>
        <execution>
          <phase>process-classes</phase>
          <goals>
              <goal>execute</goal>
          </goals>
          <configuration>
            <classpathScope>runtime</classpathScope>
            <source>
              // this script has access to the compiled classes and all depencencies of scope=runtime.
            </source>
          </configuration>
        </execution>
      </executions>
    </plugin>

By default `classpathScope` is set to `none` which means that no additional classpath elements will be available.

When executing a goal directly without a project, the scope may be configured by setting the `scope` property:

    mvn groovy:shell -Dscope=test

For all supported values and additional documentation see
[ClasspathScope](apidocs/org/codehaus/gmaven/plugin/ClasspathScope.html).

Any additional classpath which may be needed can also be configured on the plugin definition:

    <plugin>
      <groupId>org.codehaus.gmaven</groupId>
      <artifactId>groovy-maven-plugin</artifactId>
      <dependencies>
        <dependency>
          <groupId>commons-lang</groupId>
          <artifactId>commons-lang</artifactId>
          <version>2.6</version>
        </dependency>
      </dependencies>
    </plugin>
