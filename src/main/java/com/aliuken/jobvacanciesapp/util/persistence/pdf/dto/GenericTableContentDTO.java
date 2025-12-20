package com.aliuken.jobvacanciesapp.util.persistence.pdf.dto;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;

import java.io.Serializable;

public record GenericTableContentDTO(
	int cellHorizontalAlignment,
	Font cellFont,
	String[][] contentArray,
	boolean alternateRowColor,
	boolean drawBorders
) implements Serializable {

	private static final GenericTableContentDTO NO_ARGS_INSTANCE = new GenericTableContentDTO(Element.ALIGN_LEFT, null, null, false, false);

	public GenericTableContentDTO {

	}

	public static GenericTableContentDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}
}
