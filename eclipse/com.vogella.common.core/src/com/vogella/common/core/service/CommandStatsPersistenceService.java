package com.vogella.common.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import com.vogella.common.core.domain.CommandStats;

public interface CommandStatsPersistenceService {
	void save(CommandStats object);
	Collection<CommandStats> get();
	Optional<CommandStats> get(String commandId);
	void persist() throws IOException;
}
