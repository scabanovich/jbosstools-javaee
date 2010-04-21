 /*******************************************************************************
  * Copyright (c) 2007-2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2.util;

import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.jsf.JSFModelPlugin;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class JSF2ResourceUtil {

	public static final String JSF2_URI_PREFIX = "http://java.sun.com/jsf/composite"; //$NON-NLS-1$

	public static final String COMPONENT_RESOURCE_PATH_KEY = "component_resource_path_key"; //$NON-NLS-1$

	public static boolean isJSF2CompositeComponentExists(IProject project,
			IDOMElement jsf2Element) {
		ElementImpl elementImpl = (ElementImpl) jsf2Element;
		String nameSpaceURI = elementImpl.getNamespaceURI();
		if (nameSpaceURI == null || nameSpaceURI.indexOf(JSF2_URI_PREFIX) == -1) {
			return false;
		}
		String nodeName = jsf2Element.getNodeName();
		if (nodeName.lastIndexOf(':') != -1) {
			nodeName = nodeName.substring(nodeName.lastIndexOf(':') + 1);
		}
		String relativeLocation = "/resources" + nameSpaceURI.replaceFirst( //$NON-NLS-1$
				JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null) {
			IVirtualFolder webRootFolder = component.getRootFolder().getFolder(
					new Path("/")); //$NON-NLS-1$
			IContainer folder = webRootFolder.getUnderlyingFolder();
			IPath path = folder.getFullPath().append(relativeLocation).append(
					"/" + nodeName + ".xhtml"); //$NON-NLS-1$ //$NON-NLS-2$
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (file.exists()) {
				return true;
			}
		}
		return searchInClassPath(project, "META-INF" + relativeLocation //$NON-NLS-1$
				+ "/" + nodeName + ".xhtml"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static IFile createJSF2CompositeComponent(IProject project,
			IPath resourceRelativePath) throws CoreException {
		if (!project.exists()) {
			return null;
		}
		if (!project.isAccessible()) {
			try {
				project.open(new NullProgressMonitor());
			} catch (CoreException e) {
				JSFModelPlugin.getPluginLog().logError(e);
			}
		}
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null) {
			IVirtualFolder webRootFolder = component.getRootFolder().getFolder(
					new Path("/")); //$NON-NLS-1$
			IContainer folder = webRootFolder.getUnderlyingFolder();
			IFolder webFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(folder.getFullPath());
			IFolder resourcesFolder = webFolder.getFolder("resources"); //$NON-NLS-1$
			NullProgressMonitor monitor = new NullProgressMonitor();
			if (!resourcesFolder.exists()) {
				resourcesFolder.create(true, true, monitor);
			}
			String[] segments = resourceRelativePath.segments();
			IFolder componentPathFolder = resourcesFolder;
			for (int i = 0; i < segments.length - 1; i++) {
				componentPathFolder = componentPathFolder
						.getFolder(segments[i]);
				if (!componentPathFolder.exists()) {
					componentPathFolder.create(true, true, monitor);
				}
			}
			IFile file = componentPathFolder
					.getFile(segments[segments.length - 1]);
			if (!file.exists()) {
				file.create(JSF2TemplateManager.getManager()
						.createStreamFromTemplate("composite.xhtml"), true, //$NON-NLS-1$
						monitor);
			}
			return file;
		}
		return null;
	}

	private static boolean searchInClassPath(IProject project,
			String classPathResource) {
		IJavaProject javaProject = JavaCore.create(project);
		try {
			for (IPackageFragmentRoot fragmentRoot : javaProject
					.getAllPackageFragmentRoots()) {
				if (fragmentRoot instanceof JarPackageFragmentRoot) {
					JarPackageFragmentRoot jarPackageFragmentRoot = (JarPackageFragmentRoot) fragmentRoot;
					ZipEntry zipEntry = jarPackageFragmentRoot.getJar()
							.getEntry(classPathResource);
					if (zipEntry != null) {
						return true;
					}
				}
			}
		} catch (JavaModelException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		return false;
	}

}
