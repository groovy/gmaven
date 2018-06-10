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

