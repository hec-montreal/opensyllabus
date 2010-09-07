/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.sakaiquebec.opensyllabus.common.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class FileHelper {
    
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static String getFileContent(String filepath) {
	return getFileContent(filepath, DEFAULT_CHARSET);
    }

    public static String getFileContent(String filepath, String encoding) {
	String fileContent = null;
	InputStreamReader inputStreamReader;
	File coXslFile = new File(filepath);
	try {
	    inputStreamReader =
		    new InputStreamReader(new FileInputStream(coXslFile));
	    StringWriter writer = new StringWriter();
	    BufferedReader buffer = new BufferedReader(inputStreamReader);
	    String line = "";
	    while (null != (line = buffer.readLine())) {
		writer.write(line);
	    }
	    fileContent = writer.toString();
	    fileContent = new String(fileContent.getBytes(), encoding);

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return fileContent;
    }
    
    public static void writeFileContent(String filepath, String content)
    	throws IOException {
	
	writeFileContent(new File(filepath), content);
    }
    
    public static void writeFileContent(File file, String content)
    	throws IOException {

	OutputStreamWriter outputStreamWriter;
	outputStreamWriter =
	    new OutputStreamWriter(new FileOutputStream(file), DEFAULT_CHARSET);
	BufferedWriter buffer = new BufferedWriter(outputStreamWriter);
	buffer.write(content);
	buffer.close();
	
    }

}

