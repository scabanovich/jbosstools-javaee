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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.ui.JSTJobUiPlugin;
//import org.jboss.tools.common.java.generation.JavaBeanGenerator;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 * 
 */
public class NewBatchletWizardPage extends NewClassWizardPage {
	String defaultTypeName = null;

	protected StatusInfo fieldNameStatus = new StatusInfo();

	protected boolean mayBeRegisteredInBeansXML = true;

	public NewBatchletWizardPage() {
		setTitle(WizardMessages.NEW_BATCHLET_WIZARD_PAGE_NAME);
		setDescription(WizardMessages.NEW_BATCHLET_WIZARD_DESCRIPTION);
//		setImageDescriptor(CDIImages.getImageDescriptor(CDIImages.CDI_CLASS_IMAGE));
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
		defaultTypeName = null;
		setSuperClass("javax.batch.api.AbstractBatchlet", false);
//		setModifiers(getModifiers() | Flags.AccAbstract, true);

		doStatusUpdate();
	}

	TestElement t;
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		// createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);

		createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);

		// createMethodStubSelectionControls(composite, nColumns);

		createCustomFields(composite);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);
		
		Composite composite2 = new Composite(composite, SWT.NONE);
		GridData d = new GridData(GridData.FILL_HORIZONTAL);
		d.horizontalSpan = nColumns;
		composite2.setLayoutData(d);
		composite2.setLayout(new GridLayout(1, false));
		t = TestElement.TYPE.instantiate();
		SapphireForm form = new SapphireForm(composite2, t, DefinitionLoader.context(NewBatchArtifactWizard.class).sdef("testform").form("TestForm"));
		
		Listener l = new Listener() {
			
			@Override
			public void handle(Event event) {
				String m = t.validation().message();
				System.out.println(m);
				
			}
		};
		t.attach(l, "Ref");
		t.attach(l, "Properties");
		t.attach(l, "Properties/Name");
		t.attach(l, "Properties/Value");

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);

		// onInterceptorBindingChange();
		doStatusUpdate();
	}

	protected void createTypeMembers(IType newType,
			final ImportsManager imports, IProgressMonitor monitor)
			throws CoreException {
		createInheritedMethods(newType, true, true, imports,
				new SubProgressMonitor(monitor, 1));

		ISourceRange range = newType.getSourceRange();
		IBuffer buf = newType.getCompilationUnit().getBuffer();
		String lineDelimiter = StubUtility.getLineDelimiterUsed(newType
				.getJavaProject());
		StringBuffer sb = new StringBuffer();
		addAnnotations(imports, sb, lineDelimiter);
		buf.replace(range.getOffset(), 0, sb.toString());
		modifyMethodContent(newType, imports, monitor, lineDelimiter);
	}

	void addAnnotations(ImportsManager imports, StringBuffer sb,
			String lineDelimiter) {
		addDecoratorAnnotation(imports, sb, lineDelimiter);
	}

	protected void addDecoratorAnnotation(ImportsManager imports,
			StringBuffer sb, String lineDelimiter) {

		String typeName = "javax.inject.Named";
		int i = typeName.lastIndexOf('.');
		String name = typeName.substring(i + 1);
		imports.addImport(typeName);
		sb.append("@").append(name).append(lineDelimiter);					

	}

	protected void createCustomFields(Composite composite) {
//		createFieldNameField(composite);
	}

