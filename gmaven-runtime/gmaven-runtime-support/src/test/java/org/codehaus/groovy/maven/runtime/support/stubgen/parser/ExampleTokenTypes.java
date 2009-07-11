/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.codehaus.groovy.maven.runtime.support.stubgen.parser;

/**
 * Helper to test {@link org.codehaus.groovy.maven.runtime.support.stubgen.parser.DynamicTokens}.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface ExampleTokenTypes
{
    int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int BLOCK = 4;
	int MODIFIERS = 5;
	int OBJBLOCK = 6;
	int SLIST = 7;
	int METHOD_DEF = 8;
	int VARIABLE_DEF = 9;
	int INSTANCE_INIT = 10;
	int STATIC_INIT = 11;
	int TYPE = 12;
	int CLASS_DEF = 13;
	int INTERFACE_DEF = 14;
	int PACKAGE_DEF = 15;
	int ARRAY_DECLARATOR = 16;
	int EXTENDS_CLAUSE = 17;
	int IMPLEMENTS_CLAUSE = 18;
	int PARAMETERS = 19;
	int PARAMETER_DEF = 20;

    String NOT_A_TOKEN = "foo";
}