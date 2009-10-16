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

def ant = new AntBuilder()

def props = new Properties()
props.load(new File(basedir, 'invoker.properties').newInputStream())
def version = props['project.version']

def target = new File(localRepositoryPath, "org/codehaus/gmaven/examples/it/install-1/${version}/install-1-${version}.jar")
ant.delete(file: target)
return true
