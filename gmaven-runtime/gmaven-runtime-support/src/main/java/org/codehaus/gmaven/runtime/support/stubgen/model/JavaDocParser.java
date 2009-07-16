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

package org.codehaus.groovy.maven.runtime.support.stubgen.model;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses doclet tags from javadoc.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class JavaDocParser
{
    private static final Pattern JAVADOCS_PATTERN = Pattern.compile("(?s).*/\\*\\*(.*?)\\*/[^\\*/}]*$");

    public JavaDocDef parse(String text) {
        // text may be null

        JavaDocDef def = null;

        if (text != null) {
            Matcher m = JAVADOCS_PATTERN.matcher(text);
            
            if (m.matches()) {
                int lastGroupIndex = m.groupCount();

                if (lastGroupIndex > 0) {
                    text = m.group(lastGroupIndex).trim();
                }

                if (text != null) {
                    def = parseWithQDox(text);
                }
            }
        }

        return def;
    }

    private JavaDocDef parseWithQDox(final String text) {
        assert text != null;

        // Render a synthetic class to parse out the comment and tags
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);

        out.println("/**");
        out.println(text);
        out.println(" */");
        out.println("class Dummy {}");
        out.flush();

        StringReader reader = new StringReader(writer.getBuffer().toString());

        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSource(reader);

        JavaClass[] classes = builder.getClasses();

        JavaDocDef def = new JavaDocDef();

        def.setComment(classes[0].getComment());

        DocletTag[] tags = classes[0].getTags();
        
        if (tags != null) {
            for (int i=0; i<tags.length; i++) {
                TagDef tag = new TagDef();

                tag.setName(tags[i].getName());
                tag.setValue(tags[i].getValue());
                tag.setParameters(tags[i].getParameters());
                tag.setNamedParameters(tags[i].getNamedParameterMap());

                def.addTag(tag);
            }
        }
        
        return def;
    }
}