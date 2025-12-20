package com.aliuken.jobvacanciesapp.util.concurrency;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

public record ChunkResultDTO<C,R>(
	@NotEmpty(message="{chunk.notEmpty}")
	C chunk,

	R result
) implements Serializable {

	private static final ChunkResultDTO NO_ARGS_INSTANCE = new ChunkResultDTO(null, null);

	public ChunkResultDTO {

	}

	public static ChunkResultDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String chunkString = String.valueOf(chunk);
		final String resultString = String.valueOf(result);

		final String result = StringUtils.getStringJoined("ChunkResultDTO [chunk=", chunkString, ", result=", resultString, "]");
		return result;
	}
}
