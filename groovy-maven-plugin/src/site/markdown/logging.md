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
# Logging

## Apache Maven 3.0+

GMaven uses SLF4J and uses the [Gossip](https://github.com/jdillon/gossip) provider to allow for simple and
flexible configuration of logging output.

When running in Apache Maven 3.0.x without any SLF4J customizations, additional GMaven logging can be enabled
by setting the `gmaven.logging` property on the `mvn` command-line.

To enable `DEBUG` logging:

    mvn -Dgmaven.logging=DEBUG

To enable `TRACE` logging:

    mvn -Dgmaven.logging=TRACE

## Apache Maven 3.1+

Apache Maven 3.1.x provides SLF4J bindings [slf4j-simple](http://www.slf4j.org/apidocs/org/slf4j/impl/SimpleLogger.html)
by default and that binding implementation must be configured to enable more verbose logging details.

To enable logging configuration as described for Apache Maven 3.0+ the 3.1+ distribution needs to be
augmented to replace the default SLF4J binding with Gossip.
