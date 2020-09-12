// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.app.s3util.util;

import java.io.File;

import junit.framework.TestCase;

public class TestMimeHelper
extends TestCase
{
    // this test exists to exercise the extension extraction mechanism
    // I see no good reason to cross-check the entire table
    public void testLookup() throws Exception
    {
        String text    = "text/plain";
        String notText = "application/octet-stream";

        assertEquals(text,      MimeHelper.lookup(new File("foo.txt")));
        assertEquals(notText,   MimeHelper.lookup(new File("footxt")));
        assertEquals(notText,   MimeHelper.lookup(new File("fo.otxt")));
        assertEquals(notText,   MimeHelper.lookup(new File("footxt.")));
    }
}
