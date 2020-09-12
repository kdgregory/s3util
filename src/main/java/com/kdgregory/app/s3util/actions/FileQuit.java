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

package com.kdgregory.app.s3util.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Shuts down the application, after first checking to ensure that everything
 *  has been saved. In addition to being invoked from the menu, this action is
 *  attached to the main frame's close button.
 */
public class FileQuit
extends AbstractAction
implements WindowListener
{
    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(getClass());

//----------------------------------------------------------------------------
//  Instance Variables and Constructor
//----------------------------------------------------------------------------

    public FileQuit()
    {
        super("Quit");
    }

//----------------------------------------------------------------------------
//  ActionListener
//----------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e)
    {
        logger.info("invoked");
        System.exit(0);
    }


//----------------------------------------------------------------------------
//  WindowListener
//----------------------------------------------------------------------------

    @Override
    public void windowActivated(WindowEvent e)
    {
        // ignored
    }

    @Override
    public void windowClosed(WindowEvent e)
    {
        // ignored
    }

    @Override
    public void windowClosing(WindowEvent e)
    {
        actionPerformed(null);
    }

    @Override
    public void windowDeactivated(WindowEvent e)
    {
        // ignored
    }

    @Override
    public void windowDeiconified(WindowEvent e)
    {
        // ignored
    }

    @Override
    public void windowIconified(WindowEvent e)
    {
        // ignored
    }

    @Override
    public void windowOpened(WindowEvent e)
    {
        // ignored
    }
}
