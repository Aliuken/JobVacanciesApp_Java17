package com.aliuken.jobvacanciesapp.model.entity.factory.superclass;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.aliuken.jobvacanciesapp.model.entity.superclass.AbstractEntity;
import com.aliuken.jobvacanciesapp.util.javase.GenericsUtils;

public abstract class AbstractEntityFactory<T extends AbstractEntity> extends AbstractFactoryBean<T> {
	private final Class<T> objectType = GenericsUtils.cast(this.createInstance().getClass());

	protected AbstractEntityFactory() {
		setSingleton(false);
	}

	@Override
	public Class<T> getObjectType() {
		return objectType;
	}

	@Override
	protected abstract T createInstance();

	public final T getObjectWithoutException() {
		try {
			final T object = this.getObject();
			return object;
		} catch(final Exception exception) {
			final T object = this.createInstance();
			return object;
		}
	}
}
