package com.aliuken.jobvacanciesapp.model.entity.enumtype;

import java.io.Serializable;
import java.util.function.Predicate;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.superinterface.Internationalizable;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import jakarta.validation.constraints.NotNull;

public enum TableField implements Serializable, Internationalizable {
	ID("id", null, "id", "abstractEntity.id", false),
	AUTH_USER_EMAIL("authUserEmail", TableFieldEntity.AUTH_USER, "email", "abstractEntityWithAuthUser.email", false),
	AUTH_USER_NAME("authUserName", TableFieldEntity.AUTH_USER, "name", "abstractEntityWithAuthUser.name", false),
	AUTH_USER_SURNAMES("authUserSurnames", TableFieldEntity.AUTH_USER, "surnames", "abstractEntityWithAuthUser.surnames", false),
	FIRST_REGISTRATION_DATE_TIME("firstRegistrationDateTime", null, "firstRegistrationDateTime", "abstractEntity.firstRegistrationDateTime", false),
	FIRST_REGISTRATION_AUTH_USER_EMAIL("firstRegistrationAuthUserEmail", null, "firstRegistrationAuthUser.email", "abstractEntity.firstRegistrationAuthUserEmail", false),
	LAST_MODIFICATION_DATE_TIME("lastModificationDateTime", null, "lastModificationDateTime", "abstractEntity.lastModificationDateTime", true),
	LAST_MODIFICATION_AUTH_USER_EMAIL("lastModificationAuthUserEmail", null, "lastModificationAuthUser.email", "abstractEntity.lastModificationAuthUserEmail", true);

	private final static Predicate<TableField> VALUES_PREDICATE = (tableField -> true);
	private final static Predicate<TableField> VALUES_WITHOUT_AUTH_USER_PREDICATE = (tableField -> !tableField.isAuthUserField());
	private final static Predicate<TableField> VALUES_WITHOUT_LAST_MODIFICATION_PREDICATE = (tableField -> !tableField.isLastModificationField);
	private final static Predicate<TableField> VALUES_WITHOUT_AUTH_USER_AND_LAST_MODIFICATION_PREDICATE = (tableField -> (!tableField.isAuthUserField() && !tableField.isLastModificationField));

	@NotNull
	private final String code;

	private final TableFieldEntity entity;

	@NotNull
	private final String partialFieldPath;

	@NotNull
	private final String finalFieldPath;

	@NotNull
	private final String messageName;

	private final boolean isLastModificationField;

	private TableField(final String code, final TableFieldEntity entity, final String partialFieldPath, final String messageName, final boolean isLastModificationField) {
		this.code = code;
		this.entity = entity;
		this.partialFieldPath = partialFieldPath;

		if(entity != null) {
			final String entityName = entity.getLowerCasedEntityName();
			this.finalFieldPath = StringUtils.getStringJoined(entityName, Constants.DOT, partialFieldPath);
		} else {
			this.finalFieldPath = partialFieldPath;
		}

		this.messageName = messageName;
		this.isLastModificationField = isLastModificationField;
	}

	public String getCode() {
		return code;
	}

	public TableFieldEntity getEntity() {
		return entity;
	}

	public String getPartialFieldPath() {
		return partialFieldPath;
	}

	public String getFinalFieldPath() {
		return finalFieldPath;
	}

	@Override
	public String getMessageName() {
		return messageName;
	}

	public boolean isLastModificationField() {
		return isLastModificationField;
	}

	public boolean isAuthUserField() {
		return (TableFieldEntity.AUTH_USER == entity);
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
