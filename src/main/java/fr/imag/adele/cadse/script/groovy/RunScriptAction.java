package fr.imag.adele.cadse.script.groovy;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.ui.MenuAction;

public class RunScriptAction extends MenuAction {
	public RunScriptAction() {
		super();
		init("Run groovy script", "Run groovy script", null, 0, null);
	}
	
	@Override
	public void run(IItemNode[] selection) throws CadseException {
		CadseDialog.open();
	}
}
