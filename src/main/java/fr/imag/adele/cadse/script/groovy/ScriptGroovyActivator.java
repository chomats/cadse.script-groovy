package fr.imag.adele.cadse.script.groovy;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class ScriptGroovyActivator extends Plugin {
	static BundleContext _cxt;
	
	public ScriptGroovyActivator() {
	}
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		_cxt = context;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		_cxt = null;
	}
}
