/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.command.CommandManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

@SuppressWarnings("nls")
public class SetGeomCommandTest {

    private TestHandler handler;
    private EditBlackboard bb;
    private EditGeom editGeom;
    private PrimitiveShape hole;
    private EditGeom editGeom2;
    private Layer layer;
    private SimpleFeature feature;
    private SimpleFeature feature2;

    @Before
    public void setUp() throws Exception {
        handler=new TestHandler(2);
        bb= handler.getEditBlackboard();
        editGeom = bb.newGeom("testing", null); //$NON-NLS-1$
        bb.addPoint(10,10, editGeom.getShell());
        bb.addPoint(20,10, editGeom.getShell());
        bb.addPoint(30,10, editGeom.getShell());
        
        hole = editGeom.newHole();
        bb.addPoint(15,10, hole);
        bb.addPoint(25,10, hole);
        bb.addPoint(35,10, hole);
        
        hole = editGeom.newHole();
        bb.addPoint(17,10, hole);
        bb.addPoint(27,10, hole);
        bb.addPoint(35,10, hole);
        
        editGeom2 = bb.newGeom("testing2", null); //$NON-NLS-1$
        
        bb.addPoint(10,15, editGeom2.getShell());
        bb.addPoint(20,15, editGeom2.getShell());
        bb.addPoint(30,15, editGeom2.getShell());
        
        hole = editGeom2.newHole();
        bb.addPoint(15,15, hole);
        bb.addPoint(25,15, hole);
        bb.addPoint(35,15, hole);
        
        hole = editGeom2.newHole();
        bb.addPoint(17,15, hole);
        bb.addPoint(27,15, hole);
        bb.addPoint(35,15, hole);
        
        handler.setCurrentShape(hole);
        handler.setCurrentState(EditState.CREATING);
        
        layer = (Layer) handler.getContext().getMap().getMapLayers().get(0);
        FeatureIterator<SimpleFeature> features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        feature = features.next();
        feature2=features.next();
        ((Map)handler.getContext().getMap()).getEditManagerInternal().setEditFeature(feature, layer);
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.behaviour.SetGeomCommand.run(IProgressMonitor)'
     */
    @Test
    public void testRun() throws Exception {
        IEditManager editManager = handler.getContext().getEditManager();
        
        assertEquals("Does the ID match",feature.getID(), editManager.getEditFeature().getID());
		assertEquals("Is the feature equal",feature.getDefaultGeometry(), editManager.getEditFeature().getDefaultGeometry());
        assertEquals("Is the layer equal",layer, editManager.getEditLayer());
        
        SelectFeatureAsEditFeatureCommand command = new SelectFeatureAsEditFeatureCommand(handler, feature2, layer, Point.valueOf(10,10));
               
        handler.getContext().sendSyncCommand(command);
        assertEquals(handler.getEditBlackboard().getGeoms().get(0), handler.getCurrentGeom());
        assertEquals(handler.getEditBlackboard().getGeoms().get(0).getShell(), handler.getCurrentShape());
        assertEquals(EditState.MODIFYING, handler.getCurrentState());
        assertFalse(bb.getGeoms().contains(editGeom));
        assertFalse(bb.getGeoms().contains(editGeom2));
        assertEquals(feature2.getDefaultGeometry(), editManager.getEditFeature().getDefaultGeometry());
        assertEquals( feature2.getID(), handler.getCurrentGeom().getFeatureIDRef().get());

        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        command.rollback(new NullProgressMonitor());
        assertEquals(editGeom2.getShell().getPoint(0), handler.getCurrentGeom().getShell().getPoint(0));
        assertEquals(editGeom2.getShell().getPoint(1), handler.getCurrentGeom().getShell().getPoint(1));
        assertEquals(hole.getPoint(0), handler.getCurrentShape().getPoint(0));
        assertEquals(hole.getPoint(1), handler.getCurrentShape().getPoint(1));
        assertTrue(handler.getCurrentGeom().getHoles().contains(handler.getCurrentShape()));
        assertEquals(EditState.CREATING, handler.getCurrentState());
        assertEquals(2, bb.getGeoms().size());
        assertEquals( editGeom2.getFeatureIDRef().get(), handler.getCurrentGeom().getFeatureIDRef().get());

        assertEquals(feature.getDefaultGeometry(), editManager.getEditFeature().getDefaultGeometry());
        assertEquals(layer, editManager.getEditLayer());
    }

}
