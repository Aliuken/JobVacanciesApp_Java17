package com.aliuken.jobvacanciesapp.model.dto;

import java.io.Serializable;

import org.springframework.data.domain.Page;

import com.aliuken.jobvacanciesapp.model.entity.superclass.AbstractEntity;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;

import jakarta.validation.constraints.NotNull;

public record AbstractEntityPageWithExceptionDTO<T extends AbstractEntity>(
	@NotNull
	Page<T> page,

	Exception exception
) implements Serializable {

	private static final AbstractEntityPageWithExceptionDTO<? extends AbstractEntity> NO_ARGS_INSTANCE = new AbstractEntityPageWithExceptionDTO<>(Page.empty(), null);

	public AbstractEntityPageWithExceptionDTO {

	}

	public static AbstractEntityPageWithExceptionDTO<? extends AbstractEntity> getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String pageString = page.toString();
		final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);

		final String result = StringUtils.getStringJoined("AbstractEntityPageWithExceptionDTO [page=", pageString, ", exception=", rootCauseMessage, "]");
		return result;
	}

}
