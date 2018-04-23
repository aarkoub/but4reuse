package org.but4reuse.adapters.pstl.plantUML.actions;

import org.but4reuse.adaptedmodel.AdaptedModel;
import org.but4reuse.adaptedmodel.manager.AdaptedModelManager;
import org.but4reuse.adapters.pstl.plantUML.UMLComponentModelsExtractor;
import org.but4reuse.utils.ui.dialogs.URISelectionDialog;
import org.eclipse.contribution.visualiser.views.Menu;
import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Create PlantUML
 * 
 * @author tziadi
 */
public class CreateComponentUMLAction implements IViewActionDelegate {

	Menu menu;

	@Override
	public void run(IAction action) {
		// Get construction uri from user
		String out = "/projectName";
		IContainer output = AdaptedModelManager.getDefaultOutput();
		if (output != null) {
			out = output.getFullPath().toString();
		}
		URISelectionDialog inputDialog = new URISelectionDialog(Display.getCurrent().getActiveShell(),
				"UML component model container URI", "Insert container URI for UML component model ", "platform:/resource" + out + "/cvl/");
		if (inputDialog.open() != Dialog.OK) {
			return;
		}
		String constructionURI = inputDialog.getValue();
		AdaptedModel adaptedModel = AdaptedModelManager.getAdaptedModel();

		// Call the extractor
		UMLComponentModelsExtractor.createComponentModels(constructionURI, adaptedModel);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {
		menu = (Menu) view;
	}

}
