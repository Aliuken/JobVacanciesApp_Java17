package com.aliuken.jobvacanciesapp.model.dto;

import java.io.Serializable;
import java.util.Objects;

import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.PredefinedFilterEntity;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TableField;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TableSortingDirection;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TableSearchDTO(
	@NotEmpty(message="{language.notEmpty}")
	@Size(min=2, max=2, message="{language.minAndMaxSize}")
	String languageParam,

	String predefinedFilterName,

	String predefinedFilterValue,

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

	private static final TableSearchDTO NO_ARGS_INSTANCE = new TableSearchDTO(null, null, null, null, null, null, null, null, null);

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

	public PredefinedFilterEntity getPredefinedFilterEntity() {
		final PredefinedFilterEntity predefinedFilterEntity = PredefinedFilterEntity.findByEntityName(predefinedFilterName);
		return predefinedFilterEntity;
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
		final PredefinedFilterEntity predefinedFilterEntity = this.getPredefinedFilterEntity();
		final String predefinedFilterEntityName = Objects.toString(predefinedFilterEntity);
		final TableField filterTableField = this.getFilterTableField();
		final String filterTableFieldName = Objects.toString(filterTableField);
		final TableField tableSortingField = this.getTableSortingField();
		final String tableSortingFieldName = Objects.toString(tableSortingField);
		final String pageSizeString = Objects.toString(pageSize);
		final String pageNumberString = Objects.toString(pageNumber);

		final String result = StringUtils.getStringJoined("TableSearchDTO [languageParam=", languageParam,
			", predefinedFilterName=", predefinedFilterName, ", predefinedFilterEntityName=", predefinedFilterEntityName, ", predefinedFilterValue=", predefinedFilterValue,
			", filterName=", filterName, ", filterTableFieldName=", filterTableFieldName, ", filterValue=", filterValue,
			", sortingField=", sortingField, ", tableSortingFieldName=", tableSortingFieldName, ", sortingDirection=", sortingDirection,
			", pageSize=", pageSizeString, ", pageNumber=", pageNumberString, "]");
		return result;
	}
}
