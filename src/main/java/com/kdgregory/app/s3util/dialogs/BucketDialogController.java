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
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.kdgcommons.lang.StringUtil;

import com.kdgregory.app.s3util.main.Concierge;
import com.kdgregory.app.s3util.s3ops.S3BucketListOp;
import com.kdgregory.app.s3util.s3ops.S3InitialLoadOp;
import com.kdgregory.swinglib.SwingUtil;
import com.kdgregory.swinglib.UIHelper;
import com.kdgregory.swinglib.actions.DialogCloseAction;
import com.kdgregory.swinglib.field.FieldWatcher;


/**
 *  Manages a modal dialog that allows the user to select/create a bucket.
 *  Once a bucket has been selected, the prefs are updated with that bucket's
 *  name and a request is sent to refresh the main window.
 */
public class BucketDialogController
implements S3BucketListOp.Callback
{
    private Concierge concierge;

    // the dialog is lazily constructed by show()
    private FieldWatcher fieldWatcher;
    private JDialog theDialog;
    private JList<String> fBucketList;
    private JTextField fBucketName;


    public BucketDialogController(Concierge concierge)
    {
        this.concierge = concierge;
    }

//----------------------------------------------------------------------------
//  Public Methods
//----------------------------------------------------------------------------

    /**
     *  Displays the (modal) dialog. Returns the selected bucket name, as a
     *  convenience for the caller.
     */
    public void show()
    {
        if (theDialog == null)
            constructDialog();

        fieldWatcher.reset();
        refreshList();
        SwingUtil.center(theDialog, concierge.getDialogOwner());
        theDialog.setVisible(true);
    }


//----------------------------------------------------------------------------
//  List updates
//----------------------------------------------------------------------------

    /**
     *  Resets the current list contents to "Loading" and kicks of a request
     *  for new contents.
     */
    private void refreshList()
    {
        fBucketList.setListData(new String[] { "Loading..." });
        new S3BucketListOp(concierge, this).start();
    }


    /**
     *  Called by the "list buckets" operation.
     */
    @Override
    public void setBucketList(List<String> list)
    {
        String[] list2 = list.toArray(new String[list.size()]);
        fBucketList.setListData(list2);

        String current = concierge.getConfig().getAmazonBucketName();
        if (! StringUtil.isEmpty(current))
        {
            fBucketList.setSelectedValue(current, true);
            fBucketName.setText(current);
        }
    }

//----------------------------------------------------------------------------
//  GUI construction / behavior
//----------------------------------------------------------------------------

    private void constructDialog()
    {
        OKAction okAction = new OKAction();
        DialogCloseAction cancelAction = new DialogCloseAction(null, "Cancel");

        theDialog = UIHelper.newModalDialog(
                    concierge.getDialogOwner(),
                    "Create/Select Bucket",
                    createContentPane(),
                    okAction, cancelAction);
        cancelAction.setDialog(theDialog);

        fieldWatcher = new FieldWatcher(okAction);
        fieldWatcher.addWatchedField(fBucketName);
    }


    private JPanel createContentPane()
    {
        fBucketList = new JList<String>();
        fBucketList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fBucketList.getSelectionModel()
                    .addListSelectionListener(new SelectionListener());
        JScrollPane pBucketList = new JScrollPane(fBucketList);
        pBucketList.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBucketList.setBorder(UIHelper.dialogGroupBorder());

        fBucketName = new JTextField(20);
        JPanel pBucketName = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pBucketName.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBucketName.add(new JLabel("Bucket:"));
        pBucketName.add(UIHelper.interButtonSpace());
        pBucketName.add(fBucketName);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Select Existing:"));
        panel.add(pBucketList);
        panel.add(pBucketName);
        return panel;
    }


    private class SelectionListener
    implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent evt)
        {
            String selected = (String)fBucketList.getSelectedValue();
            fBucketName.setText(selected);
        }
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
            String selected = fBucketName.getText();
            concierge.getConfig().setAmazonBucketName(selected);
            concierge.getMainFrame().resetBucket();

            if (! StringUtil.isBlank(selected))
                new S3InitialLoadOp(concierge).start();
        }
    }
}
