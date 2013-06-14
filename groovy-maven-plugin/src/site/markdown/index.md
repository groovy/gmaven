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

### System.exit Forbidden

Use of `java.lang.System.exit()`, `java.lang.Runtime.exit()` or `java.lang.Runtime.halt()` is ***forbidden***
from use by a script execution or evaluation in console or shells.
