package com.aliuken.jobvacanciesapp.model.entity.enumtype;

import java.io.Serializable;
import java.util.function.Predicate;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.superinterface.Internationalizable;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;

import jakarta.validation.constraints.NotNull;

public enum TableField implements Serializable, Internationalizable {
	ID("id", "id", null, "abstractEntity.id", false),
	USER_EMAIL("email", "email", "authUser.email", "abstractEntityWithAuthUser.email", false),
	USER_NAME("name", "name", "authUser.name", "abstractEntityWithAuthUser.name", false),
	USER_SURNAMES("surnames", "surnames", "authUser.surnames", "abstractEntityWithAuthUser.surnames", false),
	FIRST_REGISTRATION_DATE_TIME("firstRegistrationDateTime", "firstRegistrationDateTime", null, "abstractEntity.firstRegistrationDateTime", false),
	FIRST_REGISTRATION_AUTH_USER_EMAIL("firstRegistrationAuthUserEmail", "firstRegistrationAuthUser.email", null, "abstractEntity.firstRegistrationAuthUserEmail", false),
	LAST_MODIFICATION_DATE_TIME("lastModificationDateTime", "lastModificationDateTime", null, "abstractEntity.lastModificationDateTime", true),
	LAST_MODIFICATION_AUTH_USER_EMAIL("lastModificationAuthUserEmail", "lastModificationAuthUser.email", null, "abstractEntity.lastModificationAuthUserEmail", true);

	private final static Predicate<TableField> VALUES_PREDICATE = (tableField -> true);
	private final static Predicate<TableField> VALUES_WITHOUT_AUTH_USER_PREDICATE = (tableField -> tableField.isNotAuthUserField());
	private final static Predicate<TableField> VALUES_WITHOUT_LAST_MODIFICATION_PREDICATE = (tableField -> !tableField.isLastModificationField);
	private final static Predicate<TableField> VALUES_WITHOUT_AUTH_USER_AND_LAST_MODIFICATION_PREDICATE = (tableField -> (tableField.isNotAuthUserField() && !tableField.isLastModificationField));

	@NotNull
	private final String code;

	@NotNull
	private final String fieldPath;

	private final String authUserFieldPath;

	@NotNull
	private final String messageName;

	private final boolean isLastModificationField;

	private TableField(final String code, final String fieldPath, final String authUserFieldPath, final String messageName, final boolean isLastModificationField) {
		this.code = code;
		this.fieldPath = fieldPath;
		this.messageName = messageName;
		this.authUserFieldPath = authUserFieldPath;
		this.isLastModificationField = isLastModificationField;
	}

	public String getCode() {
		return code;
	}

	public String getFieldPath() {
		return fieldPath;
	}

	public String getAuthUserFieldPath() {
		return authUserFieldPath;
	}

	public boolean isNotAuthUserField() {
		return (authUserFieldPath == null);
	}

	@Override
	public String getMessageName() {
		return messageName;
	}

	public boolean isLastModificationField() {
		return isLastModificationField;
	}

	public static TableField findByCode(final String code) {
		if(LogicalUtils.isNullOrEmptyString(code)) {
			return null;
		}

		final TableField tableField = Constants.PARALLEL_STREAM_UTILS.ofEnum(TableField.class)
			.filter(e -> code.equals(e.code))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("TableField code does not exist"));

		return tableField;
	}

	public static TableField[] values(final boolean entityWithAuthUserFields, final boolean isUnmodifiableEntity) {
		final TableField[] tableFields = Constants.PARALLEL_STREAM_UTILS.ofEnum(TableField.class)
			.filter(TableField.valuesPredicate(entityWithAuthUserFields, isUnmodifiableEntity))
			.toArray(TableField[]::new);
		return tableFields;
	}

	private static Predicate<? super TableField> valuesPredicate(final boolean entityWithAuthUserFields, final boolean isUnmodifiableEntity) {
		final Predicate<? super TableField> valuesPredicate;
		if(entityWithAuthUserFields) {
			if(isUnmodifiableEntity) {
				valuesPredicate = TableField.VALUES_WITHOUT_LAST_MODIFICATION_PREDICATE;
			} else {
				valuesPredicate = TableField.VALUES_PREDICATE;
			}
		} else {
			if(isUnmodifiableEntity) {
				valuesPredicate = TableField.VALUES_WITHOUT_AUTH_USER_AND_LAST_MODIFICATION_PREDICATE;
			} else {
				valuesPredicate = TableField.VALUES_WITHOUT_AUTH_USER_PREDICATE;
			}
		}
		return valuesPredicate;
	}
}
