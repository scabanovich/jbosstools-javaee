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
package org.jboss.tools.jsf.project.capabilities;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;

public class AddJSFCapabilitiesSupport extends SpecialWizardSupport {
	static String ATTR_CAPABILITY = "capability";
	CapabilitiesPerformer performer = new CapabilitiesPerformer();
	
	public AddJSFCapabilitiesSupport() {}
	
    public String getHelpKey() {
    	if(action == null) return null;
    	//Let it be the same for all entities
        return action.getName() + "_" + getStepId();
    }

    public void reset() {
		performer.init(getTarget().getModel());
		getProperties().put("CapabilitiesPerformer", performer);
	}
	
	void setCapabilitiList() {
		XModelObject[] os = JSFCapabilities.getInstance().getChildren();
		String[] options = new String[os.length == 0 ? 1 : os.length];
		for (int i = 0; i < os.length; i++) {
			options[i] = os[i].getAttributeValue("name");
		}
		if(os.length == 0) options[0] = "";
		setValueList(0, ATTR_CAPABILITY, options);
		setAttributeValue(0, ATTR_CAPABILITY, options[0]);
	}

	public void action(String name) throws XModelException {
		if(FINISH.equals(name)) {
			boolean b = executeWithContext();
			if(b) setStepId(1);
		} else if(CLOSE.equals(name)) {
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		}
	}

    public String[] getActionNames(int stepId) {
    	if(stepId == 0) return new String[]{FINISH, CANCEL, HELP};
        return new String[]{CLOSE, HELP};
    }

    protected boolean executeWithContext() throws XModelException {
    	IRunnableContext context = (IRunnableContext)getProperties().get("IRunnableContext");
    	final Executor executor = new Executor();
    	try {
    		context.run(false, true, executor);
    	} catch (InvocationTargetException e) {
    		throw new XModelException(e);
    	} catch (InterruptedException e) {
    		throw new XModelException(e);
    	}
    	if(executor.exception != null) XModelException.rethrow(executor.exception);
    	getProperties().put("addedCapabilities", (executor.added != null) ? executor.added : new String[0]);
    	return !executor.cancelled;
    }
    
    class Executor implements IRunnableWithProgress {
    	Exception exception = null;
    	String[] added;
    	boolean cancelled = false;
    	
    	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
	    	monitor.beginTask("" + getTitle(), getTaskCount());
	    	try {
	    		XJob.waitForJob(true);
	    	} catch (InterruptedException e) {
	    		//ignore 
	    	}
	    	PerformerContext context = new PerformerContext(monitor);
			try {
				IPerformerItem[] items = performer.getChildren();
		    	for (int i = 0; i < items.length; i++) {
		    		if(!items[i].isSelected()) continue;
		    		if(!items[i].execute(context)) {
		    			//do nothing
		    		}
	    			monitor.worked(getTaskCount(items[i], 0));
		    	}
			} catch (CoreException e) {
				exception = e;
				monitor.setCanceled(true);
			}finally {
				monitor.done();
				added = (String[])context.changeList.toArray(new String[0]);
				cancelled = monitor.isCanceled();
			}
		}
    }

    public String getStepImplementingClass(int stepId) {
    	if(stepId == 0) {
    		return "org.jboss.tools.jsf.ui.wizard.capabilities.AddCapabilitiesScreenOne";
    	} else if(stepId == 1) {
    		return "org.jboss.tools.jsf.ui.wizard.capabilities.AddCapabilitiesScreenTwo";
    	}
        return "org.jboss.tools.common.model.ui.wizards.special.SpecialWizardStep";
    }
    
    int getTaskCount() {
    	return getTaskCount(performer, 0);
    }
    
    int getTaskCount(IPerformerItem item, int count) {
    	if(item.isSelected()) count++;
    	IPerformerItem[] cs = item.getChildren();
    	if(cs != null) for (int i = 0; i < cs.length; i++) {
    		count = getTaskCount(cs[i], count);
    	}
    	return count;
    }

    public boolean isActionEnabled(String name) {
    	if(getStepId() == 0 && FINISH.equals(name)) {
        	IPerformerItem[] cs = performer.getChildren();
        	for (int i = 0; i < cs.length; i++) {
        		if(hasSelected(cs[i])) return true;
        	}
    		return false;
    	}
    	return super.isActionEnabled(name);
    }

    boolean hasSelected(IPerformerItem item) {
    	if(!item.isSelected()) return false;
    	IPerformerItem[] cs = item.getChildren();
    	if(cs.length == 0) return true;
    	for (int i = 0; i < cs.length; i++) {
    		if(hasSelected(cs[i])) return true;
    	}
    	return false;
    }
}
