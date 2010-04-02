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
 */
package fr.imag.adele.cadse.script.groovy;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import fede.workspace.tool.view.node.CategoryNode;
import fede.workspace.tool.view.node.FilteredItemNode;
import fede.workspace.tool.view.node.FilteredItemNodeModel;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.FilteredItemNode.Category;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemShortNameComparator;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.impl.ui.mc.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IActionPage;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.dialog.SWTDialog;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_AbstractForChecked;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForCheckedViewer;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForChooseFile;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_TreeModel;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DCheckedListUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DChooseFileUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DGridUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DListUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DSashFormUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTabUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTextUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTreeModelUI;

public class CadseDialog extends SWTDialog {

	int							allreadyselected	= 0;
	Item						selectedItem		= null;
	HashSet<CadseRuntime>		selected			= new HashSet<CadseRuntime>();
	HashSet<Bundle>				bundles				= new HashSet<Bundle>();


	protected DTabUI										_fieldMain;
	protected DTreeModelUI									_fieldCadseRuntimes;
	protected DCheckedListUI<IC_ForCheckedViewer>			_fieldBundles;
	protected DChooseFileUI									_fieldCodeGroovy;

	private CadseRuntime[][]	ret;

	public class MyMC_AttributesItem extends MC_AttributesItem {

		@Override
		public Object getValue() {
			if (getItem() == null) {
				return "";
			}
			Object _ret = super.getValue();
			if (_ret == null) {
				return "";
			}
			return _ret;
		}
		
		@Override
		public Item getItem() {
			return selectedItem;
		}

		@Override
		public void notifieValueChanged(UIField field, Object value) {
			// read only value
		}
		
		@Override
		public boolean validValueChanged(UIField field, Object visualValue) {
			// read only value
			return false;
		}
		
		@Override
		public boolean validValue(UIField field, Object value) {
			// read only value
			return false;
		}
	}

	public class MyActionPage extends AbstractActionPage {

		@Override
		public void doFinish(UIPlatform ui, Object monitor) throws Exception {
		}
	}

	public class IC_CadseRutime extends IC_TreeModel {

		@Override
		public ItemType getType() {
			return null;
		}

		/**
		 * Crete the structured model to show All item of type CadseRuntime
		 * Category extends the item destination of link of type
		 * CadseRuntime::extends Category 'extended by' the item source of
		 * incomings link of type CadseRuntime::extends for a node of type
		 * CadseRuntime shows the above category...
		 * 
		 */

		@Override
		protected FilteredItemNodeModel getTreeModel() {
			if (model == null) {
				model = new FilteredItemNodeModel();
				// en premier on rajoute les insances de cadse runtime trier par
				// le nom
				model.addItemFromItemTypeEntry(null, CadseGCST.CADSE, ItemShortNameComparator.INSTANCE);
//
//
//				// on lie les deux category à un instance de ce Cadseruntime
//				model.addCategories(CadseGCST.CADSE, categoryExtendsTo, categoryExtendedBy);
//				model.addItemFromLinkTypeEntry(categoryExtendsTo, CadseGCST.CADSE_lt_EXTENDS,
//						ItemShortNameComparator.INSTANCE, false, false);
//				model.addItemFromLinkTypeEntry(categoryExtendedBy, CadseGCST.CADSE_lt_EXTENDS,
//						ItemShortNameComparator.INSTANCE, false, true);

			}
			return model;
		}

		public Object[] getSources() {
			return CadseCore.getLogicalWorkspace().getCadseRuntime();
		}

	}

	public class MC_CadseRuntime extends AbstractModelController {

		public MC_CadseRuntime(Item desc) {
			super(desc);
		}

		@Override
		public Object getValue() {
			ArrayList<CadseRuntime> executedCadse = new ArrayList<CadseRuntime>();
			return executedCadse.toArray(new CadseRuntime[executedCadse.size()]);
		}

		@Override
		public void notifieValueChanged(UIField field, Object value) {
		}

