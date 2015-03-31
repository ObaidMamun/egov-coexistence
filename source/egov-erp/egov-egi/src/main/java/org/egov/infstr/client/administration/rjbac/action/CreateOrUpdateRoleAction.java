/*
 * @(#)CreateOrUpdateRoleAction.java 3.0, 18 Jun, 2013 3:43:29 PM
 * Copyright 2013 eGovernments Foundation. All rights reserved. 
 * eGovernments PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.egov.infstr.client.administration.rjbac.action;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Role;
import org.egov.infra.admin.master.service.RoleService;
import org.egov.infstr.client.EgovAction;
import org.egov.lib.rrbac.model.Action;
import org.egov.lib.rrbac.services.RbacService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOrUpdateRoleAction extends EgovAction {
	private static final Logger LOG = LoggerFactory.getLogger(CreateOrUpdateRoleAction.class);
	private RbacService rbacService;
	private RoleService roleService;

	public void setRbacService(final RbacService rbacService) {
		this.rbacService = rbacService;
	}

	public void setRoleService(final RoleService roleService) {
		this.roleService = roleService;
	}

	@Override
	public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest req, final HttpServletResponse res) {

		String target = "failure";
		if (!this.isTokenValid(req)) {
			return mapping.findForward(target);
		}
		final RoleActionForm roleActionform = (RoleActionForm) form;

		try {

			if (org.apache.commons.lang.StringUtils.isBlank(roleActionform.getRoleId())) {
				throw new EGOVRuntimeException("Role Id not found");
			}

			final Role role = this.roleService.getRoleById(Long.valueOf(roleActionform.getRoleId()));
			
			// Deleting role action mappings for those actions which are unchecked.
			final Set<Integer> delActionsSet = (Set<Integer>) req.getSession().getAttribute("delActions");

			if ((delActionsSet != null) && !delActionsSet.isEmpty()) {
				for (final Integer actionId : delActionsSet) {
					if (actionId != null) {
						final Action action = this.rbacService.getActionById(actionId);
						if ((action != null) && (role != null)) {
						 // This is commented while rewriting role master screen
						// code must be corrected while rewriting this screen
						//	action.removeRole(role);
						}
					}
				}
			}
			
			// The selected actions are mapped to role.
			if ((role != null) && (roleActionform.getActionId() != null) && (roleActionform.getActionId().length > 0)) {
				for (final String actionId : roleActionform.getActionId()) {
					if (org.apache.commons.lang.StringUtils.isNotBlank(actionId)) {
					 // This is commented while rewriting role master screen
		                        // code must be corrected while rewriting this screen
						//this.rbacService.getActionById(Integer.valueOf(actionId)).addRole(role);
					}
				}
			}

			
			target = "success";
			req.setAttribute("MESSAGE", "Role to Action mapping has successfully completed.");
		} catch (final Exception c) {
			LOG.error("Error occurred while setting Role to Action mapping",c);
			req.setAttribute("MESSAGE", "Could not complete the Role to Action mapping due to some internal server error");
			throw new EGOVRuntimeException("Error occurred while setting Role to Action mapping");
		}
		if (target.equals("success")) {
			this.resetToken(req);
		}
		return mapping.findForward(target);
	}
}
