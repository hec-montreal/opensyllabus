/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.sakaiquebec.opensyllabus.shared.model;

/**
 * This class contains the list of all the possible rubrics in a course outline.
 * TODO: when GWT 1.5 release will be used, replace this class with enum types
 * 
 * @author <a href="mailto:sacha.Lepretre@crim.ca">Sacha Lepretre</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContentRubricTypeList {

    public static final String UNDEFINED = "undefined";
    public static final String HOMEWORKS = "homeworks";
    public static final String SESSION = "session";
    public static final String QUIZ = "quiz";
    public static final String QAS = "qas";
    public static final String TOOLS = "tools";
    public static final String READINGLIST = "readinglist";
    public static final String EVALUATIONS = "evaluations";
    public static final String NEWS = "news";
    public static final String LEARNINGSTRAT = "learningstrat";
    public static final String INSTRUCTIONS = "instructions";
    public static final String TEACHNOTE = "teachnote";
    public static final String EXERCISES = "exercises";
    public static final String EXAMS = "exams";
    public static final String ASSIGNMENTS = "assignments";
    public static final String CASESTUDIES = "casestudies";
    public static final String RESSINCLASS = "ressinclass";
    public static final String MISRESOURCES = "misresources";
    public static final String BIBLIOGRAPHY = "bibliography";
    public static final String PASTEXAMS = "pastexams";
    public static final String OBJECTIVES = "objectives";
    public static final String DESCRIPTION = "description";
    public static final String PLAGIAT = "plagiat";
    public static final String CONTACTINFO = "contactinfo";
    public static final String COORDINATORS = "coordinators";
    public static final String LECTURERS = "lecturers";
    public static final String TEACHINGASSISTANTS = "teachingassistants";
    public static final String SECRETARIES = "secretaries";
    public static final String EVALCRITERIA = "evalcriteria";
    public static final String EVALPREPARATION = "evalpreparation";
    public static final String EVALDETAILS = "evaldetails";
    public static final String EVALSUBPROC = "evalsubproc";
    public static final String PLAGIARISM = "plagiarism";
    public static final String COMPLBIBRES = "complbibres";
    public static final String MISCELLANEOUS = "mis";

    /**
     * The list of types.
     */
    private static final String[] rubricTypeList =
	    { UNDEFINED, INSTRUCTIONS, DESCRIPTION, OBJECTIVES, PASTEXAMS,
		    BIBLIOGRAPHY, MISRESOURCES, RESSINCLASS, LEARNINGSTRAT,
		    CASESTUDIES, ASSIGNMENTS, NEWS, EVALUATIONS, EXAMS,
		    EXERCISES, READINGLIST, TEACHNOTE, TOOLS, QAS, QUIZ,
		    SESSION, HOMEWORKS, CASESTUDIES, PLAGIAT, CONTACTINFO,
		    COORDINATORS, LECTURERS, TEACHINGASSISTANTS, SECRETARIES,
		    EVALCRITERIA, EVALPREPARATION, EVALDETAILS, EVALSUBPROC,
		    MISCELLANEOUS, PLAGIARISM, COMPLBIBRES};

    /**
     * Returns the list of rubrics.
     * 
     * @return
     */
    public static final String[] getRubricTypeList() {
	return rubricTypeList;
    }
}
