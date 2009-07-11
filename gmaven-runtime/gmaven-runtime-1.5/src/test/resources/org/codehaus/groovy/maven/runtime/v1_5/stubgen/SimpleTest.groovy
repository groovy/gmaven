/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.codehaus.groovy.maven.runtime.v1_5.stubgen;

import foo
import foo.bar
import foo.bar.*
import static foo.bar.baz
import somelong.ClassThingy as CT
import foo.bar.baz.ick.Poop as Crap

/**
 * Javadoc for class.
 *
 * @author me
 * @version whatever
 */
class SimpleTest
    extends Foo
    implements Bar, Baz
{
    /**
     * Javadoc for field.
     */
    String prop1

    /**
     * Custom setter for prop1
     */
    void setProp1(String foo) {}

    def prop2 = "foo"

    /**
     * Multi-line JavaDoc.
     *
     * For prop3.
     */
    CT prop3 = 1

    def prop4 = new Foo()

    private static final def prop5 = new Foo(1,2,3)

    static boolean prop5 = null

    static final boolean prop6 = null

    transient boolean trans = false

    volatile boolean vola = false

    byte byteValue

    short shortValue

    int intValue

    long longValue

    float floatValue

    double doubleValue

    char charValue

    boolean booleanValue

    BigDecimal bigDec

    BigInteger bigInt
    
    /** Byte array. */
    byte[] byteArray

    /** Int array. */
    int[] intArray

    /** Float array. */
    float[] floatArray

    /** Object array. */
    Object[] objectArray

    /** 2D Object array. */
    Object[][] objectArray2d

    /** 2D byte array. */
    byte[][] byteArray2d

    // short, int, long, float, double, char, boolean

    /** 2D String array. */
    String[][] stringArray2d

    /** 3D Object array. */
    Object[][][] objectArray3d

    /** 3D byte array. */
    byte[][][] byteArray3d

    // MORE?

    /** Yup 12D array. */
    def insaneMatrixFunction(byte [][][][][][][][][][][][] universalTruth) {
        println "there is no god"
    }

    /**
     * Javadoc for constructor.
     */
    def SimpleTest() {
        super((String)"foo", 1, 2 as long, new Object())
        
        foo.bar.println "bar"
    }
    
    /**
     * Constructor w/${}
     */
    def SimpleTest() {
        super("${}")
    }
    
    /**
     * Constructor w/${a}
     */
    def SimpleTest() {
        super("${a}")
    }
    
    /**
     * Constructor w/super(otherMethod())
     */
    def SimpleTest() {
        super(otherMethod())
    }
    
    /**
     * Constructor w/super(1 + 1)
     */
    def SimpleTest() {
        super(1 + 1)
    }
    
    /**
     * Constructor w/super(1 - 1)
     */
    def SimpleTest() {
        super(1 - 1)
    }
    
    def SimpleTest() {
    }

    def SimpleTest() {
        super();
    }

    def SimpleTest() {
        super("FOO");
    }

    def SimpleTest() {
        this("FOO");
    }

    def SimpleTest() {
        super(this, true, 1, 1.1, foo.class, 0x01, null);
    }

    def SimpleTest (String s , GantBinding b) {
        this ( s , b , null )
    }

    def SimpleTest (File f) {
        this ( f.name , null , null )
    }

    private CT foo() {}

    Crap foo() {}
    
    /**
     * Javadoc for test() method.
     *
     * @param a foo
     * @param b bar
     * @param c baz
     */
    public static final void test(final int a, Crap b, float c) {
        def var1
        
        println "hi"
        
        super.test("foo")
    }
    
    /**
     * Javadoc for test2() method.
     *
     * @throws a.b.c whatever
     */
    public static final def test2(foo, bar, baz) throws a.b.c, b, c, d {
        println "there"
        
        return "blah"
    }
}

class MainModel extends Model implements ExitListener {

   public static final String ACTION_PRINT               = 'print',
                              ACTION_OPEN_PRINT_DIALOG   = 'openPrintDialog',
                              ACTION_OPEN_PREFERENCES    = 'openPreferences',
                              ACTION_EXIT                = 'exit',
                              ACTION_OPEN_TIP_OF_THE_DAY = 'openTipOfTheDay',
                              ACTION_OPEN_HELP_CONTENTS  = 'openHelpContents',
                              ACTION_OPEN_ABOUT_DIALOG   = 'openAboutDialog'

   public static final String MONTH    = 'month',
                              OPERATOR = 'operator',
                              TOTALS   = 'totals'

}