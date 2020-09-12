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

package com.kdgregory.app.s3util.main;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.sf.kdgcommons.lang.StringUtil;

import com.kdgregory.app.s3util.filelist.FileListTableModel;
import com.kdgregory.app.s3util.filelist.S3File;
import com.kdgregory.swinglib.SwingUtil;
import com.kdgregory.swinglib.components.MainFrame;
import com.kdgregory.swinglib.listeners.PopupListener;
import com.kdgregory.swinglib.table.FormattingRenderer;
import com.kdgregory.swinglib.table.TableUtil;


/**
 *  Responsible for building out the application main frame. This class may
 *  be constructed anywhere, but {@link #buildAndShow} must be called from
 *  the event thread.
 */
public class MainFrameController
{
    private final static String BASE_TITLE = "S3Util";

    private Concierge concierge;
    private ActionRegistry actionRegistry;

    private JFrame mainFrame;
    private JTable table;
    private FileListTableModel fileList;
    private DefaultTableModel emptyList;


    public MainFrameController(Concierge concierge)
    {
        this.concierge = concierge;
        actionRegistry = new ActionRegistry(concierge);
    }

//----------------------------------------------------------------------------
//  Initialization
//----------------------------------------------------------------------------

    public void buildAndShow()
    {
        mainFrame = new MainFrame(BASE_TITLE, actionRegistry.fileQuit);
        mainFrame.setJMenuBar(createMainMenu());
        mainFrame.setContentPane(createContentPane());
        mainFrame.pack();
        concierge.setMainFrame(this, mainFrame);

        PopupListener.attach(table, createPopupMenu());
        resetBucket();

        SwingUtil.centerAndShow(mainFrame);
    }


    private JMenuBar createMainMenu()
    {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(actionRegistry.fileRefresh);
        fileMenu.add(new JSeparator(JSeparator.HORIZONTAL));
        fileMenu.add(actionRegistry.fileUpload);
        fileMenu.add(actionRegistry.fileDownload);
        fileMenu.add(actionRegistry.fileDelete);
        fileMenu.add(new JSeparator(JSeparator.HORIZONTAL));
        fileMenu.add(actionRegistry.fileQuit);

        JMenu editMenu = new JMenu("Edit");
        fileMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.add(actionRegistry.editSelectAll);
        editMenu.add(actionRegistry.editSelectNone);
        editMenu.add(new JSeparator(JSeparator.HORIZONTAL));
        editMenu.add(actionRegistry.editCopyUrl);

        JMenu bucketMenu = new JMenu("Bucket");
        fileMenu.setMnemonic(KeyEvent.VK_B);
        bucketMenu.add(actionRegistry.bucketSelect);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(bucketMenu);
        return menuBar;
    }


    private JPopupMenu createPopupMenu()
    {
        JPopupMenu menu = new JPopupMenu();
        menu.add(actionRegistry.editSelectAll);
        menu.add(actionRegistry.editSelectNone);
        menu.add(new JSeparator(JSeparator.HORIZONTAL));
        menu.add(actionRegistry.editCopyUrl);
        menu.add(new JSeparator(JSeparator.HORIZONTAL));
        menu.add(actionRegistry.fileUpload);
        menu.add(actionRegistry.fileDownload);
        menu.add(actionRegistry.fileDelete);
        menu.add(new JSeparator(JSeparator.HORIZONTAL));
        menu.add(actionRegistry.fileRefresh);
        return menu;
    }


    private JScrollPane createContentPane()
    {
        emptyList = new DefaultTableModel(
                        new Object[0][0],
                        new String[] { "Loading..." });

        table = new JTable();
        table.setFocusable(false);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.getSelectionModel().addListSelectionListener(new SelectionListener());
        table.setDefaultRenderer(
                Integer.class,
                new FormattingRenderer(new DecimalFormat("#,##0")));
        table.setDefaultRenderer(
                Date.class,
                new FormattingRenderer(new SimpleDateFormat("MMM dd yyyy HH:mm:ss")));

        JScrollPane container = new JScrollPane(table);
        container.setPreferredSize(new Dimension(600, 400));
        return container;
    }


//----------------------------------------------------------------------------
//  Operational Methods
//----------------------------------------------------------------------------

    /**
     *  Instructs the frame to indicate the status of a background operation.
     *
     *  @param  isBusy  Pass <code>true</code> to indicate that an operation
     *                  is in progress, <code>false</code> to indicate that
     *                  it has completed.
     */
    public void setBusyState(boolean isBusy)
    {
        if (isBusy)
            concierge.getCursorManager()
                      .pushBusyCursor(mainFrame.getRootPane());
        else
            concierge.getCursorManager()
                      .popCursor(mainFrame.getRootPane());
    }


    /**
     *  Updates the frame and actions to match the current bucket,
     *  if any.
     */
    public void resetBucket()
    {
        String bucketName = concierge.getConfig().getAmazonBucketName();
        if (StringUtil.isEmpty(bucketName))
        {
            // the only time we'll have an empty bucket is when the
            // program is first initialized, or for the short time
            // between deleting a bucket and selecting a new one
            mainFrame.setTitle(BASE_TITLE);
        }
        else
        {
            mainFrame.setTitle(BASE_TITLE + " - " + bucketName);
        }

        // regardless of whether we've got a bucket or not, we will reset
        // the displayed list
        table.setModel(emptyList);
        fileList = new FileListTableModel();
    }


    /**
     *  Resets the entire list of files.
     */
    public void resetList(FileListTableModel model)
    {
        fileList = model;
        displayActualList();
        selectNone();
    }


    /**
     *  Adds a single file to the list (will update an existing file with
     *  the same key).
     */
    public void addFileToList(S3File file)
    {
        fileList.addFile(file);
    }


    /**
     *  Deletes a single file from the  list. The scroll is not changed.
     */
    public void removeFileFromList(S3File file)
    {
        fileList.deleteFile(file);
    }


    /**
     *  Returns the number of files selected.
     */
    public int getSelectionCount()
    {
        return table.getSelectedRowCount();
    }


    /**
     *  Returns the list of currently selected files.
     */
    public List<S3File> getSelectedFiles()
    {
        int[] selection = table.getSelectedRows();
        List<S3File> result = new ArrayList<S3File>(selection.length);
        for (int row : selection)
            result.add(fileList.getFileAt(row));
        return result;
    }


    /**
     *  Sets the "selected" flag on all current files.
     */
    public void selectAll()
    {
        table.selectAll();
    }


    /**
     *  Clears the "selected" flag on all current files.
     */
    public void selectNone()
    {
        table.clearSelection();
    }

//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  A helper method that determines whether the table is currently
     *  displaying the dummy "Loading" list, or the real list. If the
     *  former, switches list models and properly sets column sizes.
     */
    private void displayActualList()
    {
        if (table.getModel() != fileList)
        {
            table.setModel(fileList);
            TableUtil.setRelativeColumnWidths(
                    table,
                    fileList.getRelativeWidths());
        }
    }


    /**
     *  Listens for changes to the selection, and enables/disables actions
     *  appropriately.
     */
    private class SelectionListener
    implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent evt)
        {
            actionRegistry.updatePerSelection(getSelectionCount());
        }
    }
}
