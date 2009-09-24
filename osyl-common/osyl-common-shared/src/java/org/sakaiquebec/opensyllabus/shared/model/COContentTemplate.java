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

//package org.sakaiquebec.opensyllabus.shared.model;
//
///**
// * This class contains the default  XML course outline content used at course outline creation.
// * 
// * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
// * @deprecated
// */
//
//public class COContentTemplate {
//    
//    /**
//     * Default XML string - course outline content template with no data
//     */
//    static String defaultXml =
//	"<?xml version=\"1.0\" encoding=\"UTF-8\"?><CO id='H2008-1-1429100' name='H2008-1-1429100' type='bylecture' lang ='fr' scrty='public'>" +
//	    "<label><![CDATA[...insert your title here...]]></label>" +
//	    "<COUnit  type='presentation' scrty='public'>" +
//	    "<label><![CDATA[Presentation]]></label>" +
//	    "<COUnitContent type='presentation' scrty='public'>" +
//		"<COResourceProxy type='text' scrty='public'>" +
//		    "<COContentRubric type='description'/>" +
//		    "<properties>" +
//			"<visible>true</visible>" +
//			"<requirementLevel>recommended</requirementLevel>" +
//			"<important>false</important>" +
//		    "</properties>" +
//		    "<COResource  type='text' scrty='public'>" +
//			"<properties>" +
//			    "<text>" +
//			    "<![CDATA[...insert your presentation text  here...]]>" +
//			    "</text>" +
//			"</properties>" +
//		    "</COResource>" +
//		"</COResourceProxy>" +
//		"</COUnitContent>" +
//		"</COUnit>"+
//		"<COUnit  type='contactinfo' scrty='public'>" +
//		"<label><![CDATA[Contact information]]></label>" +
//			"<COUnitContent type='contactinfo' scrty='public'>" +
//			"</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit  type='learningmat' scrty='public'>" +
//		"<label><![CDATA[Learning material]]></label>" +
//			"<COUnitContent type='learningmat' scrty='public'>" +
//				"<COResourceProxy type='text' scrty='public'>" +
//					"<COContentRubric type='misresources'/>" +
//					"<properties>" +
//						"<visible>true</visible>" +
//						"<requirementLevel>recommended</requirementLevel>" +
//						"<important>false</important>" +
//					"</properties>" +
//					"<COResource  type='text' scrty='public'>" +
//						"<properties>" +
//						 "<text>" +
//						    "<![CDATA[...insert your learning material text  here...]]>" +
//						    "</text>" +
//						"</properties>" +
//					"</COResource>" +
//				"</COResourceProxy>" +
//				"<COResourceProxy type='text' scrty='public'>" +
//				"<COContentRubric type='complbibres'/>" +
//				"<properties>" +
//					"<visible>true</visible>" +
//					"<requirementLevel>recommended</requirementLevel>" +
//					"<important>false</important>" +
//				"</properties>" +
//				"<COResource  type='text' scrty='public'>" +
//					"<properties>" +
//					 "<text>" +
//					    "<![CDATA[<strong>La bibliotheque vous propose...<br></strong>" +
//					    "<a href=\"http://taos.hec.ca/web2/tramp2.exe/log_in&setting_key=french&screen=biblio.html&*nocours=2-086-07\">Environnement economique international</a><br>" +
//"<a href=\"http://taos.hec.ca/web2/tramp2.exe/log_in&setting_key=french&screen=biblio.html&*nocours=2-411-00\">Management des grandes entreprises</a><br>" +
//"<a href=\"http://taos.hec.ca/web2/tramp2.exe/log_in&setting_key=french&screen=biblio.html&*nocours=3-430-05\">Management strategique des organisations</a><br>" +
//"<a href=\"http://taos.hec.ca/web2/tramp2.exe/log_in&setting_key=french&screen=biblio.html&*nocours=6-430-05\">Approches et outils contemporains en strategie</a><br>" +
//"<a href=\"http://taos.hec.ca/web2/tramp2.exe/log_in&setting_key=french&screen=biblio.html&*nocours=6-513-03\">Cha&icirc;ne logistique, fondements et tendances</a><br>" +
//"<a href=\"http://taos.hec.ca/web2/tramp2.exe/log_in&setting_key=french&screen=biblio.html&*nocours=30-403-00\">Devenir une organisation apprenante</a><br>" +
//"<a href=\"http://taos.hec.ca/web2/tramp2.exe/log_in&setting_key=french&screen=biblio.html&*nocours=EMBA\">Programme EMBA</a><br>]]>" +
//					    "</text>" +
//					"</properties>" +
//				"</COResource>" +
//			"</COResourceProxy>" +
//			"</COUnitContent>" +
//		"</COUnit>" +
//		"<COStructure type='evaluations' scrty='public'>" +//evaluations
//		"<COUnit type='evaluation' scrty='public'>" +
//		"<label><![CDATA[Mid-term exam]]></label>" +
//		"<COUnitContent type='evaluation' scrty='public'>" +
//		"<properties>" +
//		"<rating>30%</rating>"+
//		"</properties>"+
//			"<COResourceProxy type='text' scrty='attendee'>" +
//				"<COContentRubric type='description'/>" +
//				"<label><![CDATA[Mid-term exam]]></label>" +
//				"<properties>" +
//					"<visible>true</visible>" +
//					"<requirementLevel>recommended</requirementLevel>" +
//					"<important>false</important>" +
//					"<uri>sakaiAssignementURI</uri>"+
//					"<rating>5</rating>"+
//					"<opendate>10-02-2008</opendate>"+
//					"<closedate>10-02-2008</closedate>"+
//				"</properties>" +
//				"<COResource type='text' scrty='attendee'>" +
//					"<properties>" +
//							"<text>" +
//								"<![CDATA[...insert your text here...]]>" +
//							"</text>" +
//					"</properties>" +
//				"</COResource>" +
//			"</COResourceProxy>" +
//		"</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='evaluation' scrty='public'>" +
//		"<label><![CDATA[Quiz]]></label>" +
//		"<COUnitContent type='evaluation' scrty='public'>" +
//		"<properties>" +
//		"<rating>20%</rating>"+
//		"</properties>"+
//			"<COResourceProxy type='text' scrty='attendee'>" +
//				"<COContentRubric type='description'/>" +
//				"<label><![CDATA[Quiz]]></label>" +
//				"<properties>" +
//					"<visible>true</visible>" +
//					"<requirementLevel>recommended</requirementLevel>" +
//					"<important>false</important>" +
//					"<uri>sakaiAssignementURI</uri>"+
//					"<rating>5</rating>"+
//					"<opendate>10-02-2008</opendate>"+
//					"<closedate>10-02-2008</closedate>"+
//				"</properties>" +
//				"<COResource type='text' scrty='attendee'>" +
//					"<properties>" +
//							"<text>" +
//								"<![CDATA[...insert your text here...]]>" +
//							"</text>" +
//					"</properties>" +
//				"</COResource>" +
//			"</COResourceProxy>" +
//		"</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='evaluation' scrty='public'>" +
//		"<label><![CDATA[Session work]]></label>" +
//		"<COUnitContent type='evaluation' scrty='public'>" +
//		"<properties>" +
//		"<rating>20%</rating>"+
//		"</properties>"+
//			"<COResourceProxy type='text' scrty='attendee'>" +
//				"<COContentRubric type='description'/>" +
//				"<label><![CDATA[Session work]]></label>" +
//				"<properties>" +
//					"<visible>true</visible>" +
//					"<requirementLevel>recommended</requirementLevel>" +
//					"<important>false</important>" +
//					"<uri>sakaiAssignementURI</uri>"+
//					"<rating>5</rating>"+
//					"<opendate>10-02-2008</opendate>"+
//					"<closedate>10-02-2008</closedate>"+
//				"</properties>" +
//				"<COResource type='homework' scrty='attendee'>" +
//					"<properties>" +
//							"<text>" +
//								"<![CDATA[...insert your text here...]]>" +
//							"</text>" +
//					"</properties>" +
//				"</COResource>" +
//			"</COResourceProxy>" +
//		"</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='evaluation' scrty='attendee'>" +
//		"<label><![CDATA[Final exam]]></label>" +
//		"<COUnitContent type='evaluation' scrty='public'>" +
//		"<properties>" +
//		"<rating>30%</rating>"+
//		"</properties>"+
//		"<COResourceProxy type='text' scrty='attendee'>" +
//		"<COContentRubric type='description'/>" +
//		"<label><![CDATA[Final exam]]></label>" +
//		"<properties>" +
//			"<visible>true</visible>" +
//			"<requirementLevel>recommended</requirementLevel>" +
//			"<important>false</important>" +
//			"<uri>sakaiAssignementURI</uri>"+
//			"<rating>5</rating>"+
//			"<opendate>2008-02-01</opendate>"+
//			"<closedate>2008-02-05</closedate>"+
//		"</properties>" +
//		"<COResource type='ehomework' scrty='attendee'>" +
//			"<properties>" +
//				"<text>" +
//					"<![CDATA[...insert your text here...]]>" +
//				"</text>" +
//			"</properties>" +
//		"</COResource>" +
//	"</COResourceProxy>" +
//	"</COUnitContent>" +
//	"</COUnit>" +
//	"</COStructure><!-- evaluations-->" +
//	    "<COStructure type='lectures' scrty='public'>" +
//		"<COUnit  type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy  type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +					
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//			        "</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit id='H2008-1-1429039-L' type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy  type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//		        "</COResourceProxy>" +
//		    "</COUnitContent>" +
//		"</COUnit>" +
//	        "<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent ref='12345678' type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//        		"<COResourceProxy  type='text' scrty='public'>" +
//	                    "<COContentRubric	type='objectives'/>" +
//        		    "<properties>" +
//	                	"<visible>true</visible>" +
//			        "<requirementLevel>recommended</requirementLevel>" +
//                		"<important>false</important>" +
//		                "<library>false</library>" +
//				"<bookstore>false</bookstore>" +
//                	    "</properties>" +
//		            "<COResource  type='text' scrty='public'>" +
//			        "<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//		        "</COResourceProxy>" +
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//		    "</COUnitContent>" +
//                "</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//			        "<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//			        "<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//		        "<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +				
//		    "</COUnitContent>" +
//		"</COUnit>" +
//		"<COUnit type='lecture' scrty='public'>" +
//		    "<label><![CDATA[...insert your title here...]]></label>" +
//		    "<COUnitContent type='lecture' scrty='public'>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='description'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +
//			"<COResourceProxy type='text' scrty='public'>" +
//			    "<COContentRubric	type='objectives'/>" +
//			    "<properties>" +
//				"<visible>true</visible>" +
//				"<requirementLevel>recommended</requirementLevel>" +
//				"<important>false</important>" +
//			    "</properties>" +
//			    "<COResource  type='text' scrty='public'>" +
//				"<properties>" +
//				    "<text>" +
//				    "<![CDATA[...insert your text here...]]>" +
//				    "</text>" +
//				"</properties>" +
//			    "</COResource>" +
//			"</COResourceProxy>" +				
//		    "</COUnitContent>" +
//		"</COUnit>" +
//	    "</COStructure><!-- lectures-->" +
//        "</CO>";
//
//
//
//	/**
//	 * @return the default template xml string
//	 */
//	static public String getDefaultXml(){
//		return defaultXml;
//	}
//}
