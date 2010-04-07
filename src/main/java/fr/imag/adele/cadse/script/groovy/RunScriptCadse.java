package fr.imag.adele.cadse.script.groovy;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.resolver.PlatformAdmin;

import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.IItemNode;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import org.osgi.framework.Bundle;
import fr.imag.adele.cadse.core.impl.CadseCore;

public class RunScriptCadse {

	
	public static Object run(IItemNode[] selection, CadseRuntime[] cadse, Bundle[] bundlesLibs, File code) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException {
		
		Binding binding = new Binding();
		HashSet<Bundle> bundles = new HashSet<Bundle>(cadse.length+bundlesLibs.length);
		for (CadseRuntime cr : cadse) {
			Bundle b = Platform.getBundle(cr.getQualifiedName());
			if (b != null) {
				bundles.add(b);
				Class c;
				try {
					c = b.loadClass(cr.getCstQualifiedClassName());
					binding.setVariable(cr.getCSTName(), c);
				} catch (ClassNotFoundException e) {
				}
			}
		}
		bundles.addAll(Arrays.asList(bundlesLibs));// find bundles org.codehaus.groovy
		
		ClassLoader parent = new BundlesClassLoader(RunScriptCadse.class.getClassLoader(), (Bundle[]) bundles.toArray(new Bundle[bundles.size()]));
		GroovyClassLoader loader = new GroovyClassLoader(parent);
		
		binding.setVariable("CadseCore", CadseCore.class);
		binding.setVariable("lw", CadseCore.getLogicalWorkspace());
		binding.setVariable("selection", selection);
		GroovyShell shell = new GroovyShell(loader, binding);
		
		Script script = shell.parse(code);
		return script.run();

	}
	
	public static Object run(CadseRuntime[] cadses, String[] bundlesLibs,  String codeText) {
		Binding binding = new Binding();
		ArrayList<Bundle> bundles = new ArrayList<Bundle>(cadses.length+bundlesLibs.length);
		for (CadseRuntime cr : cadses) {
			Bundle b = Platform.getBundle(cr.getQualifiedName());
			if (b != null) {
				bundles.add(b);
				Class c;
				try {
					c = b.loadClass(cr.getCstQualifiedClassName());
					binding.setVariable(cr.getCSTName(), c);
				} catch (ClassNotFoundException e) {
				}
			}
		}
		for (String s : bundlesLibs) {
			Bundle b = Platform.getBundle(s);
			if (b != null)
				bundles.add(b);
		}
		
		ClassLoader parent = new BundlesClassLoader(RunScriptCadse.class.getClassLoader(), (Bundle[]) bundles.toArray(new Bundle[bundles.size()]));
		GroovyClassLoader loader = new GroovyClassLoader(parent);
		
		binding.setVariable("CadseCore", CadseCore.class);
		binding.setVariable("lw", CadseCore.getLogicalWorkspace());
		GroovyShell shell = new GroovyShell(loader, binding);

		Object value = shell.evaluate(codeText);
		return value;
	}
}
