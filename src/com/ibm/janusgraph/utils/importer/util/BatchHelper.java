/*******************************************************************************
 *   Copyright 2017 IBM Corp. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.ibm.janusgraph.utils.importer.util;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BatchHelper {

    public static long countLines(String filePath) throws Exception {
        LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filePath)));
        try {
            lnr.skip(Long.MAX_VALUE);
            return lnr.getLineNumber() + 1;
        } finally {
            lnr.close();
        }
    }

    public static Date convertDate(String inputDate) throws ParseException {
        // TODO Handle time as part of date or separate

        SimpleDateFormat dateParser = null;
        // Detect the date format and convert it
        if (inputDate.matches("[0-9]*")) {
            for (int i = inputDate.length(); i < 4; i++) {
                inputDate = "0".concat(inputDate);
            }
            // Only numbers Hour / Minute format
            dateParser = new SimpleDateFormat("Hm");
        } else if (inputDate.matches("[0-9]{2}-[A-Za-z]{3}-[0-9]{4}")) {
            // Use dd-MMM-yyyy format
            dateParser = new SimpleDateFormat("dd-MMM-yyyy");
        } else if (inputDate.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            // Use yyyy-mm-dd format
            dateParser = new SimpleDateFormat("yyyy-mm-dd");
        } else if (inputDate.matches("[0-9]{2}/[0-9]{2}/[0-9]{2}")) {
            // Use dd/mm/yy format
            dateParser = new SimpleDateFormat("dd/mm/yy");
        } else if (inputDate.matches("[0-9]+\\.[0-9]{2}\\.[0-9]{4}")) {
            // dd.mm.yyyy
            dateParser = new SimpleDateFormat("dd.mm.yyyy");
        } else {
            // Default use MM/dd/yy
            dateParser = new SimpleDateFormat("MM/dd/yy hh:mm");
        }

        return dateParser.parse(inputDate);
    }

    public static Object convertPropertyValue(String value, Class dataType) throws ParseException {
        Object convertedValue = null;

        if (dataType == Integer.class)
            convertedValue = new Integer(value);
        else if (dataType == Date.class)
            convertedValue = convertDate(value);
        else
            convertedValue = value;

        return convertedValue;
    }

}
