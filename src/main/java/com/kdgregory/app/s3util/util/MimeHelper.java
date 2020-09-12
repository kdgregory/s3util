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
import java.util.HashMap;
import java.util.Map;



/**
 *  Provides a lookup table for file extension to MIME type. If you have
 *  JDK 1.6, you could use <code>MimetypesFileTypeMap</code> (although
 *  it requires configuration).
 *  <p>
 *  This class supports only those types most likely to be used in a website.
 *  See http://www.iana.org/assignments/media-types/ for the official list
 *  of registered types.
 */
public class MimeHelper
{
    private static Map<String,String> TABLE = new HashMap<String,String>();
    static
    {
        // image
        TABLE.put("gif",    "image/gif");
        TABLE.put("jpg",    "image/jpeg");
        TABLE.put("jpeg",   "image/jpeg");
        TABLE.put("png",    "image/png");

        // text (note: some text files map to application types)
        TABLE.put("css",    "text/css");
        TABLE.put("htm",    "text/html");
        TABLE.put("html",   "text/html");
        TABLE.put("js",     "application/javascript");
        TABLE.put("txt",    "text/plain");
        TABLE.put("xml",    "text/xml");

        // other
        TABLE.put("pdf",    "application/pdf");
    }


    /**
     *  Will attempt to match the file's extension (that component of the
     *  filename after the last '.') to a list of known mappings. If unable
     *  to find a mapping, returns <code>application/octet-stream</code>.
     */
    public static String lookup(File file)
    {
        String name = file.getName();
        int extIdx = name.lastIndexOf('.') + 1;
        if ((extIdx > 0) && (extIdx < name.length()))
        {
            String ext = name.substring(extIdx);
            String mimeType = TABLE.get(ext);
            if (mimeType != null)
                return mimeType;
        }
        return "application/octet-stream";
    }
}