		@Override
		public void initAfterUI(UIField field) {
			DTreeModelUI<?> runningField = _swtuiPlatforms.getRunningField(field, _page);
			TreeViewer viewer = runningField.getTreeViewer();
			viewer.addFilter(new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof ItemNode) {
						Item item = ((ItemNode)element).getItem();
						return item != null && item.getType() != CadseGCST.CADSE_DEFINITION;
					}
					return true;
				}
			});
		}

		@Override
		public void notifieSubValueAdded(UIField field, Object added) {
		}

		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {
			if (removed instanceof IItemNode) {
				IItemNode n = (IItemNode) removed;
				if (n.getItem() != null && n.getItem().getType() == CadseGCST.CADSE) {
					selected.remove(n.getItem());
				}
			}
		}

	}
	
	public class MC_Bundles extends AbstractModelController {

		public MC_Bundles() {
			super(null);
		}

		@Override
		public Object getValue() {
			ArrayList<CadseRuntime> executedCadse = new ArrayList<CadseRuntime>();
			for (CadseRuntime cadseRuntime : CadseCore.getLogicalWorkspace().getCadseRuntime()) {
				if (cadseRuntime.isExecuted()) {
					executedCadse.add(cadseRuntime);
				}
			}
			allreadyselected = executedCadse.size();

			return executedCadse.toArray(new CadseRuntime[executedCadse.size()]);
		}

		@Override
		public void notifieValueChanged(UIField field, Object value) {
		}

		@Override
		public void notifieSubValueAdded(UIField field, Object added) {
			bundles.add((Bundle) added);
		}

		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {
			if (removed instanceof IItemNode) {
				IItemNode n = (IItemNode) removed;
				if (n.getItem() != null && n.getItem().getType() == CadseGCST.CADSE) {
					selected.remove(n.getItem());
				}
			}
		}
	}
	
	public class IC_Bundles extends IC_AbstractForChecked implements IC_ForCheckedViewer {

		public Object[] getSources() {
			return ScriptGroovyActivator._cxt.getBundles();
		}
		
		@Override
		public Image toImageFromObject(Object obj) {
			return null;
		}
		@Override
		public String toStringFromObject(Object obj) {
			if (obj instanceof Bundle) {
				return ((Bundle)obj).getSymbolicName();
			}
			return "??";
		}
	}
	
	public class MC_CodeGroovy extends AbstractModelController {

		public MC_CodeGroovy() {
			super(null);
		}

		@Override
		public Object getValue() {
			ArrayList<CadseRuntime> executedCadse = new ArrayList<CadseRuntime>();
			for (CadseRuntime cadseRuntime : CadseCore.getLogicalWorkspace().getCadseRuntime()) {
				if (cadseRuntime.isExecuted()) {
					executedCadse.add(cadseRuntime);
				}
			}
			allreadyselected = executedCadse.size();

			return executedCadse.toArray(new CadseRuntime[executedCadse.size()]);
		}

		@Override
		public void notifieValueChanged(UIField field, Object value) {
		}

		@Override
		public void notifieSubValueAdded(UIField field, Object added) {
			bundles.add((Bundle) added);
		}

		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {
			if (removed instanceof IItemNode) {
				IItemNode n = (IItemNode) removed;
				if (n.getItem() != null && n.getItem().getType() == CadseGCST.CADSE) {
					selected.remove(n.getItem());
				}
			}
		}

	}
	
	public class IC_CodeGroovy extends IC_ForChooseFile {

	}

	/**
	 * Create the dialog structure... DSashFormUI DGrillUI FieldExtends DGrillUI
	 * fieldTWVersion fieldDescription
	 * 
	 * @param ret
	 * @generated
	 */
	public CadseDialog(SWTUIPlatform swtuiPlatforms, CadseRuntime[][] ret) {
		super(swtuiPlatforms, "Executed CADSEs", "You can execute other CADSEs by validating the checkboxes bellow.");
		this.ret = ret;
		this._fieldCadseRuntimes = createRuntime(true);
		this._fieldBundles = createBundles();
		this._fieldCodeGroovy = _swtuiPlatforms.createDChooseFileUI(_page, "#code", "Code groovy", EPosLabel.top, new MC_CodeGroovy(), new IC_CodeGroovy(), "select a groovy script");
		MyMC_AttributesItem defaultMc = new MyMC_AttributesItem();

		this._fieldMain = _swtuiPlatforms.createField(_page, "#main", "", EPosLabel.none, defaultMc, null,new DTabUI<RuningInteractionController>(),
				CadseGCST.DISPLAY, _fieldCadseRuntimes, _fieldBundles, _fieldCodeGroovy);
		// add main field
		addLast(_fieldMain);

		registerListener();
	}

	/**
	 * Register listener or validator if need
	 */
	protected void registerListener() {
		// fieldExtends.addValidateContributor(this);
	}

	/**
	 * When a cadseruntime is selected, set the selected item and reset the
	 * values of fields
	 * 
	 * @param selectedItem
	 */
	public void setSelectedItem(Item selectedItem) {
		this.selectedItem = selectedItem;
	}

	/**
	 * Create a tree field to show CadseModel tree
	 */
	public DTreeModelUI createRuntime(boolean checkBox) {
		return _swtuiPlatforms.createTreeModelUI(_page, "#list", "Cadse", EPosLabel.top, new MC_CadseRuntime(null),
				new IC_CadseRutime(), checkBox);
	}

	/**
	 * Create a text field to display the description's CadseRuntime item
	 */
	public DCheckedListUI<IC_ForCheckedViewer> createBundles() {
		final DCheckedListUI<IC_ForCheckedViewer> field = _swtuiPlatforms.createField(_page, "#Bundles", "Bundles", EPosLabel.top, new MC_Bundles(), (IC_ForCheckedViewer) new IC_Bundles(),
				new DCheckedListUI<IC_ForCheckedViewer>(), CadseGCST.DCHECKED_LIST);
		return field;
	}

	/**
	 * Create a text field to display the version's CadseRuntime item
	 */
	public DTextUI createFieldTWVersion() {
		return _swtuiPlatforms.createTextUI(_page, CadseGCST.ITEM_at_TW_VERSION_, "version", EPosLabel.left,
				new MyMC_AttributesItem(), null, 1, false, false, false, false, true, null);
	}

	/**
	 * Open dialog.
	 * 
	 * @param askToErase
	 *            the ask to erase
	 * 
	 * @return true, if successful
	 * 
	 */
	static public void open() {
		final CadseRuntime[][] ret = new CadseRuntime[1][];
		ret[0] = null;
		CadseDialog d = new CadseDialog(new SWTUIPlatform(), ret);
		d.open(null);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected IActionPage getFinishAction() {
		return new MyActionPage();
	}

}
