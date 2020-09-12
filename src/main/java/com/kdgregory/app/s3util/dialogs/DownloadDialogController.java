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

package com.kdgregory.app.s3util.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.kdgregory.app.s3util.main.Concierge;
import com.kdgregory.swinglib.UIHelper;


/**
 *  Manages a File Download dialog: a JFileChooser to select the destination
 *  directory, and some options to control the download.
 */
public class DownloadDialogController
{
    private Concierge concierge;
    private File selected;

    // the dialog is lazily constructed by show()
    private JDialog theDialog;
    private JFileChooser fChooser;
    private JCheckBox fFlatten;


    public DownloadDialogController(Concierge concierge)
    {
        this.concierge = concierge;
    }

//----------------------------------------------------------------------------
//  Public Methods
//----------------------------------------------------------------------------

    /**
     *  Displays the (modal) dialog. If the user selects a file, returns it;
     *  otherwise returns <code>null</code>.
     */
    public File show()
    {
        if (theDialog == null)
            constructDialog();

        String prevDir = concierge.getConfig().getLastDownloadDirectory();
        if (prevDir != null)
        {
            File prevAsFile = new File(prevDir);
            fChooser.setCurrentDirectory(prevAsFile);
            fChooser.setSelectedFile(prevAsFile);
        }

        theDialog.setVisible(true);
        return selected;
    }


    /**
     *  Retrieves the selected file, <code>null</code> if the user clicked
     *  "Cancel".
     */
    public File getSelectedFile()
    {
        return selected;
    }


    /**
     *  Retrieves the "flatten" checkbox value. Will be meaningless if
     *  the user selected a file or clicked "Cancel".
     */
    public boolean getFlatten()
    {
        return fFlatten.isSelected();
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private void constructDialog()
    {
        theDialog = UIHelper.newModalDialog(
                    concierge.getDialogOwner(),
                    "Select Directory for Download",
                    createContentPane(),
                    new OKAction(), new CancelAction());
    }


    private JPanel createContentPane()
    {
        fChooser = new JFileChooser();
        fChooser.setFileHidingEnabled(false);
        fChooser.setControlButtonsAreShown(false);
        fChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fChooser.setMultiSelectionEnabled(false);
        fChooser.setAlignmentX(Component.LEFT_ALIGNMENT);

        fFlatten = new JCheckBox("Flatten directories");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(fChooser);
        panel.add(fFlatten);
        return panel;
    }


    private class OKAction
    extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        public OKAction()
        {
            super("Select");
        }

        @Override
        public void actionPerformed(ActionEvent ignored)
        {
            theDialog.setVisible(false);
            selected = fChooser.getSelectedFile();
            if (selected != null)
            {
                concierge.getConfig()
                          .setLastDownloadDirectory(selected.getAbsolutePath());
            }
        }
    }


    private class CancelAction
    extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        public CancelAction()
        {
            super("Cancel");
        }

        @Override
        public void actionPerformed(ActionEvent ignored)
        {
            selected = null;
            theDialog.setVisible(false);
        }
    }
}
