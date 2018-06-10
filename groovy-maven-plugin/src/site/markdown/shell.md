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
# Shell

GMaven supports running the command-line [Groovy Shell](http://groovy.codehaus.org/Groovy+Shell)
(aka `groovysh`) with the [shell](shell-mojo.html) goal:

    mvn groovy:shell

This goal works with and without a project.  When a project is available additional [classpath](classpath.html)
configuration options are avaiable.

All context [variables](variables.html) are availble for use in the shell.
