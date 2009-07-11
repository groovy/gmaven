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

import org.codehaus.groovy.maven.runtime.support.stubgen.UnsupportedFeatureException;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ClassDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ConstructorDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.FieldDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ImportDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.JavaDocAware;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.JavaDocDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.MethodDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ModifiersAware;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ModifiersDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.PackageDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ParameterDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.SuperParameterDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.TagDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.TypeDef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Provides support for {@link Renderer} implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class RendererSupport
    implements Renderer
{
    protected final ClassDef clazz;

    protected final Map importAliases = new HashMap();

    protected final Map definedMethods = new HashMap();

    protected RendererSupport(final ClassDef clazz) {
        assert clazz != null;

        this.clazz = clazz;

        assert clazz.getParent() != null;

        // Create a map of method signatures for quick lookup resolution
        Iterator iter = clazz.getMethods().iterator();
        while (iter.hasNext()) {
            MethodDef method = (MethodDef)iter.next();

            definedMethods.put(method.signature(), method);
        }
    }

    public void render(final Writer writer) throws IOException {
        assert writer != null;

        PrintWriter out = new PrintWriter(writer);

        renderHeader(out);

        renderPackage(out);

        renderImports(out);

        renderClass(out);

        out.flush();
    }

    public String getName() {
        return clazz.getName();
    }

    public String getPackage() {
        PackageDef pkg = clazz.getPackage();
        if (pkg != null) {
            return pkg.getName();
        }
        return null;
    }

    //
    // Rendering
    //

    protected void renderHeader(final PrintWriter out) {
        assert out != null;

        // TODO: Should try to include the legal headers from the source here if we can parse them out

        out.println("//");
        out.println("// Generated stub from " + clazz.getParent().getUrl());
        out.println("//");
        out.println();
    }

    protected void renderPackage(final PrintWriter out) {
        assert out != null;

        PackageDef def = clazz.getPackage();

        if (def != null) {
            out.print("package ");
            out.print(def.getName());
            out.println(";");
        }
        else {
            out.println("// Default package");
        }

        out.println();
    }

    protected void renderImports(final PrintWriter out) {
        assert out != null;

        Set imports = clazz.getImports();
        assert imports != null;

        if (!imports.isEmpty()) {
            Iterator iter = imports.iterator();

            while (iter.hasNext()) {
                ImportDef def = (ImportDef)iter.next();

                renderImport(out, def);
            }

            out.println();
        }
    }

    protected void renderImport(final PrintWriter out, final ImportDef def) {
        assert out != null;
        assert def != null;

        // If the import is an alias (import as) then add to the mapping and omit rendering import
        // will use the fully qualified name of the type when the alias is encoutnered

        String alias = def.getAlias();
        if (alias != null) {
            importAliases.put(alias, def);

            out.print("// Import alias '");
            out.print(alias);
            out.print("' will resolve to the full-qualified name: ");
            out.println(def.getQualifiedName());

            return;
        }

        out.print("import ");

        if (def.isStatic()) {
            out.print("static ");
        }

        out.print(def.getQualifiedName());

        if (def.isWildcard()) {
            out.print(".*");
        }

        out.println(";");
    }

    protected void renderType(final PrintWriter out, TypeDef def) {
        assert out != null;
        assert def != null;

        String name = def.getName();

        if (name == null) {
            name = "java.lang.Object";
        }
        else {
            ImportDef alias = (ImportDef)importAliases.get(name);

            if (alias != null) {
                name = alias.getQualifiedName();
            }
        }

        out.print(name);

        int dimensions = def.getDimensions();

        for (int i=0; i < dimensions; i++) {
            out.print("[]");
        }
    }

    protected void renderTypeSet(final PrintWriter out, final Set types) {
        assert out != null;
        assert types != null;

        Iterator iter = types.iterator();

        while (iter.hasNext()) {
            TypeDef def = (TypeDef)iter.next();
            renderType(out, def);

            if (iter.hasNext()) {
                out.print(", ");
            }
        }
    }

    protected void renderModifiers(final PrintWriter out, final ModifiersAware source) {
        assert out != null;
        assert source != null;

        ModifiersDef modifiers = source.getModifiers();
        assert modifiers != null;

        Set values = modifiers.getValues();

        if (!values.isEmpty()) {
            Iterator iter = values.iterator();

            while (iter.hasNext()) {
                String modifier = (String)iter.next();

                out.print(modifier);

                if (iter.hasNext()) {
                    out.print(" ");
                }
            }

            out.print(" ");
        }
    }

    protected void renderJavaDoc(final PrintWriter out, final JavaDocAware source, final String indent) {
        assert out != null;
        assert source != null;
        assert indent != null;

        JavaDocDef def = source.getJavaDoc();

        if (def != null) {
            out.print(indent);
            out.println("/**");

            String comment = def.getComment();

            if (comment != null) {
                comment = comment.trim();

                if (comment.length() > 0) {
                    BufferedReader reader = new BufferedReader(new StringReader(comment));

                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            out.print(indent);
                            out.print(" * ");
                            out.println(line);
                        }
                    }
                    catch (IOException e) {
                        throw new InternalError("Failed to process JavaDoc comment string: " + comment); // Should never happen
                    }
                }
            }

            Set tags = def.getTags();
            assert tags != null;

            if (!tags.isEmpty()) {
                // Add blank if we have a coment
                if (comment != null && comment.length() > 0) {
                    out.print(indent);
                    out.println(" *");
                }

                Iterator iter = tags.iterator();

                while (iter.hasNext()) {
                    TagDef tag = (TagDef)iter.next();

                    out.print(indent);
                    out.print(" * @");
                    out.print(tag.getName());

                    String value = tag.getValue();
                    if (value != null && value.length() > 0) {
                        out.print(" ");
                        out.print(value);
                    }

                    out.println();
                }
            }

            out.print(indent);
            out.println(" */");
        }
    }

    protected void renderClass(final PrintWriter out) {
        assert out != null;

        renderJavaDoc(out, clazz, "");

        // TODO: May want to clone the modifiers to prevent augmenting the model

        ModifiersDef modifiers = clazz.getModifiers();
        if (!modifiers.hasAccessModifiers()) {
            modifiers.add("public");
        }

        renderModifiers(out, clazz);

        ClassDef.Type type = clazz.getType();
        assert type != null;

        out.print(type);
        out.print(" ");
        out.println(clazz.getName());

        switch (type.code) {
            case ClassDef.Type.CLASS_CODE:
            {
                TypeDef superClass = clazz.getSuperClass();
                if (superClass != null) {
                    out.print("    extends ");
                    renderType(out, superClass);
                    out.println();
                }

                Set implementz = clazz.getImplements();
                assert implementz != null;

                if (!implementz.isEmpty()) {
                    out.print("    implements ");
                    renderTypeSet(out, implementz);
                    out.println();
                }
            }
            break;

            case ClassDef.Type.INTERFACE_CODE:
            {
                Set implementz = clazz.getImplements();
                assert implementz != null;

                if (!implementz.isEmpty()) {
                    out.print("    extends ");
                    renderTypeSet(out, implementz);
                    out.println();
                }
            }
            break;

            case ClassDef.Type.ENUM_CODE:
                throw new UnsupportedFeatureException("enum");

            default:
                throw new InternalError("Invalid class type: " + type); // Should never happen
        }

        out.println("{");

        renderFields(out);

        // Seperator only if we have both fields and methods
        if (!clazz.getFields().isEmpty() && !clazz.getMethods().isEmpty()) {
            out.println();
        }

        renderMethods(out);

        // TODO: See when we need to set this and when it could be harmful?

        // Render synthetic methods
        if (!clazz.isInterface()) {
            // Seperator if we have fields or methods
            if (!clazz.getFields().isEmpty() || !clazz.getMethods().isEmpty()) {
                out.println();
            }

            renderSyntheticMethods(out);
        }

        out.println("}");
    }

    protected void renderFields(final PrintWriter out) {
        assert out != null;

        Set fields = clazz.getFields();
        assert fields != null;

        if (!fields.isEmpty()) {
            Iterator iter = fields.iterator();

            while (iter.hasNext()) {
                FieldDef def = (FieldDef)iter.next();

                if (def.isProperty()) {
                    renderProperty(out, def);
                }
                else {
                    renderField(out, def);
                }

                if (iter.hasNext()) {
                    out.println();
                }
            }
        }
    }

    protected void renderField(final PrintWriter out, final FieldDef def) {
        assert out != null;
        assert def != null;

        renderJavaDoc(out, def, "    ");

        out.print("    ");

        if (!def.getParent().isInterface()) {
            renderModifiers(out, def);
        }

        TypeDef type = def.getType();

        renderType(out, type);

        out.print(" ");

        out.print(def.getName());

        // TODO: See when we need to set this and when it could be harmful?

        out.print(" = ");
        out.print(type.getDefaultValue());

        out.println(";");
    }

    protected void renderProperty(final PrintWriter out, final FieldDef def) {
        assert out != null;
        assert def != null;
        assert def.isProperty();

        // Render private field, w/original javadocs
        FieldDef field = new FieldDef();
        field.setParent(def.getParent());
        field.setJavaDoc(def.getJavaDoc());
        field.setType(def.getType());
        field.setName(def.getName());
        field.getModifiers().merge(def.getModifiers()).add("private");
        renderField(out, field);

        String name = capitalize(def.getName());

        // Setup the modifiers for property methods
        ModifiersDef modifiers = def.getModifiers();
        modifiers.add("public");
        modifiers.remove("transient").remove("volatile");

        MethodDef getter = new MethodDef();
        getter.setParent(def.getParent());
        getter.setName("get" + name);
        getter.setReturns(def.getType());
        getter.getModifiers().merge(modifiers);

        if (!definedMethods.containsKey(getter.signature())) {
            renderMethod(out, getter);
        }

        if (def.getType().isBoolean()) {
            MethodDef isser = new MethodDef();
            isser.setParent(def.getParent());
            isser.setName("is" + name);
            isser.setReturns(def.getType());
            isser.getModifiers().merge(modifiers);

            if (!definedMethods.containsKey(isser.signature())) {
                renderMethod(out, isser);
            }
        }

        if (!def.getModifiers().isFinal()) {
            MethodDef setter = new MethodDef();
            setter.setParent(def.getParent());
            setter.setName("set" + name);
            setter.setReturns(new TypeDef("void"));
            setter.addParameter(new ParameterDef(def.getType(), "value"));
            setter.getModifiers().merge(modifiers);

            if (!definedMethods.containsKey(setter.signature())) {
                renderMethod(out, setter);
            }
        }
    }

    protected String capitalize(final String string) {
        assert string != null;

        int length = string.length();

        if (length == 0) {
            return string;
        }
        else if (length == 1) {
            return string.toUpperCase();
        }
        else {
            return (Character.toUpperCase(string.charAt(0)) + string.substring(1));
        }
    }

    protected void renderSyntheticMethods(final PrintWriter out) {
        assert out != null;

        MethodDef def;

        def = new MethodDef();
        def.setParent(clazz);
        def.getModifiers().add("public");
        def.setReturns(new TypeDef("groovy.lang.MetaClass"));
        def.setName("getMetaClass");

        if (!definedMethods.containsKey(def.signature())) {
            renderMethod(out, def);
            out.println();
        }

        def = new MethodDef();
        def.setParent(clazz);
        def.getModifiers().add("public");
        def.setReturns(new TypeDef("void"));
        def.setName("setMetaClass");
        def.addParameter(new ParameterDef("groovy.lang.MetaClass", "metaClass"));

        if (!definedMethods.containsKey(def.signature())) {
            renderMethod(out, def);
            out.println();
        }

        def = new MethodDef();
        def.setParent(clazz);
        def.getModifiers().add("public");
        def.setReturns(new TypeDef("java.lang.Object"));
        def.setName("invokeMethod");
        def.addParameter(new ParameterDef("java.lang.String", "name"));
        def.addParameter(new ParameterDef("java.lang.Object", "args"));

        if (!definedMethods.containsKey(def.signature())) {
            renderMethod(out, def);
            out.println();
        }

        def = new MethodDef();
        def.setParent(clazz);
        def.getModifiers().add("public");
        def.setReturns(new TypeDef("java.lang.Object"));
        def.setName("getProperty");
        def.addParameter(new ParameterDef("java.lang.String", "name"));

        if (!definedMethods.containsKey(def.signature())) {
            renderMethod(out, def);
            out.println();
        }

        def = new MethodDef();
        def.setParent(clazz);
        def.getModifiers().add("public");
        def.setReturns(new TypeDef("void"));
        def.setName("setProperty");
        def.addParameter(new ParameterDef("java.lang.String", "name"));
        def.addParameter(new ParameterDef("java.lang.Object", "value"));

        if (!definedMethods.containsKey(def.signature())) {
            renderMethod(out, def);
        }
    }

    protected void renderMethods(final PrintWriter out) {
        assert out != null;

        if (!clazz.isInterface()) {
            renderMagicConstructors(out);
        }

        Set methods = clazz.getMethods();
        assert methods != null;

        if (!methods.isEmpty()) {
            Iterator iter = methods.iterator();

            while (iter.hasNext()) {
                MethodDef def = (MethodDef)iter.next();
                renderMethod(out, def);

                if (iter.hasNext()) {
                    out.println();
                }
            }
        }
    }

    protected void renderMagicConstructors(final PrintWriter out) {
        assert out != null;

        //
        // TODO: Could just use a cached magic super() from constrotors for each constructor to avoid needing this
        //
        
        // Only render the magic constructor if there are other constructors defined
        if (!clazz.getConstructors().isEmpty()) {
            ConstructorDef def = new ConstructorDef();
            def.setMagic(true);
            def.setParent(clazz);
            def.getModifiers().add("private");

            // Add insane params which no one would ever use... :-(
            def.addParameter(new ParameterDef("java.lang.Void", "void1"));
            def.addParameter(new ParameterDef("java.lang.Void", "void2"));
            def.addParameter(new ParameterDef("java.lang.Void", "void3"));

            renderMethod(out, def);
            out.println();
        }
    }

    protected void renderMethod(final PrintWriter out, final MethodDef def) {
        assert out != null;
        assert def != null;

        MethodDef.Type type = def.getType();

        renderJavaDoc(out, def, "    ");

        out.print("    ");

        if (!def.getParent().isInterface()) {
            // TODO: May want to clone the modifiers to prevent augmenting the model

            ModifiersDef modifiers = def.getModifiers();
            if (!modifiers.hasAccessModifiers()) {
                modifiers.add("public");
            }

            renderModifiers(out, def);
        }

        if (type == MethodDef.Type.METHOD) {
            renderType(out, def.getReturns());
            out.print(" ");
        }

        out.print(def.getName());

        out.print("(");

        renderParameters(out, def.getParameters());

        out.print(")");

        Set throwz = def.getThrows();
        assert throwz != null;

        if (!throwz.isEmpty()) {
            out.print(" throws ");
            renderTypeSet(out, throwz);
        }

        if (def.getParent().isInterface() || def.getModifiers().isAbstract() || def.getModifiers().isNative()) {
            out.println(";");
        }
        else {
            out.println(" {");

            if (def.isConstructor()) {
                assert def instanceof ConstructorDef;
                
                ConstructorDef ctor = (ConstructorDef)def;

                if (ctor.isMagic()) {
                    renderMagicConstructorSuper(out, ctor);
                }
                else {
                    renderMagicConstructorInvoke(out, ctor);
                }
            }

            out.println("        throw new InternalError(\"Stubbed method\");");

            out.println("    }");
        }
    }

    protected void renderMagicConstructorSuper(final PrintWriter out, final ConstructorDef def) {
        assert out != null;
        assert def != null;

        Set parameters = selectMagicConstructorSuperParameters(def);

        if (parameters != null) {
            out.print("        super");
            out.print("(");

            if (!parameters.isEmpty()) {
                Iterator iter = parameters.iterator();

                while (iter.hasNext()) {
                    SuperParameterDef param = (SuperParameterDef)iter.next();
                    renderSuperParameter(out, param);

                    if (iter.hasNext()) {
                        out.print(", ");
                    }
                }
            }

            out.println(");");
            out.println();
        }
    }

    protected Set selectMagicConstructorSuperParameters(final ConstructorDef target) {
        assert target != null;

        Iterator iter = target.getParent().getConstructors().iterator();

        //
        // TODO: If we can't find one that is fully typed, perhaps we should pick the next best?
        //
        
    FIND_MAGIC_CTOR:

        while (iter.hasNext()) {
            ConstructorDef def = (ConstructorDef)iter.next();

            if (!def.isMagic() && "super".equals(def.getSuperType())) {
                Set parameters = def.getSuperParameters();

                if (parameters != null && !parameters.isEmpty()) {
                    Iterator iter2 = parameters.iterator();

                    while (iter2.hasNext()) {
                        SuperParameterDef param = (SuperParameterDef)iter2.next();

                        if (param.getType() == null) {
                            continue FIND_MAGIC_CTOR;
                        }
                    }

                    return parameters;
                }
            }
        }

        return null;
    }

    protected void renderMagicConstructorInvoke(final PrintWriter out, final ConstructorDef def) {
        assert out != null;
        assert def != null;

        out.println("        this((java.lang.Void)null, (java.lang.Void)null, (java.lang.Void)null);");
    }

    protected void renderSuperParameters(final PrintWriter out, final ConstructorDef def) {
        assert out != null;
        assert def != null;

        String superType = def.getSuperType();

        if (superType != null) {

            out.print("        ");
            out.print(superType);
            out.print("(");

            Set params = def.getSuperParameters();

            if (!params.isEmpty()) {
                Iterator iter = params.iterator();

                while (iter.hasNext()) {
                    SuperParameterDef param = (SuperParameterDef)iter.next();
                    renderSuperParameter(out, param);

                    if (iter.hasNext()) {
                        out.print(", ");
                    }
                }
            }

            out.println(");");
            out.println();
        }
    }

    protected void renderSuperParameter(final PrintWriter out, final SuperParameterDef def) {
        assert out != null;
        assert def != null;

        TypeDef type = def.getType();
        if (type == null) {
            // This is probably this, null or some dot expression, which needs to be handled better
            out.print("null");
        }
        else {
            out.print("(");
            renderType(out, type);
            out.print(")");
            out.print(type.getDefaultValue());
        }
    }

    protected void renderParameters(final PrintWriter out, final Set parameters) {
        assert out != null;
        assert parameters != null;

        Iterator iter = parameters.iterator();

        while (iter.hasNext()) {
            ParameterDef def = (ParameterDef)iter.next();

            renderParameter(out, def);

            if (iter.hasNext()) {
                out.print(", ");
            }
        }
    }

    protected void renderParameter(final PrintWriter out, final ParameterDef def) {
        assert out != null;
        assert def != null;

        if (!def.getParent().getParent().isInterface()) {
            renderModifiers(out, def);
        }

        renderType(out, def.getType());

        out.print(" ");

        out.print(def.getName());
    }
}
