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

package org.codehaus.groovy.maven.runtime.support.stubgen.render;

import org.codehaus.groovy.maven.runtime.support.stubgen.model.ClassDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ConstructorDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.MethodDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ParameterDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.SourceDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.TypeDef;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides support for {@link RendererFactory} implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class RendererFactorySupport
    implements RendererFactory
{
    public Set create(final SourceDef model) {
        assert model != null;

        Set set = new LinkedHashSet();

        // Generate renderers for classes
        Iterator iter = model.getClasses().iterator();
        while (iter.hasNext()) {
            ClassDef def = (ClassDef)iter.next();

            Renderer renderer = createRenderer(def);
            assert renderer != null;

            set.add(renderer);
        }

        // If the source def contains statements then add a render for the script
        if (model.hasStatements()) {
            Renderer renderer = createRenderer(model);
            assert renderer != null;

            set.add(renderer);
        }

        return set;
    }

    protected abstract Renderer createRenderer(final ClassDef def);

    protected Renderer createRenderer(final SourceDef def) {
        assert def != null;

        ClassDef script = new ClassDef();
        script.setParent(def);
        
        script.getModifiers().add("public");
        script.setName(def.getScriptName());
        script.setSuperClass(new TypeDef("groovy.lang.Script"));

        ConstructorDef ctor;

        ctor = new ConstructorDef();
        ctor.getModifiers().add("public");
        script.addConstructor(ctor);

        ctor = new ConstructorDef();
        ctor.getModifiers().add("public");
        ctor.addParameter(new ParameterDef("groovy.lang.Binding", "context"));
        script.addConstructor(ctor);

        MethodDef method;

        method = new MethodDef();
        method.getModifiers().add("public").add("static").add("transient");
        method.setReturns(new TypeDef("void"));
        method.setName("main");
        method.addParameter(new ParameterDef(new TypeDef("java.lang.String", 1), "args"));
        script.addMethod(method);

        method = new MethodDef();
        method.getModifiers().add("public");
        method.setReturns(new TypeDef("void"));
        method.setName("run");

        return createRenderer(script);
    }
}