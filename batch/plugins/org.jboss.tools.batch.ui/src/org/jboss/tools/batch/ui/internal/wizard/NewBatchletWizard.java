/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.batch.ui.internal.wizard;

import org.eclipse.jdt.ui.wizards.NewClassWizardPage;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewBatchletWizard extends NewBatchArtifactWizard {

	public NewBatchletWizard() {
		setWindowTitle(WizardMessages.NEW_BATCHLET_WIZARD_TITLE);
	}

	/*
	 * @see Wizard#createPages
	 */
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = new  NewBatchletWizardPage();
			((NewClassWizardPage)fPage).init(getSelection());
			initPageFromAdapter();
		}
		addPage(fPage);
	}

	/*(non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return !fPage.isEnclosingTypeSelected();
	}

	public boolean performFinish() {
		System.out.println(" " + ((NewBatchletWizardPage)fPage).t.getRef());
		boolean res = super.performFinish();

		return res;
	}

}
