package com.aliuken.jobvacanciesapp.model.entity.factory;

import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.factory.superclass.AbstractEntityFactory;

public class AuthUserFactory extends AbstractEntityFactory<AuthUser> {
	public AuthUserFactory() {
		super();
	}

	@Override
	protected AuthUser createInstance() {
		return new AuthUser();
	}

	public static AuthUser createForSearchByExample(final Long id, final AuthUser firstRegistrationAuthUser, final AuthUser lastModificationAuthUser) {
		final AuthUser authUser = new AuthUser();
		authUser.setId(id);
		authUser.setFirstRegistrationAuthUser(firstRegistrationAuthUser);
		authUser.setLastModificationAuthUser(lastModificationAuthUser);
		return authUser;
	}
}
