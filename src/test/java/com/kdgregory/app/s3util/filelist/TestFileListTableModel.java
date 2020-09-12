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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import junit.framework.TestCase;


public class TestFileListTableModel
extends TestCase
{
//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    private static class MockModelListener
    implements TableModelListener
    {
        private List<TableModelEvent> events = new ArrayList<TableModelEvent>();

        @Override
        public void tableChanged(TableModelEvent evt)
        {
            events.add(evt);
        }

        public void reset()
        {
            events.clear();
        }

        public void assertEventCount(int expected)
        {
            assertEquals(expected, events.size());
        }

        public TableModelEvent assertOneGetAndReset()
        {
            assertEventCount(1);
            TableModelEvent event = events.get(0);
            reset();
            return event;
        }
    }

//----------------------------------------------------------------------------
//  Test Cases
//
//  notes:
//      - since we're just testing the model, not its interaction with a
//        table, there's no need to run on the event dispatch thread
//      - I won't be testing the column data, as it may change based on
//        how I want the UI to look
//----------------------------------------------------------------------------

    public void testDefaultConstructor() throws Exception
    {
        FileListTableModel model = new FileListTableModel();
        assertEquals(0, model.getRowCount());
    }


    public void testConstructFromExistingList() throws Exception
    {
        S3File f1 = new S3File("test/foo.txt");
        S3File f2 = new S3File("bar.txt");
        S3File f3 = new S3File("test/bar.txt");

        TreeSet<S3File> src = new TreeSet<S3File>(Arrays.asList(f1, f2, f3));
        FileListTableModel model = new FileListTableModel(src);

        assertEquals(3, model.getRowCount());
        assertSame(f2, model.getFileAt(0));
        assertSame(f3, model.getFileAt(1));
        assertSame(f1, model.getFileAt(2));
    }


    public void testFilesAddedInSortedOrder() throws Exception
    {
        S3File f1 = new S3File("test/foo.txt");
        S3File f2 = new S3File("bar.txt");
        S3File f3 = new S3File("test/bar.txt");
        S3File f4 = new S3File("baz.txt");

        FileListTableModel model = new FileListTableModel();

        model.addFile(f1);
        assertEquals(1, model.getRowCount());
        assertSame(f1, model.getFileAt(0));

        model.addFile(f2);
        assertEquals(2, model.getRowCount());
        assertSame(f2, model.getFileAt(0));
        assertSame(f1, model.getFileAt(1));

        model.addFile(f3);
        assertEquals(3, model.getRowCount());
        assertSame(f2, model.getFileAt(0));
        assertSame(f3, model.getFileAt(1));
        assertSame(f1, model.getFileAt(2));

        model.addFile(f4);
        assertEquals(4, model.getRowCount());
        assertSame(f2, model.getFileAt(0));
        assertSame(f4, model.getFileAt(1));
        assertSame(f3, model.getFileAt(2));
        assertSame(f1, model.getFileAt(3));
    }


    public void testAddNotification() throws Exception
    {
        S3File f1 = new S3File("test/foo.txt");     // initial
        S3File f2 = new S3File("bar.txt");          // insert-first
        S3File f3 = new S3File("test/bar.txt");     // insert-middle
        S3File f4 = new S3File("zippy/test.txt");   // insert-end

        FileListTableModel model = new FileListTableModel();
        MockModelListener lsnr = new MockModelListener();
        model.addTableModelListener(lsnr);

        model.addFile(f1);
        TableModelEvent e1 = lsnr.assertOneGetAndReset();
        assertEquals(TableModelEvent.INSERT,        e1.getType());
        assertEquals(0,                             e1.getFirstRow());
        assertEquals(0,                             e1.getLastRow());
        assertEquals(TableModelEvent.ALL_COLUMNS,   e1.getColumn());

        model.addFile(f2);
        TableModelEvent e2 = lsnr.assertOneGetAndReset();
        assertEquals(TableModelEvent.INSERT,        e2.getType());
        assertEquals(0,                             e2.getFirstRow());
        assertEquals(0,                             e2.getLastRow());
        assertEquals(TableModelEvent.ALL_COLUMNS,   e2.getColumn());

        model.addFile(f3);
        TableModelEvent e3 = lsnr.assertOneGetAndReset();
        assertEquals(TableModelEvent.INSERT,        e3.getType());
        assertEquals(1,                             e3.getFirstRow());
        assertEquals(1,                             e3.getLastRow());
        assertEquals(TableModelEvent.ALL_COLUMNS,   e3.getColumn());

        model.addFile(f4);
        TableModelEvent e4 = lsnr.assertOneGetAndReset();
        assertEquals(TableModelEvent.INSERT,        e4.getType());
        assertEquals(3,                             e4.getFirstRow());
        assertEquals(3,                             e4.getLastRow());
        assertEquals(TableModelEvent.ALL_COLUMNS,   e4.getColumn());
    }


    public void testAddWithSameKeyIsUpdate() throws Exception
    {
        S3File f1 = new S3File("test/foo.txt");
        S3File f2 = new S3File("test/foo.txt");
        TreeSet<S3File> init = new TreeSet<S3File>(Arrays.asList(f1));

        FileListTableModel model = new FileListTableModel(init);
        MockModelListener lsnr = new MockModelListener();
        model.addTableModelListener(lsnr);

        model.addFile(f2);
        assertEquals(1, model.getRowCount());
        assertSame(f2, model.getFileAt(0));

        TableModelEvent e2 = lsnr.assertOneGetAndReset();
        assertEquals(TableModelEvent.UPDATE,        e2.getType());
        assertEquals(0,                             e2.getFirstRow());
        assertEquals(0,                             e2.getLastRow());
        assertEquals(TableModelEvent.ALL_COLUMNS,   e2.getColumn());
    }


    public void testDelete() throws Exception
    {
        S3File f1 = new S3File("test/foo.txt");
        S3File f2 = new S3File("test/bar.txt");
        S3File f3 = new S3File("test/baz.txt");
        TreeSet<S3File> init = new TreeSet<S3File>(Arrays.asList(f1, f2, f3));

        FileListTableModel model = new FileListTableModel(init);
        MockModelListener lsnr = new MockModelListener();
        model.addTableModelListener(lsnr);

        model.deleteFile(new S3File("test/baz.txt"));
        assertEquals(2, model.getRowCount());
        assertSame(f2, model.getFileAt(0));
        assertSame(f1, model.getFileAt(1));

        TableModelEvent evt = lsnr.assertOneGetAndReset();
        assertEquals(TableModelEvent.DELETE,        evt.getType());
        assertEquals(1,                             evt.getFirstRow());
        assertEquals(1,                             evt.getLastRow());
        assertEquals(TableModelEvent.ALL_COLUMNS,   evt.getColumn());
    }


    // this test makes sure that we don't blow up
    public void testDeleteNonexistent() throws Exception
    {
        S3File f1 = new S3File("test/foo.txt");
        S3File f2 = new S3File("test/bar.txt");
        S3File f3 = new S3File("test/baz.txt");
        TreeSet<S3File> init = new TreeSet<S3File>(Arrays.asList(f1, f2, f3));

        FileListTableModel model = new FileListTableModel(init);
        MockModelListener lsnr = new MockModelListener();
        model.addTableModelListener(lsnr);

        model.deleteFile(new S3File("zippy.txt"));
        assertEquals(3, model.getRowCount());
        assertSame(f2, model.getFileAt(0));
        assertSame(f3, model.getFileAt(1));
        assertSame(f1, model.getFileAt(2));

        lsnr.assertEventCount(0);
    }
}
