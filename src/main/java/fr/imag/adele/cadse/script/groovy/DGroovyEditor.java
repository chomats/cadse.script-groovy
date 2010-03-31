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
