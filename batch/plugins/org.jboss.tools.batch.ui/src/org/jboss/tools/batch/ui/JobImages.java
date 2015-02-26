/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.common.ui.CommonUIImages;
import org.jboss.tools.common.ui.CommonUIPlugin;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JobImages extends CommonUIImages {
	private static CommonUIImages INSTANCE;
	
	static {
		try {
			URL bin = JSTJobUiPlugin.getDefault().getBundle().getEntry("/bin");
			String path = "org/jboss/tools/batch/ui/editor/internal/model/";
			if(bin != null) {
				path = "bin/" + path;
			}
			INSTANCE = new JobImages(new URL(JSTJobUiPlugin.getDefault().getBundle().getEntry("/"), path)); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			CommonUIPlugin.getDefault().logError(e);
		}
	}

	public static CommonUIImages getInstance() {
		return INSTANCE;
	}

	public static final ImageDescriptor ANALYZER_IMAGE = getImageDescriptor("partition.png"); //$NON-NLS-1$
	public static final ImageDescriptor BATCHLET_IMAGE = getImageDescriptor("batchlet.png"); //$NON-NLS-1$
	public static final ImageDescriptor CHECKPOINT_ALGORITHM_IMAGE = getImageDescriptor("checkpoint-algorithm.png"); //$NON-NLS-1$
	public static final ImageDescriptor COLLECTOR_IMAGE = getImageDescriptor("partition.png"); //$NON-NLS-1$
	public static final ImageDescriptor DECISION_IMAGE = getImageDescriptor("decision.png"); //$NON-NLS-1$
	public static final ImageDescriptor FLOW_IMAGE = getImageDescriptor("flow.png"); //$NON-NLS-1$
	public static final ImageDescriptor JOB_LISTENER_IMAGE = getImageDescriptor("listener.png"); //$NON-NLS-1$
	public static final ImageDescriptor MAPPER_IMAGE = getImageDescriptor("partition.png"); //$NON-NLS-1$
	public static final ImageDescriptor PROCESSOR_IMAGE = getImageDescriptor("processor.png"); //$NON-NLS-1$
	public static final ImageDescriptor PROPERTY_IMAGE = getImageDescriptor("property.png"); //$NON-NLS-1$
	public static final ImageDescriptor READER_IMAGE = getImageDescriptor("reader.png"); //$NON-NLS-1$
	public static final ImageDescriptor REDUCER_IMAGE = getImageDescriptor("partition.png"); //$NON-NLS-1$
	public static final ImageDescriptor STEP_LISTENER_IMAGE = getImageDescriptor("listener.png"); //$NON-NLS-1$
	public static final ImageDescriptor WRITER_IMAGE = getImageDescriptor("writer.png"); //$NON-NLS-1$

	public static Image getImage(String key) {
		return getImageDescriptor(key).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return getInstance().getOrCreateImageDescriptor(key);
	}

	protected JobImages(URL url) {
		super(url);
	}

	protected ImageRegistry getImageRegistry() {
		return JSTJobUiPlugin.getDefault().getImageRegistry();
	}

	public static Image getImageByElement(BatchArtifactType element) {
		return getImage(getImageDescriptorByElement(element));
	}

	public static ImageDescriptor getImageDescriptorByElement(BatchArtifactType element) {
		if(element == BatchArtifactType.BATCHLET) {
			return BATCHLET_IMAGE;
		} else if(element == BatchArtifactType.CHECKPOINT_ALGORITHM) {
			return CHECKPOINT_ALGORITHM_IMAGE;
		} else if(element == BatchArtifactType.DECIDER) {
			return DECISION_IMAGE;
		} else if(element == BatchArtifactType.ITEM_READER) {
			return READER_IMAGE;
		} else if(element == BatchArtifactType.ITEM_WRITER) {
			return WRITER_IMAGE;
		} else if(element == BatchArtifactType.ITEM_PROCESSOR) {
			return PROCESSOR_IMAGE;
		} else if(element == BatchArtifactType.PARTITION_MAPPER) {
			return MAPPER_IMAGE;
		} else if(element == BatchArtifactType.PARTITION_COLLECTOR) {
			return COLLECTOR_IMAGE;
		} else if(element == BatchArtifactType.PARTITION_ANALYZER) {
			return ANALYZER_IMAGE;
		} else if(element == BatchArtifactType.PARTITION_REDUCER) {
			return REDUCER_IMAGE;
		} else if(element == BatchArtifactType.JOB_LISTENER) {
			return JOB_LISTENER_IMAGE;
		} else if(element.getTag().equals(BatchConstants.TAG_STEP)) {
			return STEP_LISTENER_IMAGE;
		}
		return null;
	}
}
