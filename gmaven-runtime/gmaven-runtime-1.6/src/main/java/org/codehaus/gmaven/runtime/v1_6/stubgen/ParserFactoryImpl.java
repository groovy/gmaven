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

package org.codehaus.groovy.maven.runtime.v1_6.stubgen;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.collections.AST;
import org.codehaus.groovy.antlr.AntlrASTProcessSnippets;
import org.codehaus.groovy.antlr.AntlrASTProcessor;
import org.codehaus.groovy.antlr.LineColumn;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.java.Java2GroovyConverter;
import org.codehaus.groovy.antlr.java.JavaLexer;
import org.codehaus.groovy.antlr.java.JavaRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.DynamicTokens;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.Node;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.NodeSupport;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.ParseException;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.Parser;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.ParserFactory;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.SourceType;
import org.codehaus.groovy.maven.runtime.support.stubgen.parser.Tokens;

import java.io.Reader;

/**
 * Provides stub-parsing support for Groovy 1.6.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class ParserFactoryImpl
    implements ParserFactory
{
    private final Tokens tokens = new DynamicTokens(GroovyTokenTypes.class);

    public Parser create(final SourceType type) {
        assert type != null;

        switch (type.code) {
            case SourceType.GROOVY_CODE:
                return new GroovyParser(type);

            case SourceType.JAVA_CODE:
                return new JavaParser(type);

            default:
                throw new IllegalArgumentException("Invalid source type: " + type);
        }
    }

    private Node node(final AST ast) {
        // ast may be null
        
        if (ast == null) {
            return null;
        }
        
        return new NodeSupport() {
            protected Tokens tokens() {
                return tokens;
            }

            protected int type() {
                return ast.getType();
            }

            protected String name() {
                return tokens.name(type());
            }

            protected int childCount() {
                return ast.getNumberOfChildren();
            }

            public String text() {
                return ast.getText();
            }

            public Node firstChild() {
                return node(ast.getFirstChild());
            }

            public Node nextSibling() {
                return node(ast.getNextSibling());
            }

            public int line() {
                return ast.getLine();
            }

            public int column() {
                return ast.getColumn();
            }
        };
    }

    //
    // ParserSupport
    //

    private abstract class ParserSupport
        implements Parser
    {
        protected final SourceType type;

        protected SourceBuffer sourceBuffer;

        protected String[] tokenNames;

        protected ParserSupport(final SourceType type) {
            assert type != null;

            this.type = type;
        }

        public SourceType getSourceType() {
            return type;
        }

        public Tokens getTokens() {
            return tokens;
        }

        public Node parse(final Reader reader, final String fileName) throws ParseException {
            assert reader != null;
            assert fileName != null;

            sourceBuffer = new SourceBuffer();

            try {
                return doParse(new UnicodeEscapingReader(reader, sourceBuffer));
            }
            catch (RecognitionException e) {
                throw new ParseException(e.getMessage(), new ParseException.Location(e.getLine(), e.getColumn(), fileName));
            }
            catch (Exception e) {
                throw new ParseException(e);
            }
        }

        protected abstract Node doParse(final UnicodeEscapingReader reader) throws Exception;

        public String snippet(final Node start, final Node stop) {
            // start may be null
            assert stop != null;

            if (sourceBuffer == null) {
                throw new IllegalStateException();
            }

            // Figure out where we should start looking
            LineColumn startAt;
            if (start != null) {
                startAt = new LineColumn(start.line(), start.column());
            }
            else {
                startAt = new LineColumn(1, 1);
            }

            // And where to stop
            LineColumn stopAt = new LineColumn(stop.line(), stop.column());

            return sourceBuffer.getSnippet(startAt, stopAt);
        }
    }

    //
    // GroovyParser
    //

    private class GroovyParser
        extends ParserSupport
    {
        public GroovyParser(final SourceType type) {
            super(type);
        }

        protected Node doParse(final UnicodeEscapingReader reader) throws Exception {
            GroovyLexer lexer = new GroovyLexer(reader);
            reader.setLexer(lexer);

            GroovyRecognizer parser = GroovyRecognizer.make(lexer);
            parser.setSourceBuffer(sourceBuffer);
            tokenNames = parser.getTokenNames();

            parser.compilationUnit();
            AST ast = parser.getAST();

            AntlrASTProcessor processor;

            // Decorate the AST w/line+col information
            processor = new AntlrASTProcessSnippets();
            ast = processor.process(ast);

            return node(ast);
        }
    }

    //
    // JavaParser
    //

    private class JavaParser
        extends ParserSupport
    {
        public JavaParser(final SourceType type) {
            super(type);
        }

        protected Node doParse(final UnicodeEscapingReader reader) throws Exception {
            JavaLexer lexer = new JavaLexer(reader);
            reader.setLexer(lexer);

            JavaRecognizer parser = JavaRecognizer.make(lexer);
            parser.setSourceBuffer(sourceBuffer);
            tokenNames = parser.getTokenNames();

            parser.compilationUnit();
            AST ast = parser.getAST();

            AntlrASTProcessor processor;

            // Convert the Java AST into Groovy AST
            processor = new PreOrderTraversal(new Java2GroovyConverter(tokenNames));
            processor.process(ast);

            // Decorate the AST w/line+col information
            processor = new AntlrASTProcessSnippets();
            ast = processor.process(ast);

            return node(ast);
        }
    }
}