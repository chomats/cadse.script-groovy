package fr.imag.adele.cadse.script.groovy;

import java.net.URL;

import org.osgi.framework.Bundle;


public class BundlesClassLoader extends ClassLoader {
 
	
	private Bundle[] _bundles;

	public BundlesClassLoader(Bundle[] bundles) {
		_bundles = bundles;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		ClassNotFoundException lastE = null;
		for (Bundle b : _bundles) {
			try {
				return b.loadClass(name);
			} catch (ClassNotFoundException e) {
				lastE = e;
			}
		}
		if (lastE == null)
			lastE = new ClassNotFoundException(name);
		throw lastE;
	}
	
	
	@Override
	protected URL findResource(String name) {
		for (Bundle b : _bundles) {
			URL ret = b.getResource(name);
			if (ret != null) return ret;
		}
		return null;
	}
}
