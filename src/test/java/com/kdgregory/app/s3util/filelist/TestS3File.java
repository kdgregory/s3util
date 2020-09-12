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

package com.kdgregory.app.s3util.filelist;

import java.io.File;

import junit.framework.TestCase;


public class TestS3File
extends TestCase
{
    public void testConstructFromFile() throws Exception
    {
        S3File s3File = new S3File(new File("test.txt"), "foo");
        assertEquals("foo/test.txt", s3File.getKey());
        assertEquals("foo", s3File.getS3Path());
    }


    public void testConstructFromFileWithAbsolutePath() throws Exception
    {
        S3File s3File = new S3File(new File("test.txt"), "/foo");
        assertEquals("foo/test.txt", s3File.getKey());
        assertEquals("foo", s3File.getS3Path());
    }


    public void testConstructFromFileWithTrailingSlashOnPath() throws Exception
    {
        S3File s3File = new S3File(new File("test.txt"), "/foo/");
        assertEquals("foo/test.txt", s3File.getKey());
        assertEquals("foo", s3File.getS3Path());
    }


    public void testConstructFromFileWithEmptyPath() throws Exception
    {
        S3File s3File = new S3File(new File("test.txt"), "");
        assertEquals("test.txt", s3File.getKey());
        assertEquals("", s3File.getS3Path());
    }


    public void testConstructFromFileWithSingleSlashAsPath() throws Exception
    {
        S3File s3File = new S3File(new File("test.txt"), "/");
        assertEquals("test.txt", s3File.getKey());
        assertEquals("", s3File.getS3Path());
    }


    public void testConstructFromKey() throws Exception
    {
        S3File s3File = new S3File("foo/test.txt");
        assertEquals("foo/test.txt", s3File.getKey());
        assertEquals("foo", s3File.getS3Path());
        assertEquals("test.txt", s3File.getFile().getPath());
    }


    public void testConstructFromKeyWithoutPath() throws Exception
    {
        S3File s3File = new S3File("test.txt");
        assertEquals("test.txt", s3File.getKey());
        assertEquals("", s3File.getS3Path());
        assertEquals("test.txt", s3File.getFile().getPath());
    }


    // this is simply to exercise the logic ... ensure that we retain
    // the key (which is used for operations), but display without a
    // leading slash ... there should be no reason for something with
    // one of these keys to find its way to the server
    public void testConstructFromBogusKey() throws Exception
    {
        S3File s3File = new S3File("/foo/test.txt");
        assertEquals("/foo/test.txt", s3File.getKey());
        assertEquals("foo", s3File.getS3Path());
        assertEquals("test.txt", s3File.getFile().getPath());
    }


    public void testLocalFileBecomesAbsolute() throws Exception
    {
        File lclFile = new File("test.txt");
        S3File s3File = new S3File(lclFile, "");

        // note: this assertion uses the path so that (1) failure will
        // provide useful information, and (2) we won't get hung up on
        // implementation details of File.equals()
        assertEquals(lclFile.getAbsoluteFile().getPath(),
                     s3File.getFile().getPath());
    }


    public void testCompareWithDifferentPaths() throws Exception
    {
        S3File file1 = new S3File("foo/baz.txt");
        S3File file2 = new S3File("bar/baz.txt");
        assertTrue(file1.compareTo(file2) > 0);
    }


    public void testCompareWithSamePathsDifferentFiles() throws Exception
    {
        S3File file1 = new S3File("foo/bar.txt");
        S3File file2 = new S3File("foo/baz.txt");
        assertTrue(file1.compareTo(file2) < 0);
    }


    public void testCompareEqual() throws Exception
    {
        S3File file1 = new S3File("foo/bar.txt");
        S3File file2 = new S3File("foo/bar.txt");
        assertTrue(file1.compareTo(file2) == 0);
    }


    // added this test because of strange behavior in the file list
    // ... but it passed
    public void testCompareWithEmptyPath() throws Exception
    {
        S3File file1 = new S3File("bar.txt");
        S3File file2 = new S3File("baz.txt");
        assertTrue(file1.compareTo(file2) < 0);
        assertTrue(file2.compareTo(file1) > 0);

        S3File file3 = new S3File("baz.txt");
        assertTrue(file2.compareTo(file3) == 0);
    }


    public void testEqualsAndHashCode() throws Exception
    {
        S3File file1 = new S3File("foo/bar.txt");
        S3File file2 = new S3File("foo/bar.txt");
        S3File file3 = new S3File("foo/baz.txt");

        assertTrue(file1.equals(file2));
        assertTrue(file2.equals(file1));

        assertFalse(file2.equals(file3));
        assertFalse(file3.equals(file2));

        assertFalse(file3.equals(null));

        // the not-equal hashcode checks are here to make sure that we don't
        // accidentally delete code ... we picked the key values to ensure
        // that they pass

        assertTrue(file1.hashCode() == file2.hashCode());
        assertTrue(file2.hashCode() != file3.hashCode());
    }
}
