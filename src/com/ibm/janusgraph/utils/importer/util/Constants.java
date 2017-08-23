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

public class Constants {
    public static final String INTERMEDIARIES_KEY_NAME = "Intermediaries";
    public static final String INTERMEDIARIES_LABEL = "Intermediaries";
    public static final String INTERMEDIARIES_FILE_NAME = "Intermediaries.csv";
    public static final String OFFICERS_KEY_NAME = "Officers";
    public static final String OFFICERS_LABEL = "Officers";
    public static final String OFFICERS_FILE_NAME = "Officers.csv";
    public static final String INTERMEDIARIES_OFFICERS_LABEL = "Intermediaries_Officers";

    public static final String VERTEX_MAP = "vertexMap";
    public static final String EDGE_MAP = "edgeMap";

    public static final String EDGE_LEFT_MAPPING = "[edge_left]";
    public static final String EDGE_RIGHT_MAPPING = "[edge_right]";
    public static final String VERTEX_LABEL_MAPPING = "[VertexLabel]";
    public static final String EDGE_LABEL_MAPPING = "[EdgeLabel]";

    public static final Integer DEFAULT_WORKERS_TARGET_RECORD_COUNT = 50000;
    public static final Integer DEFAULT_VERTEX_COMMIT_COUNT = 10000;
    public static final Integer DEFAULT_EDGE_COMMIT_COUNT = 1000;
}
