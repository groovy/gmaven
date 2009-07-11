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

package org.codehaus.groovy.maven.runtime.v1_5;

import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.support.FeatureSupport;
import org.codehaus.groovy.maven.runtime.StubCompiler;
import org.codehaus.groovy.maven.runtime.support.CompilerSupport;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ModelFactory;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.SourceDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.SourceType;
import org.codehaus.groovy.maven.runtime.support.stubgen.render.Renderer;
import org.codehaus.groovy.maven.runtime.support.stubgen.render.RendererFactory;
import org.codehaus.groovy.maven.runtime.v1_5.stubgen.ModelFactoryImpl;
import org.codehaus.groovy.maven.runtime.v1_5.stubgen.RendererFactoryImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

/**
 * Provides the stub compilation feature.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class StubCompilerFeature
    extends FeatureSupport
{
    public StubCompilerFeature() {
        super(StubCompiler.KEY);
    }

    protected Component doCreate() throws Exception {
        return new StubCompilerImpl();
    }

    //
    // StubCompilerImpl
    //

    private class StubCompilerImpl
        extends CompilerSupport
        implements StubCompiler
    {
        private ModelFactory modelFactory = new ModelFactoryImpl();

        private RendererFactory rendererFactory = new RendererFactoryImpl();

        private StubCompilerImpl() throws Exception {
            super(StubCompilerFeature.this);
        }

        public int compile() throws Exception {
            if (sources.isEmpty()) {
                log.debug("No sources added to compile; skipping");

                return 0;
            }

            log.debug("Compiling {} stubs for source(s)", String.valueOf(sources.size()));

            int count = 0;

            for (Iterator iter = sources.iterator(); iter.hasNext();) {
                URL url = (URL) iter.next();
                log.debug("    {}", url);

                count += render(url);
            }

            log.debug("Compiled {} stubs", String.valueOf(count));

            return count;
        }

        private int render(final URL url) throws Exception {
            assert url != null;
            
            SourceDef model = modelFactory.create(url);

            Set renderers = rendererFactory.create(model);

            Iterator iter = renderers.iterator();

            int count = 0;

            while (iter.hasNext()) {
                Renderer renderer = (Renderer)iter.next();

                Writer writer = createWriter(renderer, getTargetDirectory());

                try {
                    renderer.render(writer);
                    count ++;
                }
                finally {
                    writer.close();
                }
            }

            return count;
        }

        private PrintWriter createWriter(final Renderer renderer, final File outputDir) throws IOException {
            assert renderer != null;
            assert outputDir != null;

            StringBuffer buff = new StringBuffer();

            String pkg = renderer.getPackage();

            if (pkg != null) {
                buff.append(pkg.replace('.', '/'));
                buff.append("/");
            }

            buff.append(renderer.getName());
            buff.append(SourceType.JAVA_EXT);

            File outputFile = new File(outputDir, buff.toString());

            outputFile.getParentFile().mkdirs();

            return new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
        }
    }
}