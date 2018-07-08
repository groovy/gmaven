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
# Groovy Maven Plugin

See _USAGE_ content for more details.

### Hard Requirements

The Maven and Groovy version requirements are ***hard*** requirements.

When goals are executing the versions of Maven and Groovy are detected.
If they are not compatible the goals will fail with an error.

### Customizing Groovy Version

To customize the version of Groovy the plugin will use, override the `org.codehaus.groovy:groovy-all` dependency
on the plugin definition in the project.

For example to use Groovy 2.0.6 instead of the default:

    <plugin>
      <groupId>org.codehaus.gmaven</groupId>
      <artifactId>groovy-maven-plugin</artifactId>
      <dependencies>
        <dependency>
          <groupId>org.codehaus.groovy</groupId>
          <artifactId>groovy-all</artifactId>
          <version>2.0.6</version>
        </dependency>
      </dependencies>
    </plugin>

There is currently no way to change the Groovy version without a project.
Direct execution of goals will always use the default Groovy version if no project is available.  When a project
is available, then `pluginManagement` can be used to configure direct goal execution.
