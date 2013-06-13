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
# GMaven

<img src="http://media.xircles.codehaus.org/_projects/gmaven/_logos/large.png" style="float: right;"/>

[Groovy](http://groovy.codehaus.org) integration for [Apache Maven](http://maven.apache.org).

## Supported Versions

* Grovoy 2.x
* Apache Maven 3.x
* Apache Maven 3.1.x

## Features

* Script execution
* GUI console access
* Command-line shell access

For more details please see the [groovy-maven-plugin](groovy-maven-plugin/index.html) documentation.

## Compilation

GMaven 2.x no longer supports any integration for compilation of Groovy sources.  There were too many problems with
stub-generation and hooking up compliation to the proper Maven lifecycle phases to effectivly support.

For compliation support please see the
[Groovy Eclipse Compiler](http://docs.codehaus.org/display/GROOVY/Groovy-Eclipse+compiler+plugin+for+Maven)
or use the [Groovy ant tasks](http://groovy.codehaus.org/Compiling+With+Maven2).
