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

package org.codehaus.groovy.maven.plugin.execute;

//
// NOTE: Nicked from maven-core and massaged to resolve properties for Groovy executions.
//       Needed to have this delegate to Map.get() instead of Properties.getProperty()
//       to get objects in properties to resolve correctly when chained together.
//

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.path.PathTranslator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;

import java.io.File;

/**
 * Custom expresion evaluation for Groovy executions.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ExpressionEvaluatorImpl
    implements ExpressionEvaluator
{
    private final MavenSession context;

    private final MojoExecution mojoExecution;

    private final MavenProject project;

    private final PathTranslator pathTranslator;

    private final String basedir;

    public ExpressionEvaluatorImpl(final MavenSession context, final MavenProject project) {
        this.context = context;
        this.mojoExecution = new MojoExecution(new MojoDescriptor());
        this.pathTranslator = lookupPathTranslator();
        this.project = project;
        this.basedir = lookupBasedir();
    }

    private PathTranslator lookupPathTranslator() {
        try {
            return (PathTranslator)context.lookup(PathTranslator.ROLE);
        }
        catch (ComponentLookupException e) {
            throw new RuntimeException(e);
        }
    }

    private String lookupBasedir() {
        String basedir = null;

        if (project != null) {
            File projectFile = project.getFile();

            // this should always be the case for non-super POM instances...
            if (projectFile != null) {
                basedir = projectFile.getParentFile().getAbsolutePath();
            }
        }

        if (basedir == null) {
            basedir = System.getProperty("user.dir");
        }

        return basedir;
    }
    
    public Object evaluate(final String expr) throws ExpressionEvaluationException {
        try {
            return doEvaluate(expr);
        }
        catch (Exception e) {
            throw new ExpressionEvaluationException("Error evaluating plugin parameter expression: " + expr, e);
        }
    }

    private Object doEvaluate(String expr) throws Exception {
        if (expr == null) {
            return null;
        }

        String expression = stripTokens(expr);
        if (expression.equals(expr)) {
            int index = expr.indexOf("${");
            if (index >= 0) {
                int lastIndex = expr.indexOf("}", index);
                if (lastIndex >= 0) {
                    String retVal = expr.substring(0, index);

                    if (index > 0 && expr.charAt(index - 1) == '$') {
                        retVal += expr.substring(index + 1, lastIndex + 1);
                    }
                    else {
                        retVal += doEvaluate(expr.substring(index, lastIndex + 1));
                    }

                    retVal += doEvaluate(expr.substring(lastIndex + 1));
                    return retVal;
                }
            }

            // Was not an expression
            if (expression.indexOf("$$") > -1) {
                return expression.replaceAll("\\$\\$", "\\$");
            }
            else {
                return expression;
            }
        }

        Object value = evaluateSpecials(expression);

        if (value == null) {
            value = resolveProperties(expression);
        }

        if (value instanceof String) {
            String val = (String) value;

            int exprStartDelimiter = val.indexOf("${");

            if (exprStartDelimiter >= 0) {
                if (exprStartDelimiter > 0) {
                    value = val.substring(0, exprStartDelimiter) + doEvaluate(val.substring(exprStartDelimiter));
                }
                else {
                    value = doEvaluate(val.substring(exprStartDelimiter));
                }
            }
        }

        return value;
    }

    private Object resolveProperties(final String expression) {
        assert project != null;
        assert project.getProperties() != null;

        return project.getProperties().get(expression);
    }

    private Object evaluateSpecials(final String expression) throws Exception {
        Object value = null;

        if ("localRepository".equals(expression)) {
            value = context.getLocalRepository();
        }
        else if ("session".equals(expression)) {
            value = context;
        }
        else if ("reactorProjects".equals(expression)) {
            value = context.getSortedProjects();
        }
        else if ("reports".equals(expression)) {
            value = mojoExecution.getReports();
        }
        else if ("project".equals(expression)) {
            value = project;
        }
        else if ("executedProject".equals(expression)) {
            value = project.getExecutionProject();
        }
        else if (expression.startsWith("project")) {
            value = evaluateInContext(expression, project);
        }
        else if (expression.startsWith("plugin")) {
            value = evaluateInContext(expression, mojoExecution.getMojoDescriptor().getPluginDescriptor());
        }
        else if ("settings".equals(expression)) {
            value = context.getSettings();
        }
        else if (expression.startsWith("settings")) {
            value = evaluateInContext(expression, context.getSettings());
        }
        else if ("basedir".equals(expression)) {
            value = basedir;
        }
        else if (expression.startsWith("basedir")) {
            int pathSeparator = expression.indexOf("/");

            if (pathSeparator > 0) {
                value = basedir + expression.substring(pathSeparator);
            }
            else {
                throw new ExpressionEvaluationException("Unrecognized expression: " + expression);
            }
        }

        return value;
    }

    private Object evaluateInContext(final String expression, final Object context) throws Exception {
        Object value;

        int pathSeparator = expression.indexOf("/");
        if (pathSeparator > 0) {
            String pathExpression = expression.substring(0, pathSeparator);
            value = ReflectionValueExtractor.evaluate(pathExpression, context);
            value = value + expression.substring(pathSeparator);
        }
        else {
            value = ReflectionValueExtractor.evaluate(expression.substring(1), context);
        }

        return value;
    }

    private String stripTokens(String expr) {
        if (expr.startsWith("${") && expr.indexOf("}") == expr.length() - 1) {
            expr = expr.substring(2, expr.length() - 1);
        }
        return expr;
    }

    public File alignToBaseDirectory(final File file) {
        File basedir;

        if (project != null && project.getFile() != null) {
            basedir = project.getFile().getParentFile();
        }
        else {
            basedir = new File(".").getAbsoluteFile().getParentFile();
        }

        return new File(pathTranslator.alignToBaseDirectory(file.getPath(), basedir));
    }
}