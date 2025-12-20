package com.aliuken.jobvacanciesapp.controller.superclass;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.model.entity.superclass.AbstractEntity;

public abstract class AbstractEntityControllerWithTable<T extends AbstractEntity<T>> extends AbstractEntityController<T> {
	protected static final String EXPORT_TO_PDF_DISABLED_VALUE = Constants.EMPTY_STRING;
}
