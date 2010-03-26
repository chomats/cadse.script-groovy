package fr.imag.adele.cadse.script.groovy;

import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

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
