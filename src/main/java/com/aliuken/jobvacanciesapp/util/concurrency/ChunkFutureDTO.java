package com.aliuken.jobvacanciesapp.util.concurrency;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.concurrent.Future;

public record ChunkFutureDTO<C,R>(
	@NotEmpty(message="{chunk.notEmpty}")
	C chunk,

	@NotNull(message="{future.notNull}")
	Future<R> future
) implements Serializable {

	private static final ChunkFutureDTO NO_ARGS_INSTANCE = new ChunkFutureDTO(null, null);

	public ChunkFutureDTO {

	}

	public static ChunkFutureDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String chunkString = String.valueOf(chunk);
		final String futureString = String.valueOf(future);

		final String result = StringUtils.getStringJoined("ChunkFutureDTO [chunk=", chunkString, ", future=", futureString, "]");
		return result;
	}
}
