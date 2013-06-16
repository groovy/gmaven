/*
 * Copyright (c) 2007-2013, the original author or authors.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

//noinspection GroovyAssignabilityCheck
def assertVariable = { value, Class expectedType ->
  // spit out value and class for reference
  println value
  println value?.getClass()

  // require non-null and expected type
  assert value != null
  assert expectedType.isAssignableFrom(value.getClass())

  // we are good
  println 'OK'
}

// basic
assertVariable(project, org.apache.maven.project.MavenProject)
assertVariable(basedir, java.io.File)
assertVariable(properties, java.util.Properties)
assertVariable(ant, groovy.util.AntBuilder)
assertVariable(fail, groovy.lang.Closure)
assertVariable(log, org.slf4j.Logger)

// advanced
assertVariable(container, org.codehaus.gmaven.plugin.util.ContainerHelper)
assertVariable(plugin, org.apache.maven.plugin.descriptor.PluginDescriptor)
assertVariable(pluginContext, java.util.Map)
assertVariable(mojo, org.apache.maven.plugin.MojoExecution)
assertVariable(session, org.apache.maven.execution.MavenSession)
assertVariable(settings, org.apache.maven.settings.Settings)

// all validation passed
println 'ALL OK'
