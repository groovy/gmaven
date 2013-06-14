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
# GMaven Adapter API

Provides an abstraction used by [groovy-maven-plugin](../groovy-maven-plugin/index.html) to
interact with the Groovy runtime.

## Components

### GroovyRuntime

Factory, discovered via `java.util.ServiceLoader`, which provides access to feature components.

### ScriptExecutor

Provides script execution functionality.

### ConsoleWindow

Provides the ability to open the GUI Groovy console window.

### ShellRunner

Provides the ability to run the command-line Groovy shell (aka `groovysh`).

