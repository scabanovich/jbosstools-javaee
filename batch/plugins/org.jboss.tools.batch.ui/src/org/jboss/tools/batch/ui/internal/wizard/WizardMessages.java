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

import org.eclipse.osgi.util.NLS;

public class WizardMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.batch.ui.internal.wizard.messages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, WizardMessages.class);
	}

	public static String NEW_BATCHLET_WIZARD_TITLE;
	public static String NEW_BATCHLET_WIZARD_PAGE_NAME;
	public static String NEW_BATCHLET_WIZARD_DESCRIPTION;

}
