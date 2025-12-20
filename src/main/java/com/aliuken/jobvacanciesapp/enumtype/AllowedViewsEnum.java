package com.aliuken.jobvacanciesapp.enumtype;

import com.aliuken.jobvacanciesapp.Constants;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public enum AllowedViewsEnum implements Serializable {
	ANONYMOUS_ACCESS_BY_DEFAULT (AnonymousAccessPermission.BY_DEFAULT),
	ANONYMOUS_ACCESS_ALLOWED    (AnonymousAccessPermission.ACCESS_ALLOWED),
	ANONYMOUS_ACCESS_NOT_ALLOWED(AnonymousAccessPermission.ACCESS_NOT_ALLOWED);

	private final PathPatternRequestMatcher[] VARIABLE_VIEWS_ARRAY = new PathPatternRequestMatcher[]{
		PathPatternRequestMatcher.withDefaults().matcher("/"),
		PathPatternRequestMatcher.withDefaults().matcher("/search"),
		PathPatternRequestMatcher.withDefaults().matcher("/auth-users/view/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-categories/view/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-companies/view/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-vacancies/view/**")
	};

	private final PathPatternRequestMatcher[] FIXED_ANONYMOUS_VIEWS_ARRAY = new PathPatternRequestMatcher[]{
		PathPatternRequestMatcher.withDefaults().matcher("/login"),
		PathPatternRequestMatcher.withDefaults().matcher("/logout"),
		PathPatternRequestMatcher.withDefaults().matcher("/signup"),
		PathPatternRequestMatcher.withDefaults().matcher("/signup-confirmed"),
		PathPatternRequestMatcher.withDefaults().matcher("/forgotten-password"),
		PathPatternRequestMatcher.withDefaults().matcher("/reset-password"),
		PathPatternRequestMatcher.withDefaults().matcher("/about")
	};

	private final PathPatternRequestMatcher[] FIXED_USER_VIEWS_ARRAY = new PathPatternRequestMatcher[]{
		PathPatternRequestMatcher.withDefaults().matcher("/my-user/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/my-user/auth-user-curriculums/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/my-user/auth-user-entity-queries/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-requests/create/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-requests/save/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-requests/view/**")
	};

	private final PathPatternRequestMatcher[] SUPERVISOR_VIEWS_ARRAY = new PathPatternRequestMatcher[]{
		PathPatternRequestMatcher.withDefaults().matcher("/job-requests/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-vacancies/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-categories/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/job-companies/**")
	};

	private final PathPatternRequestMatcher[] ADMINISTRATOR_VIEWS_ARRAY = new PathPatternRequestMatcher[]{
		PathPatternRequestMatcher.withDefaults().matcher("/auth-users/**"),
		PathPatternRequestMatcher.withDefaults().matcher("/my-user/app/**")
	};

	@Getter
    private final AnonymousAccessPermission anonymousAccessPermission;

	@Getter
    @NotEmpty
	private final PathPatternRequestMatcher[] anonymousViewsArray;

	@Getter
    @NotEmpty
	private final PathPatternRequestMatcher[] userViewsArray;

	@Getter
    @NotEmpty
	private final PathPatternRequestMatcher[] supervisorViewsArray;

	@Getter
    @NotEmpty
	private final PathPatternRequestMatcher[] administratorViewsArray;

	private static final Map<AnonymousAccessPermission, AllowedViewsEnum> ALLOWED_VIEWS_MAP = AllowedViewsEnum.getAllowedViewsMap();

	private AllowedViewsEnum(final AnonymousAccessPermission anonymousAccessPermission) {
		this.anonymousAccessPermission = Constants.ENUM_UTILS.getFinalEnumElement(anonymousAccessPermission, AnonymousAccessPermission.class);

		if(AnonymousAccessPermission.ACCESS_ALLOWED == this.anonymousAccessPermission) {
			this.anonymousViewsArray = Constants.PARALLEL_STREAM_UTILS.joinArrays(PathPatternRequestMatcher[]::new, FIXED_ANONYMOUS_VIEWS_ARRAY, VARIABLE_VIEWS_ARRAY);
			this.userViewsArray = FIXED_USER_VIEWS_ARRAY;
		} else {
			this.anonymousViewsArray = FIXED_ANONYMOUS_VIEWS_ARRAY;
			this.userViewsArray = Constants.PARALLEL_STREAM_UTILS.joinArrays(PathPatternRequestMatcher[]::new, FIXED_USER_VIEWS_ARRAY, VARIABLE_VIEWS_ARRAY);
		}

		this.supervisorViewsArray = SUPERVISOR_VIEWS_ARRAY;
		this.administratorViewsArray = ADMINISTRATOR_VIEWS_ARRAY;
	}

    public static AllowedViewsEnum getInstance(final AnonymousAccessPermission anonymousAccessPermission) {
		final AllowedViewsEnum allowedViewsEnum = ALLOWED_VIEWS_MAP.get(anonymousAccessPermission);
		return allowedViewsEnum;
	}

	private static Map<AnonymousAccessPermission, AllowedViewsEnum> getAllowedViewsMap() {
		final AllowedViewsEnum[] allowedViewsEnumElements = AllowedViewsEnum.values();

		final Map<AnonymousAccessPermission, AllowedViewsEnum> allowedViewsMap = new HashMap<>();
		final Consumer<AllowedViewsEnum> allowedViewsEnumConsumer = (allowedViewsEnum -> {
			if(allowedViewsEnum != null) {
				final AnonymousAccessPermission anonymousAccessPermission = allowedViewsEnum.getAnonymousAccessPermission();
				allowedViewsMap.put(anonymousAccessPermission, allowedViewsEnum);
			}
		});

		final Stream<AllowedViewsEnum> allowedViewsEnumStream = Constants.PARALLEL_STREAM_UTILS.ofNullableArray(allowedViewsEnumElements);
		allowedViewsEnumStream.forEach(allowedViewsEnumConsumer);

		return allowedViewsMap;
	}
}
