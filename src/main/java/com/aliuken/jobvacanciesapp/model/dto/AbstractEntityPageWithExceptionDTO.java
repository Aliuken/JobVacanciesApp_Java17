package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.entity.superclass.AbstractEntity;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.io.Serializable;

public record AbstractEntityPageWithExceptionDTO<T extends AbstractEntity<T>>(
	@NotNull
	Page<T> page,

	Throwable throwable
) implements Serializable {

	public AbstractEntityPageWithExceptionDTO {

	}

	public boolean hasException() {
		final boolean result = (throwable != null);
		return result;
	}

	@Override
	public String toString() {
		final String pageString = page.toString();
		final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(throwable);

		final String result = StringUtils.getStringJoined("AbstractEntityPageWithExceptionDTO [page=", pageString, ", throwable=", rootCauseMessage, "]");
		return result;
	}
}
