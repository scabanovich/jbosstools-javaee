/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.seam.ui.test.wizard;

import junit.framework.TestCase;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author eskimo
 *
 */
public class SeamFormNewWizardTest extends TestCase {

	/**
	 * 
	 */
	public void testSeamFormNewWizardInstanceIsCreated() {
		IWizard
		aWizard = WorkbenchUtils.findWizardByDefId(
				ISeamUiConstants.NEW_SEAM_FORM_WIZARD_ID);
		
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				aWizard);
		IWizardPage startPage = aWizard.getStartingPage();
	}
}
