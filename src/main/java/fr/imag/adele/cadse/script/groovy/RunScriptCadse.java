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
