/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fr.imag.adele.cadse.script.groovy;

import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DAbstractField;

public class DGroovyEditor extends DAbstractField<RuningInteractionController> implements ITextListener {
	GroovyEditor groovyEditor;
	Control _mainControl;
	private String _value = "";
	@Override
	public Control getMainControl() {
		return _mainControl;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Object[] { _value };
	}

	@Override
	public void createControl(Composite container, int hspan) {
		groovyEditor = new GroovyEditor();
		
		IEditorInput input = new IEditorInput() {
			
			@Override
			public Object getAdapter(Class adapter) {
				return null;
			}
			
			@Override
			public String getToolTipText() {
				return "text";
			}
			
			@Override
			public IPersistableElement getPersistable() {
				return null;
			}
			
			@Override
			public String getName() {
				return "script text";
			}
			
			@Override
			public ImageDescriptor getImageDescriptor() {
				return null;
			}
			
			@Override
			public boolean exists() {
				return false;
			}
		};
		groovyEditor.setInput(input);
		groovyEditor.createPartControl(container);
		_mainControl = groovyEditor.getViewer().getTextWidget();
		groovyEditor.getViewer().addTextListener(this);
	}

	@Override
	public void textChanged(TextEvent event) {
		_value = event.getText();
	}
	
	
	@Override
	public Object getVisualValue() {
		return _value;
	}
	
	
	

}
