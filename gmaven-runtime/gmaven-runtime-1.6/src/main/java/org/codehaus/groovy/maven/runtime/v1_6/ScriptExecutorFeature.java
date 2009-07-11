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

package org.codehaus.groovy.maven.runtime.v1_6;

import groovy.lang.Closure;
import groovy.util.AntBuilder;
import org.apache.tools.ant.BuildLogger;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.ComponentException;
import org.codehaus.groovy.maven.feature.support.FeatureSupport;
import org.codehaus.groovy.maven.runtime.ClassFactory;
import org.codehaus.groovy.maven.runtime.ScriptExecutor;
import org.codehaus.groovy.maven.runtime.support.ScriptExecutorSupport;
import org.codehaus.groovy.maven.runtime.util.Callable;
import org.codehaus.groovy.maven.runtime.util.MagicAttribute;

/**
 * Provides the script execution feature.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ScriptExecutorFeature
    extends FeatureSupport
{
    public ScriptExecutorFeature() {
        super(ScriptExecutor.KEY);
    }

    protected Component doCreate() throws Exception {
        return new ScriptExecutorImpl();
    }

    //
    // ScriptExecutorImpl
    //

    private class ScriptExecutorImpl
        extends ScriptExecutorSupport
    {
        private ScriptExecutorImpl() throws Exception {
            super(ScriptExecutorFeature.this);
        }

        protected ClassFactory getClassFactory() {
            try {
                return (ClassFactory) provider().feature(ClassFactory.KEY).create(config());
            }
            catch (Exception e) {
                throw new ComponentException(e);
            }
        }

        protected Object createClosure(final Callable target) {
            assert target != null;
            
            return new Closure(this) {
                public Object call(final Object[] args) {
                    try {
                        return target.call(args);
                    }
                    catch (Exception e) {
                        return throwRuntimeException(e);
                    }
                }
            };
        }

        private AntBuilder createAntBuilder() {
            AntBuilder ant = new AntBuilder();

            Object obj = ant.getAntProject().getBuildListeners().elementAt(0);

            if (obj instanceof BuildLogger) {
                BuildLogger logger = (BuildLogger)obj;

                logger.setEmacsMode(true);
            }

            return ant;
        }

        protected Object createMagicAttribute(final MagicAttribute attr) {
            assert attr != null;

            if (attr == MagicAttribute.ANT_BUILDER) {
                return createAntBuilder();
            }

            throw new ComponentException("Unknown magic attribute: " + attr);
        }
    }
}