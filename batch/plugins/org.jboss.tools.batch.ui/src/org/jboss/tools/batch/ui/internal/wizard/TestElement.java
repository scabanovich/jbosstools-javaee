/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.ui.internal.wizard;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface TestElement extends Element {

	ElementType TYPE = new ElementType( TestElement.class );

	@Label( standard = "ref" )
	@XmlBinding( path = "@ref" )
	@Required
	ValueProperty PROP_REF = new ValueProperty( TYPE, "Ref" );

	Value<String> getRef();
	void setRef( String ref);


	@Type( base = Property.class)
	@Label( standard = "properties" )
	@XmlListBinding( path = "" )
	ListProperty PROP_PROPERTIES = new ListProperty(TYPE, "Properties");

	ElementList<Property> getProperties();

}