//	void editField(ICompilationUnit cu, IField m, String javatype, String fieldHeader,
//			String lineDelimiter) throws CoreException {
//		synchronized (cu) {
//			cu.reconcile(ICompilationUnit.NO_AST, true, null, null);
//		}
//		ISourceRange range = m.getSourceRange();
//		IBuffer buf = cu.getBuffer();
//		StringBuffer sb = new StringBuffer(lineDelimiter);
//		if (isAddComments()) {
//			String fieldComment = CodeGeneration.getFieldComment(cu,
//					javatype, m.getElementName(), lineDelimiter);
//			sb.append(fieldComment).append(lineDelimiter);
//		}
//		sb.append(fieldHeader);
//		String formattedContent = JavaBeanGenerator.codeFormat2(
//				CodeFormatter.K_CLASS_BODY_DECLARATIONS, sb.toString(), 1,
//				lineDelimiter, cu.getJavaProject());
//		if (formattedContent != null && formattedContent.startsWith("\t")) { //$NON-NLS-1$
//			formattedContent = formattedContent.substring(1);
//		}
//		buf.replace(range.getOffset(), range.getLength(), formattedContent);
//	}

//	protected IStatus superInterfacesChanged() {
//		List list = getSuperInterfaces();
//		if(list != null && !list.isEmpty()) {
//			setDefaultTypeName(list.get(0).toString());
//		}
//		StatusInfo result = (StatusInfo) super.superInterfacesChanged();
//		if (!result.isError()) {
//			if (list == null || list.isEmpty()) {
//				result.setError("Please select decorated type.");
//			}
//		}
//		return result;
//	}

	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] {
				fContainerStatus,
				isEnclosingTypeSelected() ? fEnclosingTypeStatus
						: fPackageStatus, fTypeNameStatus, fModifierStatus,
				fSuperClassStatus, fSuperInterfacesStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}

	protected void updateStatus(IStatus[] status) {
		if(!WizardMessages.NEW_BATCHLET_WIZARD_DESCRIPTION.equals(getDescription())) {
			setDescription(WizardMessages.NEW_BATCHLET_WIZARD_DESCRIPTION);
		}
		IStatus[] ns = new IStatus[status.length + 1];
		System.arraycopy(status, 0, ns, 0, status.length);
		ns[status.length] = fieldNameStatus;
		status = ns;
		updateStatus(StatusUtil.getMostSevere(status));
	}
	
	protected void modifyMethodContent(IType type, ImportsManager imports,
			IProgressMonitor monitor, String lineDelimiter) throws CoreException {
		IMethod[] ms = type.getMethods();
		for (int i = 0; i < ms.length; i++) {
			if(ms[i].isConstructor()) continue;
			ICompilationUnit cu = type.getCompilationUnit();
			synchronized (cu) {
				cu.reconcile(ICompilationUnit.NO_AST, true, null, null);
			}
			IBuffer buf = cu.getBuffer();
			ISourceRange range = ms[i].getSourceRange();

			int start = -1;
			int end = -1;
			StringBuffer sb = new StringBuffer();
			if("process".equals(ms[i].getElementName())) {			
				start = buf.getContents().indexOf("return", range.getOffset());
				if(start < 0 || start > range.getOffset() + range.getLength()) continue;
				start += 7;
				end =  buf.getContents().indexOf(";", start);
				if(end < 0) continue;
				end++;
			} else {
				continue;
			}
			imports.addImport("javax.batch.runtime.BatchStatus");
			sb.append("BatchStatus.COMPLETED.toString();");
			buf.replace(start, end - start, sb.toString());
		}
		
	}

	@Override
	public void setVisible(boolean visible) {
		if(!getControl().isVisible() && visible && fSuperInterfacesStatus.matches(IStatus.ERROR) && !fTypeNameStatus.matches(IStatus.ERROR)) {
			setDescription(fSuperInterfacesStatus.getMessage());
		}
		super.setVisible(visible);
	}

	@Override
	protected String getSuperInterfacesLabel() {
		return super.getSuperInterfacesLabel();
	}

	@Override
	protected IStatus packageChanged() {
		IStatus result = super.packageChanged();
		if(result != null && result.isOK()) {
			//***.setPackageFragment(getPackageFragment());
		}
		//validate custom fields here
		return result;
	}

	@Override
	protected IStatus typeNameChanged() {
		IStatus result = super.typeNameChanged();
		//validate custom fields here
		return result;
	}
}