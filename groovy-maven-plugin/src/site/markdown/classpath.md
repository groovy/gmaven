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