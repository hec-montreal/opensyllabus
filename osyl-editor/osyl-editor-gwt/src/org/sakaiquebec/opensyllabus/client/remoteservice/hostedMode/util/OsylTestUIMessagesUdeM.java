/**********************************************************************************
 * $Id: OsylTestUIMessages.java 1822 2009-01-27 16:20:46Z laurent.danet@hec.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Qu�bec Team.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util;

import java.util.HashMap;
import java.util.Map;


/**
 * UI Messages map for hosted mode (used in development only). This map is
 * needed because the application cannot get the message bundle from the server
 * when used in hosted (disconnected) mode.<br/><br/>
 * 
 * ATTENTION: Any message added to the default message bundle should be added
 * here!
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lep�tre</a>
 * @version $Id: OsylTestUIMessages.java 1046 2008-06-11 21:15:46Z
 *          claude.coulombe@umontreal.ca $
 */
public class OsylTestUIMessagesUdeM {

    private static Map<String,String> messages;

    /**
     * @return the messages map
     */
    public static Map<String,String> getMap() {
	if (messages == null) {
	    messages = new HashMap<String,String>();
	    
	    messages.put("Global.error", "Erreur");
	    messages.put("Global.ok", "OK");
	    messages.put("Global.cancel", "Annuler");
	    messages.put("Global.warning", "Avertissement");
	    messages.put("Global.yes","Oui");
	    messages.put("Global.no", "Non");

	    // Toolbar texts
	    messages.put("ButtonAddToolBar", "Ajouter...");
	    messages.put("ButtonHomeToolBar", "Accueil");
	    messages.put("ButtonImportToolBar", "Documents");
	    messages.put("ButtonPrintToolBar", "Imprimer");
	    messages.put("ButtonPublishToolBar", "Publier");
	    messages.put("ButtonSaveToolBar", "Enregistrer");
	    messages.put("ButtonViewToolBar", "Aper�u...");
	    messages.put("ButtonCloseToolBar", "Fermer");

	    // Toolbar tooltips
	    messages.put("ButtonAddToolBarTooltip",
		    "Ajouter un �l�ment � la vue courante...");
	    messages.put("ButtonHomeToolBarTooltip",
		    "Retourner � l'accueil principal...");
	    messages.put("ButtonImportToolBarTooltip", "Ajouter un document...");
	    messages.put("ButtonPrintToolBarTooltip", "Imprimer le plan de cours...");
	    messages.put("ButtonPublishToolBarTooltip",
		    "Publier le plan de cours...");
	    messages.put("ButtonSaveToolBarTooltip", "Enregistrer le plan de cours...");
	    messages.put("ButtonViewToolBarTooltip", "Voir un aper�u du plan de cours...");
	    messages.put("ButtonCloseToolBarTooltip","Fermer la fen�tre de pr�visualisation...");
	    
	    messages.put("OsylToolbar", "Barre d''outils OpenSyllabus");

	    messages.put("OsylTreeView", "Vue arborescente d''OpenSyllabus");

	    messages.put("OsylWorkSpaceView", "Vue de l''espace de travail d''OPenSyllabus");

	    messages.put("RpcError", "*** Erreur du RPC ***");

	    messages.put("RpcSuccess", "*** Succ�s du RPC ***");

	    messages.put("Bienvenue", "Bienvenue!  Nous sommes le {0}.");

	    messages.put("cancel", "Annuler");

	    messages.put("delete", "Supprimer");

	    messages.put("edit", "�diter");
	    
	    messages.put("follow", "Suivre le lien");
	    
	    messages.put("addButton","Ajouter");
	    
	    messages.put("upButton", "Remonter");
	    
	    messages.put("addFileButton","Ajouter un fichier...");

	    messages.put("UtilityRemoteFileBrowser_directoryTag","(D)");
	    
	    messages.put("UtilityRemoteFileBrowser_fileTag","(F)");
	    
	    messages.put("UtilityRemoteFileBrowser_citationTag","(C)");
	    
	    messages.put("enterImgURL", "Tapez l'URL de l'image:");

	    messages.put("enterLinkURL", "Tapez l'URL du lien");

	    messages.put("fileUpload.addResource", "Ajout d'une ressource");

	    messages.put("fileUpload.selectDest", "S�lectionnez la destination: ");

	    messages.put("fileUpload.plsSelectFile", "Veuillez s�lectionner un fichier!");

	    messages.put("fileUpload.resNotSaved",
		    "Impossible d'enregistrer la ressource!");

	    messages.put("fileUpload.resSaved", "Ressource enregistr�e.");

	    messages
		    .put("fileUpload.unableAddRes",
			    "Impossible d''ajouter la ressource au contexte courant!\n\nD�tails: {0}");

	    messages.put("fileUpload.unableReadRemoteDir", "Impossible de lire le contenu du dossier distant");
	    
	    messages.put("FormatingToolbar", "Mise en forme");
	    
	    messages
		    .put("fileUpload.unableToGetSiteID",
			    "Erreur: Impossible d''obtenir le Site ID. La cr�ation de ressource ne fonctionnera pas.");

	    messages.put("courseOutlinePublication", "Publication d'un plan de cours");

	    messages.put("publish", "Publier");

	    messages.put("publishing", "Veuillez patienter: Publication en cours...");
	    
	    messages.put("close", "Fermer");
	    	    
	    messages.put("publishOK","Le plan de cours a �t� publi�");

	    messages.put("publishError","Impossible de publier le plan de cours");

	    messages.put("resourceEditor","D�tails de la ressource");

	    messages.put("resourceEditorSelectRubric","<choisissez une rubrique>");
	    
	    messages.put("resourceEditorTabCfg", "Configuration");

	    messages.put("resourceEditorTabDoc","Document");
	    
	    messages.put("rtt_background", "Surlignage");

	    messages.put("rtt_black", "noir");

	    messages.put("rtt_blue", "bleu");

	    messages.put("rtt_bold", "Mettre en gras");

	    messages.put("rtt_cancel", "Annuler");

	    messages.put("rtt_color", "couleur");

	    messages.put("rtt_createLink", "Cr�er un lien");

	    messages.put("rtt_font", "Police");

	    messages.put("rtt_foreground", "Couleur de police");

	    messages.put("rtt_green", "vert");

	    messages.put("rtt_hr", "Ins�rer r�glet horizontal");

	    messages.put("rtt_indent", "Indenter vers la droite");

	    messages.put("rtt_insertImage", "Ins�rer une image");

	    messages.put("rtt_italic", "En italique");

	    messages.put("rtt_justifyCenter", "Centrer");

	    messages.put("rtt_justifyLeft", "Justifier � gauche");

	    messages.put("rtt_justifyRight", "Justifier � droite");

	    messages.put("rtt_large", "Large");

	    messages.put("rtt_medium", "Moyen");

	    messages.put("rtt_normal", "Normal");

	    messages.put("rtt_ol", "Ins�rer une liste ordonn�e");

	    messages.put("rtt_outdent", "Indenter vers la gauche");

	    messages.put("rtt_red", "rouge");

	    messages.put("rtt_removeFormat", "Enlever le formattage");

	    messages.put("rtt_removeLink", "Enlever le lien");

	    messages.put("rtt_size", "Taille");

	    messages.put("rtt_small", "Petit");

	    messages.put("rtt_strikeThrough", "Barrer");

	    messages.put("rtt_subscript", "Indice");

	    messages.put("rtt_superscript", "Exposant");

	    messages.put("rtt_ul", "Ins�rer une liste non-ordonn�e");

	    messages.put("rtt_underline", "Souligner");

	    messages.put("rtt_validate", "Valider");

	    messages.put("rtt_white", "Blanc");

	    messages.put("rtt_xlarge", "T-Grand");

	    messages.put("rtt_xsmall", "T-Petit");

	    messages.put("rtt_xxlarge", "TT-Grand");

	    messages.put("rtt_xxsmall", "TT-Petit");

	    messages.put("rtt_yellow", "Jaune");
	    
	    messages.put("saveDialogBox.title", "Warning");
	    messages.put("saveDialogBox.content", "Do you want to save ?");

	    messages.put("save", "Enregistrer");

	    messages.put("savedOK", "Sauvegarde compl�t�e!");

	    messages.put("unableToInitServer",
		    "Impossible d''�tablir la communication avec le serveur.\r\n\r\nD�tails:\r\n{0}");

	    messages
		    .put(
			    "unableToLoadCO",
			    "Impossible de charger le plan de cours du serveur.\nLe contenu de d�veloppement sera utilis�.\r\n\r\nD�tails:\r\n{0}");

	    messages.put("ResDocumentConfigView_Welcome", "S�lectionnez une ressource ci-contre");

	    messages.put("ResDocumentConfigView_Display_Label", "Affichage");

	    messages.put("ResDocumentConfigView_Hide_CheckBox", "Cacher");

	    messages
		    .put("ResDocumentConfigView_Importance_Label", "Importance");

	    messages.put("ResDocumentConfigView_NotImportant_CheckBox","Pas Important");

	    messages.put("ResDocumentConfigView_Diffusion_Label","Niveau de diffusion");

	    messages.put("ResDocumentConfigView_Public_RadioButton", "Public");

	    messages.put("ResDocumentConfigView_Institution_RadioButton",
		    "Institution");

	    messages.put("ResDocumentConfigView_Attendee_RadioButton",
		    "�tudiant inscrit");

	    messages.put("UtilityRemoteFileBrowser_TitleLabel", 
		    "Fureteur de ressources");
	    
	    messages.put("UtilityRemoteFileBrowser_FolderPanelLabel",
		    "Regarder dans: ");
	    
	    messages.put("UtilityRemoteFileBrowser_FileListingLabel",
		    "Fichiers: ");
	    
	    messages.put("UtilityRemoteFileBrowser_LoadingLabel", 
	    	"Chargement...");
	    
	    messages.put("UtilityRemoteFileBrowser_Up_Button", "Nouveau...");
	    
	    messages.put("UtilityRemoteFileBrowser_Choose_Button", "Choisir");
	    
	    messages.put("UtilityRemoteFileBrowser_Selected_File", "Fichier s�lectionn�: ");
	    
	    messages.put("UtilityRemoteFileBrowser_Selected_File_None","aucun");
	    
	    
	    messages.put("ResDocumentConfigView_AssignmentCloseDate_TabLabel", "Date de remise");
	    
	    messages.put("ResDocumentConfigView_AssignmentOpenDate_TabLabel", "Date d''ouverture");
	    
	    messages.put("ResDocumentConfigView_AssignmentRating_Label", "Pourcentage");
	    
	    messages.put("ResProxAssignmentView_AttachDocLabel", "Document joint");
	    
	    messages.put("ResProxAssignmentView_CloseDateLabel", "Date de remise");
	    
	    messages.put("ResProxAssignmentView_OpenDateLabel", "Date d''ouverture");
	    
	    messages.put("OsylOKCancelDialog_Ok_Button","OK");
	    
	    messages.put("OsylOKCancelDialog_Cancel_Button","Annuler");
	    	    
	    messages.put("OsylOkCancelDialog_Delete_Title", "Attention!");
	    
	    messages.put("OsylOkCancelDialog_Title", "Attention!");
	    
	    messages.put("OsylOkCancelDialog_Delete_Content", "�tes-vous certain de vouloir supprimer cet item?");
	    
	    messages.put("OsylOkCancelDialog_Delete_Ok_Button", "Oui");
	    
	    messages.put("OsylOkCancelDialog_Delete_Cancel_Button", "Non");
	    
	    messages.put("OsylAlertDialog_Ok_Button", "OK");
	    
	    messages.put("OsylAlertDialog_Title", "Alerte");
	    
	    messages.put("saveBeforeQuit", "Vous �tes sur le point de quitter OpenSyllabus mais vous n'avez pas enregistr� les derni�res modifications.\nSouhaitez-vous enregistrer (bouton OK) ou quitter sans enregistrer (bouton Annuler) ?");

	    messages.put("RemovedContentUnit","Supprim�: '{0}'");
	    
	    messages.put("Preview.attendee_version", "Version des �tudiants inscrits");
	    
	    messages.put("Preview.public_version", "Version publique");
	    
	    
	    messages.put("MetaInfo.audience", "Niveau de diffusion");
	    
	    messages.put("MetaInfo.audience.attendee", "�tudiants inscrits");
	    
	    messages.put("MetaInfo.audience.onsite", "Institution");
	    
	    messages.put("MetaInfo.audience.public", "Public");
	    
	    messages.put("MetaInfo.audience.title", "Choisissez le niveau de diffusion de l'�l�ment");
	    
	    messages.put("MetaInfo.hidden", "Cach�");
	    
	    messages.put("MetaInfo.hide", "Cacher");
	    
	    messages.put("MetaInfo.hide.title", "Cocher pour cacher l'�l�ment, jusqu'� ce qu'il soit compl�t� par exemple");
	    
	    messages.put("MetaInfo.important", "Important");
	    
	    messages.put("MetaInfo.important.title", "Cocher pour d�marquer l'�l�ment");
	    
	    messages.put("MetaInfo.title", "Options");
	    
	    messages.put("EditorPopUp.options.rubric", "Rubrique");
	    
	    messages.put("EditorPopUp.options.rubric.choose", "Choisissez une rubrique");
	    
	    messages.put("EditorPopUp.options.rubric.choose.title", "Choisissez la rubrique dans laquelle l'�l�ment doit appara�tre");

	    messages.put("EditorPopUp.title", "�dition");
	    
	    messages.put("MetaInfo.requirement", "Niveau d'exigence");
	  //we don't want to display text by default for the requirement level
	    messages.put("MetaInfo.requirement.undefined", " "); 
	    messages.put("MetaInfo.requirement.mandatory", "Obligatoire");
	    messages.put("MetaInfo.requirement.recommended", "Recommand�");
	    messages.put("MetaInfo.requirement.complementary", "Compl�mentaire");
	    messages.put("MetaInfo.requirement.title", "Choisissez le niveau d'exigence de cet �l�ment");
	    

	    messages.put("MetaInfo.library", "Biblioth�que");
	
	    messages.put("MetaInfo.library.title", "Cocher si la r�f�rence est disponible a la biblioth�que de l''Institution");

	    messages.put("MetaInfo.bookstore", "Bookstore");

	    messages.put("MetaInfo.bookstore.title", "Cocher si la r�f�rence est disponible � la librairie de l''Institution");
	  
	    messages.put("MetaInfo.citation.exception", "La r�f�rence bibliographique ne doit pas �tre vide");
	    
	    messages.put("clickable_text", "Clickable text");
	    messages.put("description", "Description");
	    
	    messages.put("DocumentEditor.AddFolderPromt.InitFolderName", "Nouveau dossier");
	    messages.put("DocumentEditor.AddFolderPromt.InvalidName", "' n'est pas un nom valide!");
	    messages.put("DocumentEditor.AddFolderPromt.Promt", "SVP, tapez le nom du nouveau dossier");
	    messages.put("DocumentEditor.context", "2. Contexte d'utilisation");
	    
	    messages.put("DocumentEditor.title.add", "Ajouter un document");
	    messages.put("DocumentEditor.title.edit", "�diter un document");
	    
	    messages.put("DocumentEditor.document.details", "Voir les d�tails du document...");
	    messages.put("DocumentEditor.document.selection", "1. S�lectionnez un document");
	    messages.put("DocumentEditor.document.description", "Description du document");
	    messages.put("DocumentEditor.document.license", "Licence");
	    messages.put("DocumentEditor.save.name", "Sauvegarder");
	    messages.put("DocumentEditor.save.title", "Sauvegarder les modifications");
	    
	    messages.put("DocumentEditor.context", "2. Use context");
	    
	    messages.put("DocumentEditor.document.PropUpdateError", "La mise � jour des propri�t�s du document a �chou�.");
	    messages.put("DocumentEditor.document.PropUpdateSuccess", "La mise � jour des propri�t�s du document a r�ussi.");
	    
	    messages.put("Evaluation.EndDate","Date de fin / remise");
	    messages.put("Evaluation.StartDate","Date de d�but");
	    messages.put("Evaluation.deliverable","Livrable");
	    messages.put("Evaluation.location","Localisation");
	    messages.put("Evaluation.mode","Mode");
	    messages.put("Evaluation.name","Titre de l'instrument & �tape dans le cours");
	    messages.put("Evaluation.rating","Pond�ration de l'instrument");
	    messages.put("Evaluation.scope","Port�e");
	    messages.put("Evaluation.scope.tooltip","Sommatif = obligatoire, Formatif = facultatif");
	    messages.put("Evaluation.subtype","Type de remise");
	    messages.put("Evaluation.type","Type de l'instrument d'�valuation");
	    
	    messages.put("Link.label","Libell�");
	    messages.put("Link.url","URL");
	    messages.put("Link.description","Description du lien");
	    
	    messages.put("UpButton.title", "Remonter");
	    messages.put("DownButton.title", "Descendre");
	    
	    messages.put("Browser.upButton.tooltip","Remonter d'un niveau");
	    messages.put("Browser.addFolderButton.tooltip","Cr�er un dossier...");
	    messages.put("Browser.addFileButton.tooltip","Ajouter un fichier...");
	    messages.put("Browser.addCitationButton.tooltip","Ajouter une citation...");
	    
	    messages.put("Browser.selected_citation","Citation selectionn�e: ");
	    messages.put("Browser.selected_file","Fichier s�lectionn� : ");
	    
	    messages.put("CitationForm.createCitation", "Cr�ation d'une citation");
	    messages.put("CitationForm.editCitation", "�dition d'une citation");
	    messages.put("CitationForm.fillRequiredFields", "SVP, remplir les champs requis!");
	    messages.put("CitationEditor.title.add","Ajouter une r�f�rence bibliographique");
	    messages.put("CitationEditor.title.edit","�diter une r�f�rence bibliographique");
	    messages.put("CitationEditor.document.selection","1. S�lectionner une r�f�rence bibliographique");
	    messages.put("CitationEditor.document.details","Voir la r�f�rence bibliographique...");
	    messages.put(" CitationEditor.context","2. Contexte d'utilisation...");
	   
	    messages.put("selectRubric","<choisissez une rubrique>");
	    
	}
	return messages;
    }
}
