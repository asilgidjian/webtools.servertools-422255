/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.task.*;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewServerWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.TaskWizard;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard used to select a server from various lists.
 */
public class SelectServerWizard extends TaskWizard {
	protected static NewServerWizardFragment task;

	/**
	 * SelectServerWizard constructor comment.
	 */
	public SelectServerWizard(final IModule module, final String launchMode) {
		super(ServerUIPlugin.getResource("%wizSelectServerWizardTitle"), new WizardFragment() {
			protected void createChildFragments(List list) {
				task = new NewServerWizardFragment(module, launchMode);
				list.add(task);
				list.add(new FinishWizardFragment(new TempSaveRuntimeTask()));
				list.add(new FinishWizardFragment(new TempSaveServerTask()));
				list.add(new ModifyModulesWizardFragment(module));
				list.add(new TasksWizardFragment());
				list.add(new FinishWizardFragment(new SaveRuntimeTask()));
				list.add(new FinishWizardFragment(new SaveServerTask()));
				list.add(new FinishWizardFragment(new Task() {
					public void execute(IProgressMonitor monitor) throws CoreException {
						
						try {
							IServer server = (IServer) getTaskModel().getObject(ITaskModel.TASK_SERVER);
							ServerUIPlugin.getPreferences().addHostname(server.getHost());
						} catch (Exception e) {
							// ignore
						}
					}
				}));
			}
		});
	
		setNeedsProgressMonitor(true);
	}

	/**
	 * Return the server.
	 * @return org.eclipse.wst.server.core.model.IServer
	 */
	public IServer getServer() {
		try {
			return (IServer) getRootFragment().getTaskModel().getObject(ITaskModel.TASK_SERVER);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isPreferredServer() {
		if (task == null)
			return false;
		return task.isPreferredServer();
	}
}