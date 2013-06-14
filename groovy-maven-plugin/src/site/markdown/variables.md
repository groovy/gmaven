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
# Context Variables

All goals which invoke the Groovy runtime, expose a set of predefined context (aka binding) variables.

## Basic

<table>
  <tr>
    <td>project</td>
    <td>
      The currently executing <a href="http://maven.apache.org/ref/3.0.5/maven-core/apidocs/org/apache/maven/project/MavenProject.html">MavenProject</a>
      or if no project, this will be a reference to the generic stub project.
    </td>
  </tr>

  <tr>
    <td>basedir</td>
    <td>
      <code>java.io.File</code> reference to detected base directory for Maven execution.
    </td>
  </tr>

  <tr>
    <td>properties</td>
    <td>
      <code>java.util.Map</code> containing merged execution properties.
      <strong>NOT</strong> a <code>java.util.Properties</code> instance.
    </td>
  </tr>

  <tr>
    <td>ant</td>
    <td>
      Preconfigured <a href="http://groovy.codehaus.org/gapi/groovy/util/AntBuilder.html">AntBuilder</a>.
    </td>
  </tr>

  <tr>
    <td>fail</td>
    <td>
      Closure to help fail execution.  See <a href="apidocs/org/codehaus/gmaven/plugin/FailClosureTarget.html">FailClosureTarget</a>
      for supported syntax and usage.
    </td>
  </tr>

  <tr>
    <td>log</td>
    <td>
      Slf4j <a href="http://www.slf4j.org/apidocs/org/slf4j/Logger.html">Logger</a>.
    </td>
  </tr>
</table>

## Advanced

<table>
  <tr>
    <td>container</td>
    <td>
      <a href="apidocs/org/codehaus/gmaven/plugin/util/ContainerHelper.html">ContainerHelper</a>
      to simplify looking up Maven components.
    </td>
  </tr>

  <tr>
    <td>plugin</td>
    <td>
      The <code>groovy-maven-plugin</code> <a href="http://maven.apache.org/ref/3.0.5/maven-plugin-api/apidocs/org/apache/maven/plugin/descriptor/PluginDescriptor.html">PluginDescriptor</a>.
    </td>
  </tr>

  <tr>
    <td>pluginContext</td>
    <td>
      Plugin context <code>java.util.Map</code> to allow communication between plugin goal executions.
      See <a href="http://maven.apache.org/ref/3.0.5/maven-plugin-api/apidocs/org/apache/maven/plugin/ContextEnabled.html">ContextEnabled</a> for more details.
    </td>
  </tr>

  <tr>
    <td>mojo</td>
    <td>
      The currently executing <a href="http://maven.apache.org/ref/3.0.5/maven-core/apidocs/org/apache/maven/plugin/MojoExecution.html">MojoExecution</a>.
    </td>
  </tr>

  <tr>
    <td>session</td>
    <td>
      The currently executing <a href="http://maven.apache.org/ref/3.0.5/maven-core/apidocs/org/apache/maven/execution/MavenSession.html">MavenSession</a>.
    </td>
  </tr>

  <tr>
    <td>settings</td>
    <td>
      The current executions <a href="http://maven.apache.org/ref/3.0.5/maven-settings/apidocs/org/apache/maven/settings/Settings.html">Settings</a>.
    </td>
  </tr>
</table>
