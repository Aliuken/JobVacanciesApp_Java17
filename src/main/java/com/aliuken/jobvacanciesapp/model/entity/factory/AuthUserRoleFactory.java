package com.aliuken.jobvacanciesapp.model.entity.factory;

import com.aliuken.jobvacanciesapp.model.entity.AuthUserRole;
import com.aliuken.jobvacanciesapp.model.entity.factory.superclass.AbstractEntityFactory;

public class AuthUserRoleFactory extends AbstractEntityFactory<AuthUserRole> {
	public AuthUserRoleFactory() {
		super();
	}

	@Override
	protected AuthUserRole createInstance() {
		return new AuthUserRole();
	}
}
