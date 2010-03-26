package fr.imag.adele.cadse.script.groovy;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.resolver.PlatformAdmin;

import fr.imag.adele.cadse.core.CadseRuntime;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;

import org.osgi.framework.Bundle;
import fr.imag.adele.cadse.core.impl.CadseCore;

public class RunScriptCadse {

	
	Object run(CadseRuntime[] cadse, String[] bundlesLibs, URL code) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException {
		
		ArrayList<Bundle> bundles = new ArrayList<Bundle>(cadse.length+bundlesLibs.length);
		for (CadseRuntime cr : cadse) {
			Bundle b = Platform.getBundle(cr.getQualifiedName());
			if (b != null)
				bundles.add(b);
		}
		for (String s : bundlesLibs) {
			Bundle b = Platform.getBundle(s);
			if (b != null)
				bundles.add(b);
			
			
		}
		
		ClassLoader parent = new BundlesClassLoader((Bundle[]) bundles.toArray(new Bundle[bundles.size()]));
		GroovyClassLoader loader = new GroovyClassLoader(parent);
		Class groovyClass = loader.parseClass(new File("src/test/groovy/script/HelloWorld.groovy"));

		// let's call some method on an instance
		GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
		Object[] args = {};
		return groovyObject.invokeMethod("run", args);

	}
	
	Object run(CadseRuntime[] cadses, String[] bundlesLibs,  String codeText) {
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
		
		ClassLoader parent = new BundlesClassLoader((Bundle[]) bundles.toArray(new Bundle[bundles.size()]));
		GroovyClassLoader loader = new GroovyClassLoader(parent);
		
		binding.setVariable("CadseCore", CadseCore.class);
		binding.setVariable("lw", CadseCore.getLogicalWorkspace());
		GroovyShell shell = new GroovyShell(loader, binding);

		Object value = shell.evaluate(codeText);
		return value;
	}
}
