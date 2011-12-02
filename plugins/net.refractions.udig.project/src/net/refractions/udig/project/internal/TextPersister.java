/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import net.refractions.udig.project.IPersister;

import org.eclipse.ui.IMemento;

/**
 * Persister for persisting text on blackboard.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class TextPersister extends IPersister {

    @Override
    public Class getPersistee() {
        return String.class;
    }

    @Override
    public Object load( IMemento memento ) {
        String text = memento.getString("text"); //$NON-NLS-1$
        return text;
    }

    @Override
    public void save( Object object, IMemento memento ) {
    	String text = (String) object;
        memento.putString("text", text); //$NON-NLS-1$
    }

}
