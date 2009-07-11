/*
 * Copyright (C) 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

//
// $Id$
//

def cl = this.class.classLoader

cl.addURL(new File(basedir, 'target/classes').toURI().toURL())

//
// FIXME: We realy need the test modules class-path to do this correctly...
//
// def type = cl.loadClass('Hello')
//
// type.newInstance().hello()

assert new File(basedir, 'target/classes/Hello.class').exists()

