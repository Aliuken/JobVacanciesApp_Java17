package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TableField;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TablePageSize;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TableSortingDirection;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

public record TableSearchDTO(
	@NotEmpty(message="{language.notEmpty}")
	@Size(min=2, max=2, message="{language.minAndMaxSize}")
	String languageParam,

	@NotNull(message="{filterName.notEmpty}")
	String filterName,

	@NotNull(message="{filterValue.notEmpty}")
	String filterValue,

	@NotEmpty(message="{sortingField.notEmpty}")
	String sortingField,

	@NotEmpty(message="{sortingDirection.notEmpty}")
	String sortingDirection,

	@NotNull(message="{pageSize.notEmpty}")
	Integer pageSize,

	@NotNull(message="{pageNumber.notEmpty}")
	Integer pageNumber
) implements Serializable {

	private static final TableSearchDTO NO_ARGS_INSTANCE = new TableSearchDTO(null, null, null, null, null, null, null);

	public TableSearchDTO {
		if(languageParam == null) {
			languageParam = Language.ENGLISH.getCode();
		}
		if(pageNumber == null) {
			pageNumber = 0;
		}
	}

	public static TableSearchDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public TableField getFilterTableField() {
		final TableField filterTableField = TableField.findByCode(filterName);
		return filterTableField;
	}

	public TableField getTableSortingField() {
		final TableField tableSortingField = TableField.findByCode(sortingField);
		return tableSortingField;
	}

	public TableSortingDirection getTableSortingDirection() {
		final TableSortingDirection tableSortingDirection = TableSortingDirection.findByCode(sortingDirection);
		return tableSortingDirection;
	}

	public TablePageSize getTablePageSize() {
		final TablePageSize tablePageSize = TablePageSize.findByValue(pageSize);
		return tablePageSize;
	}

	//If not all pagination URL parameters -> empty table (in Java)
	public boolean hasAllParameters() {
		final boolean hasAllParameters = (
			LogicalUtils.isNotNullNorEmptyString(languageParam) && !Language.BY_DEFAULT.getCode().equals(languageParam)
			&& filterName != null && filterValue != null
			&& LogicalUtils.isNotNullNorEmptyString(sortingField) && LogicalUtils.isNotNullNorEmptyString(sortingDirection)
			&& pageSize != null && pageNumber != null);
		return hasAllParameters;
	}

	@Override
	public String toString() {
		final TableField filterTableField = this.getFilterTableField();
		final String filterTableFieldName = Objects.toString(filterTableField);
		final TableField tableSortingField = this.getTableSortingField();
		final String tableSortingFieldName = Objects.toString(tableSortingField);
		final String pageSizeString = Objects.toString(pageSize);
		final String pageNumberString = Objects.toString(pageNumber);

		final String result = StringUtils.getStringJoined("TableSearchDTO [languageParam=", languageParam,
			", filterName=", filterName, ", filterTableFieldName=", filterTableFieldName, ", filterValue=", filterValue,
			", sortingField=", sortingField, ", tableSortingFieldName=", tableSortingFieldName, ", sortingDirection=", sortingDirection,
			", pageSize=", pageSizeString, ", pageNumber=", pageNumberString, "]");
		return result;
	}
}
