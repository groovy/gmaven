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

package org.codehaus.groovy.maven.common;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Tests for the {@link SystemOutputHijacker} class.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SystemOutputHijackerTest
    extends TestCase
{
    private ByteArrayOutputStream buff;

    private PrintStream out;
    
    protected void setUp() throws Exception {
        buff = new ByteArrayOutputStream();
        out = new PrintStream(buff);

        assertFalse(SystemOutputHijacker.isInstalled());
    }

    protected void tearDown() throws Exception {
        buff = null;
        out = null;

        assertFalse(SystemOutputHijacker.isInstalled());
    }

    private void installOut() throws Exception {
        assertFalse(SystemOutputHijacker.isRegistered());

        SystemOutputHijacker.install(out);

        assertTrue(SystemOutputHijacker.isInstalled());
        assertTrue(SystemOutputHijacker.isRegistered());
    }

    private void deregisterAndUninstall() throws Exception {
        SystemOutputHijacker.deregister();

        assertFalse(SystemOutputHijacker.isRegistered());

        SystemOutputHijacker.uninstall();

        assertFalse(SystemOutputHijacker.isInstalled());
    }

    public void testInstallWithStream() throws Exception {
        System.out.println("before");

        installOut();
        
        try {
            System.out.print("hijacked");
        }
        finally {
            deregisterAndUninstall();
        }
        
        System.out.println("after");
        
        String msg = new String(buff.toByteArray());
        
        assertEquals("hijacked", msg);
    }
    
    public void testInstallRegisterWithStream() throws Exception {
        System.out.println("before");
        
        SystemOutputHijacker.install();
        assertTrue(SystemOutputHijacker.isInstalled());

        assertFalse(SystemOutputHijacker.isRegistered());
        SystemOutputHijacker.register(out);
        assertTrue(SystemOutputHijacker.isRegistered());
        
        try {
            System.out.print("hijacked");
        }
        finally {
            deregisterAndUninstall();
        }
        
        System.out.println("after");
        
        String msg = new String(buff.toByteArray());
        
        assertEquals("hijacked", msg);
    }
    
    public void testDualStreams() throws Exception {
        ByteArrayOutputStream errBuff = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errBuff);
        
        System.out.println("before");
        System.err.println("BEFORE");
        
        SystemOutputHijacker.install(out, err);
        
        assertTrue(SystemOutputHijacker.isInstalled());
        assertTrue(SystemOutputHijacker.isRegistered());
        
        try {
            System.out.print("hijacked");
            System.err.print("HIJACKED");
        }
        finally {
            deregisterAndUninstall();
        }
        
        System.out.println("after");
        System.err.println("AFTER");
        
        assertEquals("hijacked", new String(buff.toByteArray()));
        assertEquals("HIJACKED", new String(errBuff.toByteArray()));
    }
    
    public void testChildThreads() throws Exception {
        System.out.println("before");
        
        installOut();
        
        Runnable task = new Runnable() {
            public void run() {
                System.out.print("hijacked");
            }
        };
        
        try {
            System.out.print("<");
            
            Thread t = new Thread(task);
            t.start();
            t.join();
            
            System.out.print(">");
        }
        finally {
            deregisterAndUninstall();
        }
        
        System.out.println("after");
        
        String msg = new String(buff.toByteArray());
        
        assertEquals("<hijacked>", msg);
    }
    
    public void testNestedRegistration() throws Exception {
        System.out.println("before");

        installOut();
        
        try {
            System.out.print("hijacked");
            
            ByteArrayOutputStream childBuff = new ByteArrayOutputStream();
            PrintStream childOut = new PrintStream(childBuff);
            
            System.out.print("!");
            
            SystemOutputHijacker.register(childOut);
            
            try {
                System.out.print("child");
            }
            finally {
                SystemOutputHijacker.deregister();
            }

            System.out.print("!");

            assertEquals("child", new String(childBuff.toByteArray()));
        }
        finally {
            deregisterAndUninstall();
        }
        
        System.out.println("after");
        
        String msg = new String(buff.toByteArray());
        
        assertEquals("hijacked!!", msg);
    }
}
