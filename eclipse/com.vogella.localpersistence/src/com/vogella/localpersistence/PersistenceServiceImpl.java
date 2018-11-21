package com.vogella.localpersistence;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.vogella.common.core.domain.CommandStats;
import com.vogella.common.core.service.CommandStatsPersistenceService;

@Component
public class PersistenceServiceImpl implements CommandStatsPersistenceService {

	private static final Logger LOG = LoggerFactory.getLogger(PersistenceServiceImpl.class);

	private static final String USER_HOME = System.getProperty("user.home");

	private static final String COMMANDSTATS_HOME = USER_HOME + "/.eclipse/commandstats";

	private Gson gson;

	private Map<String, CommandStats> commandStats;

	@Activate
	public void activate() {
		gson = new GsonBuilder().setPrettyPrinting().create();

		final Type REVIEW_TYPE = new TypeToken<List<CommandStats>>() {
		}.getType();

		try (JsonReader reader = new JsonReader(new FileReader(COMMANDSTATS_HOME))) {
			List<CommandStats> fromJson = gson.fromJson(reader, REVIEW_TYPE);
			if(fromJson != null) {
				commandStats = fromJson.stream().collect(Collectors.toMap(CommandStats::getCommandId, Function.identity()));
			} else {
				commandStats = new HashMap<>();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void save(CommandStats stats) {
		CommandStats existingStats = commandStats.get(stats.getCommandId());
		if (existingStats == null) {
			commandStats.put(stats.getCommandId(), stats);
		} else {
			existingStats.setCommandName(stats.getCommandName());
			existingStats.setInvocations(stats.getInvocations());
			existingStats.setKeybinding(stats.getKeybinding());
			existingStats.setMenuDepth(stats.getMenuDepth());
		}
	}

	@Override
	public Collection<CommandStats> get() {
		return commandStats.values();
	}

	@Override
	public Optional<CommandStats> get(String commandId) {
		return Optional.ofNullable(commandStats.get(commandId));
	}

	@Override
	public void persist() throws IOException {
		try (Writer writer = new FileWriter(COMMANDSTATS_HOME)) {
			Gson gson = new GsonBuilder().create();
			gson.toJson(commandStats.values(), writer);
		}
	}
}
