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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kdgregory.swinglib.UIHelper;

import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Manages a File Upload dialog: a JFileChooser, a textfield for the
 *  destination, and some checkboxes to control what gets uploaded.
 */
public class UploadDialogController
{
    private Concierge concierge;

    // the dialog is lazily constructed by show()
    private JDialog theDialog;
    private JFileChooser fChooser;
    private JTextField fSaveTo;
    private JCheckBox fIsRecursive;
    private JCheckBox fIsPublic;
    private Action okAction;
    private Action cancelAction;


    public UploadDialogController(Concierge concierge)
    {
        this.concierge = concierge;
    }

//----------------------------------------------------------------------------
//  Public Methods
//----------------------------------------------------------------------------

    /**
     *  Displays the (modal) dialog. On return, indicates whether the user
     *  has selected any files (cancel clears the selection).
     */
    public boolean show()
    {
        if (theDialog == null)
            constructDialog();

        String prevDir = concierge.getConfig().getLastUploadDirectory();
        if (prevDir != null)
        {
            fChooser.setCurrentDirectory(new File(prevDir));
        }
        fChooser.setSelectedFiles(null);
        okAction.setEnabled(false);
        theDialog.setVisible(true);
        return getSelectedFiles().length > 0;
    }


    /**
     *  Retrieves the selected file, <code>null</code> if the user clicked
     *  "Cancel".
     */
    public File[] getSelectedFiles()
    {
        return fChooser.getSelectedFiles();
    }


    /**
     *  Retrieves the destination directory name. Will be meaningless if
     *  the user clicked "Cancel".
     */
    public String getDestination()
    {
        return fSaveTo.getText();
    }


    /**
     *  Returns the "make public" indicator.
     */
    public boolean getMakePublic()
    {
        return fIsPublic.isSelected();
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private void constructDialog()
    {
        okAction = new OKAction();
        cancelAction = new CancelAction();

        theDialog = UIHelper.newModalDialog(
                    concierge.getDialogOwner(),
                    "Select File to Upload",
                    createContentPane(),
                    okAction, cancelAction);
    }


    private JPanel createContentPane()
    {
        fChooser = new JFileChooser();
        fChooser.setFileHidingEnabled(false);
        fChooser.setControlButtonsAreShown(false);
        fChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fChooser.setMultiSelectionEnabled(true);
        fChooser.addPropertyChangeListener(new ChooserListener());
        fChooser.setAlignmentX(Component.LEFT_ALIGNMENT);

        fSaveTo = new JTextField("/", 30);
        JPanel pSaveTo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSaveTo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pSaveTo.add(new JLabel("Destination:"));
        pSaveTo.add(UIHelper.interButtonSpace());
        pSaveTo.add(fSaveTo);

        fIsRecursive = new JCheckBox("Recursively process directories");
        fIsRecursive.setEnabled(true);
        fIsRecursive.addActionListener(new IsRecursiveListener());

        fIsPublic = new JCheckBox("Make Files Public");
        fIsPublic.setEnabled(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(fChooser);
        panel.add(pSaveTo);
        panel.add(fIsRecursive);
        panel.add(fIsPublic);
        return panel;
    }


    // this method is called whenever we change some state that would
    // result in a bad selection
    private void updateOKButton()
    {
        File selected = fChooser.getSelectedFile();

        // first case is easy
        if (selected == null)
        {
            okAction.setEnabled(false);
            return;
        }

        // don't allow directory selection unless doing recursive op
        if (selected.isDirectory())
        {
            okAction.setEnabled(fIsRecursive.isSelected());
            return;
        }

        // normal file selected, we can let user select it
        okAction.setEnabled(true);
    }


    private class ChooserListener
    implements PropertyChangeListener
    {
        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            String prop = evt.getPropertyName();
            if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop))
            {
                fChooser.setSelectedFile(null);
                File dir = fChooser.getCurrentDirectory();
                if (dir != null)
                {
                    concierge.getConfig()
                              .setLastUploadDirectory(dir.getAbsolutePath());
                }
            }
            else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop))
            {
                updateOKButton();
            }
        }
    }


    // the OK button should not be enabled for directories unless the user
    // has checked this field; this listener is invoked on any change, so
    // that the button state can be re-evaluated
    private class IsRecursiveListener
    implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            updateOKButton();
        }
    }


    // this action simply closes the dialog; all necessary data is held
    // in the dialog's fields
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
            fChooser.setSelectedFiles(null);
            theDialog.setVisible(false);
        }
    }
}
