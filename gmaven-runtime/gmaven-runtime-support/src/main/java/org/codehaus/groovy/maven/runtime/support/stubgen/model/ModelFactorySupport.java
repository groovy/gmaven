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

import org.codehaus.groovy.maven.runtime.support.stubgen.UnexpectedNodeException;
import org.codehaus.groovy.maven.runtime.support.stubgen.UnsupportedFeatureException;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.Node;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.Parser;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.ParserFactory;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.SourceType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides support for {@link ModelFactory} implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class ModelFactorySupport
    implements ModelFactory
{
    protected final ParserFactory factory;

    protected Parser parser;

    protected Node lastNode;

    protected SourceDef source;

    protected ClassDef clazz;

    protected ModelFactorySupport(final ParserFactory factory) {
        assert factory != null;

        this.factory = factory;
    }

    public SourceDef create(final URL input) throws Exception {
        assert input != null;

        // Setup the root model element
        source = createRoot(input);

        // Reset internal state
        lastNode = null;
        clazz = null;

        // Create a new parser for the source type
        parser = factory.create(source.getType());

        // Parse the source
        Reader reader = new BufferedReader(new InputStreamReader(input.openStream()));
        Node node;

        try {
            node = parser.parse(reader, input.toExternalForm());
        }
        finally {
            reader.close();
        }

        // Process the tree
        process(node);

        return source;
    }

    protected SourceDef createRoot(final URL input) {
        assert input != null;

        SourceDef def = new SourceDef();
        def.setUrl(input);

        SourceType type = SourceType.forURL(input);
        def.setType(type);

        addDefaultImports(def);

        return def;
    }

    protected abstract void addDefaultImports(final SourceDef model);

    //
    // Processing
    //

    protected void process(Node node) {
        assert node != null;

        while (node != null) {
            if (node.is("PACKAGE_DEF")) {
                packageDef(node);
            }
            else if (node.is(new String[] { "STATIC_IMPORT", "IMPORT" })) {
                importDef(node);
            }
            else if (node.is("CLASS_DEF")) {
                classDef(node);
            }
            else if (node.is("INTERFACE_DEF")) {
                interfaceDef(node);
            }
            else if (node.is("METHOD_DEF")) {
                methodDef(node);
            }
            else if (node.is("ENUM_DEF")) {
                enumDef(node);
            }
            else if (node.is("ANNOTATION_DEF")) {
                annotationDef(node);
            }
            else {
                // everything else should be some sort of statement
                source.addStatement(node);
            }

            node = node.nextSibling();
        }
    }

    protected void packageDef(final Node parent) {
        assert parent != null;

        PackageDef def = new PackageDef();

        Node node = parent.firstChild();

        node = node.skip("ANNOTATIONS");

        String name = qualifiedName(node);

        def.setName(name);

        source.setPackage(def);
    }

    protected void importDef(final Node parent) {
        assert parent != null;

        ImportDef def = new ImportDef();

        if (parent.is("STATIC_IMPORT")) {
            // import is like "import static foo"
            def.setStatic(true);
        }

        Node node = parent.firstChild();

        if (node.is("LITERAL_as")) {
            // import is like "import foo as bar"
            node = node.firstChild();
            Node aliasNode = node.nextSibling();
            def.setAlias(identifier(aliasNode));
        }

        if (node.isLeaf()) {
            // import is like "import Foo"
            def.setType(identifier(node));
        }
        else {
            Node packageNode = node.firstChild();
            String packageName = qualifiedName(packageNode);
            def.setPackage(packageName);

            Node nameNode = packageNode.nextSibling();
            if (nameNode.is("STAR")) {
                // import is like "import foo.*"
                def.setWildcard(true);
            }
            else {
                String name = identifier(nameNode);
                // import is like "import foo.Bar"
                def.setType(name);
            }
        }

        source.addImport(def);
    }

    protected void interfaceDef(final Node parent) {
        assert parent != null;

        InterfaceDef def = new InterfaceDef();

        clazz = def;

        Node node = parent.firstChild();

        node = modifiers(def, node);

        node = name(def, node);

        if (node.is("EXTENDS_CLAUSE")) {
            def.getImplements().addAll(interfaces(node));
            node = node.nextSibling();
        }

        javadocs(def, parent);

        objectBlock(node);

        source.addClass(def);
    }

    protected void classDef(final Node parent) {
        assert parent != null;

        ClassDef def = new ClassDef();
        def.addImplements("groovy.lang.GroovyObject");

        clazz = def;

        Node node = parent.firstChild();

        node = modifiers(def, node);

        node = name(def, node);

        if (node.is("TYPE_PARAMETERS")) {
            //
            // FIXME: Support generics
            //

            node = node.nextSibling();
        }

        if (node.is("EXTENDS_CLAUSE")) {
            def.setSuperClass(type(node));
            node = node.nextSibling();
        }

        if (node.is("IMPLEMENTS_CLAUSE")) {
            def.getImplements().addAll(interfaces(node));
            node = node.nextSibling();
        }

        javadocs(def, parent);

        objectBlock(node);

        source.addClass(def);
    }

    protected void enumDef(final Node parent) {
        //
        // FIXME: Implement enum support
        //
        throw new UnsupportedFeatureException("enum");
    }

    protected void annotationDef(final Node parent) {
        //
        // FIXME: Implement annotation support
        //
        throw new UnsupportedFeatureException("annotation");
    }

    protected void objectBlock(final Node parent) {
        assert parent != null;

        parent.ensure("OBJBLOCK");

        for (Node node = parent.firstChild(); node != null; node = node.nextSibling()) {
            if (node.is("OBJBLOCK")) {
                objectBlock(node);
            }
            else if (node.is("METHOD_DEF")) {
                methodDef(node);
            }
            else if (node.is("CTOR_IDENT")) {
                constructorDef(node);
            }
            else if (node.is("VARIABLE_DEF")) {
                fieldDef(node);
            }
            else if (node.is(new String[] { "ENUM_DEF", "ENUM_CONSTANT_DEF" })) {
                enumDef(node);
            }
            else if (node.is(new String[] { "STATIC_INIT", "INSTANCE_INIT" })) {
                // Ignore
            }
            else {
                throw new UnexpectedNodeException(node);
            }
        }
    }

    protected void constructorDef(final Node parent) {
        assert parent != null;

        ConstructorDef def = new ConstructorDef();

        Node node = parent.firstChild();

        node = modifiers(def, node);

        node = parameters(def, node);

        node = throwz(def, node);

        superParameters(def, node);

        javadocs(def, parent);

        clazz.addConstructor(def);
    }

    protected void superParameters(final ConstructorDef target, final Node parent) {
        assert target != null;
        assert parent != null;

        Node node = parent;

        if (node.is("SLIST")) {
            node = node.firstChild();

            if (node != null) {
                if (node.is(new String[] { "SUPER_CTOR_CALL", "CTOR_CALL" })) {
                    if (node.is("SUPER_CTOR_CALL")) {
                        target.setSuperType(ConstructorDef.SUPER);
                    }
                    else {
                        target.setSuperType(ConstructorDef.THIS);
                    }

                    node = node.firstChild();
                    node.ensure("ELIST");

                    if (!node.isLeaf()) {
                        node = node.firstChild();

                        // Pull off EXPR siblings
                        do {
                            superParameter(target, node);
                            node = node.nextSibling();
                        }
                        while (node != null);
                    }
                    // else we have a super() with-out/params
                }
            }
        }
    }

    protected void superParameter(final ConstructorDef target, final Node parent) {
        assert parent != null;

        parent.ensure("EXPR");

        Node node = parent.firstChild();

        SuperParameterDef def = new SuperParameterDef();

        if (node.is("TYPECAST")) {
            node = node.firstChild();
            assert node != null;

            node.ensure("TYPE");
            def.setType(type(node));
        }
        else if (node.is("LITERAL_as")) {
            node = node.firstChild();
            assert node != null;

            node = node.nextSibling();
            assert node != null;

            node.ensure("TYPE");
            def.setType(type(node));
        }
        else if (node.is("LITERAL_new")) {
            node = node.firstChild();
            assert node != null;

            def.setType(type(node));
        }
        else if (node.is(new String[] { "LITERAL_true", "LITERAL_false" })) {
            def.setType(TypeDef.BOOLEAN);
        }
        else if (node.is("STRING_LITERAL")) {
            def.setType(TypeDef.STRING);
        }
        else if (node.is("NUM_INT")) {
            def.setType(TypeDef.INT);
        }
        else if (node.is("NUM_LONG")) {
            def.setType(TypeDef.LONG);
        }
        else if (node.is("NUM_FLOAT")) {
            def.setType(TypeDef.FLOAT);
        }
        else if (node.is("NUM_DOUBLE")) {
            def.setType(TypeDef.DOUBLE);
        }
        else if (node.is("NUM_BIG_INT")) {
            def.setType(TypeDef.BIG_INT);
        }
        else if (node.is("NUM_BIG_DECIMAL")) {
            def.setType(TypeDef.BIG_DECIMAL);
        }
        else if (node.is("STRING_CONSTRUCTOR")) {
            def.setType(TypeDef.STRING);
        }
        else if (node.is("IDENT")) {
            // Could be a reference to parameters
            String ident = node.text();

            Set parameters = target.getParameters();
            if (!parameters.isEmpty()) {
                Iterator iter = parameters.iterator();

                while (iter.hasNext()) {
                    ParameterDef param = (ParameterDef)iter.next();
                    String name = param.getName();

                    if (name != null && name.equals(ident)) {
                        def.setType(param.getType());
                        break;
                    }
                }
            }
        }
        else {
            // Lets just assume the parser has done its job, but we don't have type information
            // so all we can do is use a null value w/o any cast
            // throw new UnexpectedNodeException(node);
        }

        target.addSuperParameter(def);
    }

    protected void methodDef(final Node parent) {
        assert parent != null;

        MethodDef def = new MethodDef();

        Node node = parent.firstChild();

        if (node.is("TYPE_PARAMETERS")) {
            //
            // FIXME: Support generics
            //

            node = node.nextSibling();
        }

        node = modifiers(def, node);

        if (node.is("TYPE")) {
            def.setReturns(type(node));
            node = node.nextSibling();
        }
        else {
            def.setReturns(new TypeDef());
        }

        node = name(def, node);

        node = parameters(def, node);

        node = throwz(def, node);

        // Don't care about the body
        if (node != null) {
            node.skip("SLIST");
        }

        javadocs(def, parent);

        clazz.addMethod(def);
    }

    protected void fieldDef(final Node parent) {
        assert parent != null;

        FieldDef def = new FieldDef();

        Node node = parent.firstChild();

        node = modifiers(def, node);

        if (node.is("TYPE")) {
            def.setType(type(node));
            node = node.nextSibling();
        }
        else {
            def.setType(new TypeDef());
        }

        name(def, node);

        javadocs(def, parent);

        clazz.addField(def);
    }

    //
    // Helpers
    //

    protected Node name(final NameAware target, final Node node) {
        assert target != null;
        assert node != null;

        target.setName(identifier(node));

        return node.nextSibling();
    }

    protected Node modifiers(final ModifiersAware target, final Node parent) {
        assert parent != null;

        // Skip unless this is a modifiers node
        if (!parent.is("MODIFIERS")) {
            return parent;
        }

        ModifiersDef def = new ModifiersDef();

        for (Node node = parent.firstChild(); node != null; node = node.nextSibling()) {

            if (node.is(new String[] { "STRICTFP", "STATIC_IMPORT", "ANNOTATION" })) {
                // ignore
            }
            else if (node.is("LITERAL_private")) {
                def.add(ModifiersDef.PRIVATE);
            }
            else if (node.is("LITERAL_protected")) {
                def.add(ModifiersDef.PROTECTED);
            }
            else if (node.is("LITERAL_public")) {
                def.add(ModifiersDef.PUBLIC);
            }
            else if (node.is("ABSTRACT")) {
                def.add(ModifiersDef.ABSTRACT);
            }
            else if (node.is("FINAL")) {
                def.add(ModifiersDef.FINAL);
            }
            else if (node.is("LITERAL_native")) {
                def.add(ModifiersDef.NATIVE);
            }
            else if (node.is("LITERAL_static")) {
                def.add(ModifiersDef.STATIC);
            }
            else if (node.is("LITERAL_synchronized")) {
                def.add(ModifiersDef.SYNCHRONIZED);
            }
            else if (node.is("LITERAL_transient")) {
                def.add(ModifiersDef.TRANSIENT);
            }
            else if (node.is("LITERAL_volatile")) {
                def.add(ModifiersDef.VOLATILE);
            }
            else {
                throw new UnexpectedNodeException(node);
            }
        }

        target.getModifiers().merge(def);

        return parent.nextSibling();
    }

    protected Set interfaces(final Node parent) {
        assert parent != null;

        Set set = new LinkedHashSet();

        for (Node node = parent.firstChild(); node != null; node = node.nextSibling()) {
            set.add(type(node));
        }

        return set;
    }

    protected Node throwz(final ThrowsAware target, final Node parent) {
        assert target != null;
        // assert parent != null;

        if (parent == null) {
            return null;
        }

        // The throw statement is optional, so skip if not present
        if (!parent.is("LITERAL_throws")) {
            return parent;
        }

        Node node = parent.firstChild();

        if (node != null) {
            do {
                target.getThrows().add(type(node));
                node = node.nextSibling();
            }
            while (node != null);
        }


        return parent.nextSibling();
    }

    protected Node parameters(final ParametersAware target, final Node parent) {
        assert target != null;
        assert parent != null;

        parent.ensure("PARAMETERS");

        Node node = parent.firstChild();

        if (node != null) {
            do {
                target.addParameter(parameter(node));
                node = node.nextSibling();
            }
            while (node != null);
        }

        return parent.nextSibling();
    }

    protected ParameterDef parameter(final Node parent) {
        assert parent != null;

        ParameterDef def = new ParameterDef();

        Node node = parent.firstChild();

        node = modifiers(def, node);

        if (node.is("TYPE")) {
            def.setType(type(node));
            node = node.nextSibling();
        }
        else {
            def.setType(new TypeDef());
        }

        def.setName(identifier(node));

        return def;
    }

    protected TypeDef type(final Node parent) {
        assert parent != null;

        TypeDef def = new TypeDef();

        Node node;

        if (parent.is(new String[] { "IDENT", "DOT" })) {
            node = parent;
        }
        else {
            node = parent.firstChild();
        }

        if (node != null) {
            int dim = 0;

            // Determine array dimentions if there are any
            while (node.is("ARRAY_DECLARATOR")) {
                node = node.firstChild();
                dim++;
            }

            def.setDimensions(dim);
            def.setName(qualifiedName(node));
        }
        
        return def;
    }

    private static JavaDocParser javaDocParser = new JavaDocParser();

    protected void javadocs(final JavaDocAware target, final Node node) {
        assert target != null;
        assert node != null;

        // Get the snippet between nodes
        String text = parser.snippet(lastNode, node);

        // Remember where we last looked
        lastNode = node;

        // Attempt to parse out the docs
        JavaDocDef def = javaDocParser.parse(text);

        if (def != null) {
            target.setJavaDoc(def);
        }
    }

    protected String qualifiedName(final Node parent) {
        assert parent != null;

        if (parent.is("IDENT")) {
            return parent.text();
        }
        else if (parent.is("DOT")) {
            Node node = parent.firstChild();
            StringBuffer buff = new StringBuffer();
            boolean first = true;

            for (; node != null; node = node.nextSibling()) {
                if (first) {
                    first = false;
                }
                else {
                    buff.append(".");
                }

                buff.append(qualifiedName(node));
            }
            return buff.toString();
        }
        else {
            return parent.text();
        }
    }

    protected String identifier(final Node node) {
        assert node != null;

        node.ensure("IDENT");

        return node.text();
    }
}