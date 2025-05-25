package com.aliuken.jobvacanciesapp.model.entity.factory;

import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.AuthUserCredentials;
import com.aliuken.jobvacanciesapp.model.entity.factory.superclass.AbstractEntityFactory;

public class AuthUserCredentialsFactory extends AbstractEntityFactory<AuthUserCredentials> {
	public AuthUserCredentialsFactory() {
		super();
	}

	@Override
	protected AuthUserCredentials createInstance() {
		return new AuthUserCredentials();
	}

	public static AuthUserCredentials createForSearchByExample(final Long id, final AuthUser firstRegistrationAuthUser, final AuthUser lastModificationAuthUser) {
		final AuthUserCredentials authUserCredentials = new AuthUserCredentials();
		authUserCredentials.setId(id);
		authUserCredentials.setFirstRegistrationAuthUser(firstRegistrationAuthUser);
		authUserCredentials.setLastModificationAuthUser(lastModificationAuthUser);
		return authUserCredentials;
	}
}