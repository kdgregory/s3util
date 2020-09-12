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
import java.util.Collections;
import java.util.Date;
import java.util.SortedSet;

import javax.swing.table.AbstractTableModel;


/**
 *  Manages the list of files.
 */
public class FileListTableModel
extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    private ArrayList<S3File> files;


    /**
     *  Constructs an empty instance.
     */
    public FileListTableModel()
    {
        files = new ArrayList<S3File>();
    }


    /**
     *  Constructs an instance from an existing list of files.
     */
    public FileListTableModel(SortedSet<S3File> files)
    {
        this.files = new ArrayList<S3File>(files);
    }

//----------------------------------------------------------------------------
//  TableModel
//----------------------------------------------------------------------------

    @Override
    public int getColumnCount()
    {
        return 4;
    }


    @Override
    public int getRowCount()
    {
        return files.size();
    }


    @Override
    public String getColumnName(int col)
    {
        switch (col)
        {
            case 0 :
                return "Path";
            case 1 :
                return "Filename";
            case 2 :
                return "Size";
            case 3 :
                return "Uploaded";
            default :
                throw new IllegalArgumentException("invalid column: " + col);
        }
    }


    @Override
    public Class<?> getColumnClass(int col)
    {
        switch (col)
        {
            case 0 :
                return String.class;
            case 1 :
                return String.class;
            case 2 :
                return Integer.class;
            case 3 :
                return Date.class;
            default :
                throw new IllegalArgumentException("invalid column: " + col);
        }
    }


    @Override
    public Object getValueAt(int row, int col)
    {
        switch (col)
        {
            case 0 :
                return getFileAt(row).getS3Path();
            case 1 :
                return getFileAt(row).getFile().getName();
            case 2 :
                return getFileAt(row).getContentLength();
            case 3 :
                return getFileAt(row).getLastModified();
            default :
                throw new IllegalArgumentException("invalid column: " + col);
        }
    }


//----------------------------------------------------------------------------
//  Public Methods
//----------------------------------------------------------------------------

    /**
     *  Returns the suggested relative widths of the columns in this model.
     */
    public int[] getRelativeWidths()
    {
        return new int[] { 50, 20, 15, 15 };
    }


    /**
     *  Returns the index of the named file, -1 if the file isn't in the
     *  model.
     */
    public int indexOf(S3File file)
    {
        int idx = Collections.binarySearch(files, file);
        return (idx >= 0) ? idx : -1;
    }


    /**
     *  Returns the file at the specified index.
     */
    public S3File getFileAt(int row)
    {
        return files.get(row);
    }


    /**
     *  Adds a new file to the model, inserting it at the proper location.
     *  If the file already exists in the model, it will be replaced (this
     *  may be used to update an entry's metadata).
     */
    public void addFile(S3File file)
    {
        int idx = files.size();
        if ((idx == 0) || (file.compareTo(files.get(idx - 1)) > 0))
        {
            files.add(file);
            fireTableRowsInserted(idx, idx);
            return;
        }

        idx = Collections.binarySearch(files, file);
        if (idx >= 0)
        {
            files.set(idx, file);
            fireTableRowsUpdated(idx, idx);
        }
        else
        {
            // insert-at-end should have been caught above, so the corrected
            // index will be the element > file
            idx = -(idx + 1);
            files.add(idx, file);
            fireTableRowsInserted(idx, idx);
        }
    }


    /**
     *  Removes a file from the model.
     */
    public void deleteFile(S3File file)
    {
        int idx = Collections.binarySearch(files, file);
        if (idx < 0)
            return;

        files.remove(idx);
        fireTableRowsDeleted(idx, idx);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

}
