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

package com.kdgregory.app.s3util;

import javax.swing.SwingUtilities;

import net.sf.kdgcommons.lang.StringUtil;

import com.kdgregory.app.s3util.dialogs.BucketDialogController;
import com.kdgregory.app.s3util.main.Concierge;
import com.kdgregory.app.s3util.main.ConfigBean;
import com.kdgregory.app.s3util.main.MainFrameController;
import com.kdgregory.app.s3util.s3ops.S3InitialLoadOp;


/**
 *  Entry point for the <em>S3Util</em> GUI.
 */
public class Main
{
    public static void main(String[] argv)
    throws Exception
    {
        final Concierge concierge = new Concierge(new ConfigBean());
        SwingUtilities.invokeAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                new MainFrameController(concierge).buildAndShow();
                if (StringUtil.isEmpty(concierge.getConfig().getAmazonBucketName()))
                {
                    new BucketDialogController(concierge).show();
                }
                else
                {
                    new S3InitialLoadOp(concierge).start();
                }
            }
        });
    }
}
