package com.vogella.localpersistence;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.vogella.common.core.domain.CommandStats;
import com.vogella.common.core.service.CommandStatsPersistenceService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class PersistenceServiceImpl implements CommandStatsPersistenceService {

	private static final String USER_HOME = System.getProperty("user.home");

	private static final String COMMANDSTATS_HOME = USER_HOME + "/.eclipse/commandstats";

	private Gson gson;

	private Flux<CommandStats> data;

	private List<CommandStats> runtimeData;

	@Activate
	public void activate() {
		runtimeData = new ArrayList<>(20);
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	public <T> Flux<T> getData(Class<T> clazz) {
		Flux<T> flux = Flux.create(sink -> {
			final Type REVIEW_TYPE = new TypeToken<List<T>>() {
			}.getType();

			try (JsonReader reader = new JsonReader(new FileReader(COMMANDSTATS_HOME))) {
				List<T> fromJson = gson.fromJson(reader, REVIEW_TYPE);
				setRuntimeData(fromJson);
				for (T t : fromJson) {
					sink.next(t);
				}
			} catch (FileNotFoundException e) {
				sink.error(e);
			} catch (IOException e) {
				sink.error(e);
			}
		});

		return flux.publishOn(Schedulers.elastic());
	}

	@SuppressWarnings("unchecked")
	private <T> void setRuntimeData(List<T> fromJson) {
		runtimeData.addAll((Collection<? extends CommandStats>) fromJson);
	}

	@Override
	public Mono<Void> add(CommandStats object) {
		return Mono.just(runtimeData.add(object)).then();
	}

	@Override
	public Mono<Void> remove(CommandStats object) {
		return Mono.just(runtimeData.remove(object)).then();
	}

	@Override
	public Flux<CommandStats> get() {
		if (!runtimeData.isEmpty()) {
			return Flux.fromIterable(runtimeData);
		}

		if (data == null) {
			data = getData(CommandStats.class).cache();
		}
		return data;
	}

	@Override
	public Mono<Void> persist() {
		return Mono.create(sink -> {
			try (Writer writer = new FileWriter(COMMANDSTATS_HOME)) {
				Gson gson = new GsonBuilder().create();
				gson.toJson(runtimeData, writer);
			} catch (IOException e) {
				sink.error(e);
			}
		}).then().publishOn(Schedulers.elastic());
	}
}
