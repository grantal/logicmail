/*-
 * Copyright (c) 2010, Derek Konigsberg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution. 
 * 3. Neither the name of the project nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.logicprobe.LogicMail.util;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class URLEncoderTest extends TestCase {
    public URLEncoderTest() {
    }

    public URLEncoderTest(String testName, TestMethod testMethod) {
        super(testName, testMethod);
    }
    
    public void testStringNotNeedingEncoding() {
        assertEquals("12", URLEncoder.encode("12"));
        assertEquals("3F", URLEncoder.encode("3F"));
        assertEquals("Hello", URLEncoder.encode("Hello"));
        assertEquals("1337DEADBEEF", URLEncoder.encode("1337DEADBEEF"));
    }
    
    public void testStringNeedingComplexEncoding() {
        String input = "<AANLkTi=HmeCMneW-U+BHamLyPLs11xN5x45CxOty6bqC@mail.gmail.com>";
        String expected = "%3CAANLkTi%3DHmeCMneW-U%2BBHamLyPLs11xN5x45CxOty6bqC%40mail.gmail.com%3E";
        assertEquals(expected, URLEncoder.encode(input));
    }

    public void testStringNotNeedingDecoding() {
        assertEquals("12", URLEncoder.decode("12"));
        assertEquals("3F", URLEncoder.decode("3F"));
        assertEquals("Hello", URLEncoder.decode("Hello"));
        assertEquals("1337DEADBEEF", URLEncoder.decode("1337DEADBEEF"));
    }
    
    public void testStringNeedingComplexDecoding() {
        String input = "%3CAANLkTi%3DHmeCMneW-U%2BBHamLyPLs11xN5x45CxOty6bqC%40mail.gmail.com%3E";
        String expected = "<AANLkTi=HmeCMneW-U+BHamLyPLs11xN5x45CxOty6bqC@mail.gmail.com>";
        assertEquals(expected, URLEncoder.decode(input));
    }
    
    public Test suite() {
        TestSuite suite = new TestSuite("URLEncoder");
        
        suite.addTest(new URLEncoderTest("stringNotNeedingEncoding", new TestMethod()
        { public void run(TestCase tc) { ((URLEncoderTest) tc).testStringNotNeedingEncoding(); }}));
        suite.addTest(new URLEncoderTest("stringNeedingComplexEncoding", new TestMethod()
        { public void run(TestCase tc) { ((URLEncoderTest) tc).testStringNeedingComplexEncoding(); }}));
        suite.addTest(new URLEncoderTest("stringNotNeedingDecoding", new TestMethod()
        { public void run(TestCase tc) { ((URLEncoderTest) tc).testStringNotNeedingDecoding(); }}));
        suite.addTest(new URLEncoderTest("stringNeedingComplexDecoding", new TestMethod()
        { public void run(TestCase tc) { ((URLEncoderTest) tc).testStringNeedingComplexDecoding(); }}));

        return suite;
    }
}
