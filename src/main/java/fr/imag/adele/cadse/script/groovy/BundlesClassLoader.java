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

import java.net.URL;

import org.osgi.framework.Bundle;


public class BundlesClassLoader extends ClassLoader {
 
	
	private Bundle[] _bundles;

	public BundlesClassLoader(ClassLoader parent, Bundle[] bundles) {
		super(parent);
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
