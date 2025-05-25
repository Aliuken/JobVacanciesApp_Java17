package com.aliuken.jobvacanciesapp.enumtype;

import java.io.Serializable;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import jakarta.validation.constraints.NotNull;

public enum ControllerDependentTraceType implements Serializable {
	DATABASE_TRACE                    ("DB "),
	ENTITY_MANAGER_CACHE_INPUT_TRACE  (">> "),
	ENTITY_MANAGER_CACHE_OUTPUT_TRACE ("<< "),
	ENTITY_MANAGER_CACHE_SUMMARY_TRACE("-- ");

	@NotNull
	private final String traceInsideController;

	@NotNull
	private final String traceOutsideController;

	private ControllerDependentTraceType(@NotNull final String initialTrace) {
		this.traceInsideController = StringUtils.getStringJoined("  ", initialTrace);
		this.traceOutsideController = StringUtils.getStringJoined(initialTrace, "  ");
	}

	public String getTraceInsideController() {
		return traceInsideController;
	}

	public String getTraceOutsideController() {
		return traceOutsideController;
	}
}
