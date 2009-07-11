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

package org.codehaus.groovy.maven.runtime.v1_6.stubgen;

import foo
import foo.bar
import foo.bar.*
import static foo.bar.baz
import a as b
import foo.bar.baz.ick as fooy

/**
 * Javadoc for interface.
 */
interface SimpleInterface
    extends Foo, Bar, Baz
{
    def prop1

    /**
     * Javadoc for test() method.
     */
    void test(final int a, long b, float c);

    /**
     * Javadoc for test2() method.
     */
    def test2(foo, bar, baz) throws a.b.c, d, e;
}