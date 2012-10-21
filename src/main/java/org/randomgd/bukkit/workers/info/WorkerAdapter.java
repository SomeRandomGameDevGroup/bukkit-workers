package org.randomgd.bukkit.workers.info;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Worker information serialization adapter.
 */
public class WorkerAdapter implements JsonSerializer<WorkerInfo>,
		JsonDeserializer<WorkerInfo> {

	/**
	 * Json symbol for 'data' field.
	 */
	private static final String SYMBOL_DATA = "data";
	/**
	 * Json symbol for decoding 'key' field.
	 */
	private static final String SYMBOL_KEY = "key";
	/**
	 * Json symbol for decoding 'id' field.
	 */
	private static final String SYMBOL_ID = "id";

	/**
	 * Context 'hack' : Store/Retrieve the current UUID.
	 */
	private UUID uuid;

	/**
	 * Constructor.
	 */
	public WorkerAdapter() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkerInfo deserialize(JsonElement element, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		// Let's be a little bit paranoid with that. Data comes from a file.
		// And we don't trust any coming from the outside.
		// We should retrieve the class information in 'key' and the actual
		// class data in 'data'.
		WorkerInfo result = null;
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			// Let's get class information.
			JsonElement keyElement = object.get(SYMBOL_KEY);
			JsonElement idElement = object.get(SYMBOL_ID);
			if ((keyElement != null) && (idElement != null)
					&& (keyElement.isJsonPrimitive())
					&& (idElement.isJsonPrimitive())) {
				String workerType = keyElement.getAsString();
				// Try some reflection.
				Class<?> descriptor = null;
				try {
					descriptor = Class.forName(workerType);
				} catch (Exception ex) {
					ex.printStackTrace();
					// TODO Use a better logger.
					throw new JsonParseException(ex.getMessage(), ex.getCause());
				}
				// Descriptor shall not be null by now.
				JsonElement dataElement = object.get(SYMBOL_DATA);
				if (dataElement != null) {
					result = context.deserialize(dataElement, descriptor);
				}
				uuid = UUID.fromString(idElement.getAsString());
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonElement serialize(WorkerInfo info, Type type,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		// We'll need to store some extra information (like the actual class).
		String informationKey = info.getClass().getCanonicalName();
		result.addProperty(SYMBOL_KEY, informationKey);
		result.addProperty(SYMBOL_ID, uuid.toString());
		JsonElement element = context.serialize(info);
		result.add(SYMBOL_DATA, element);
		return result;
	}

	/**
	 * @return The currently stored UUID.
	 */
	public final UUID getCurrentUUID() {
		return uuid;
	}

	/**
	 * Store the UUID for later context usage.
	 * 
	 * @param uuid
	 *            Unique identifier.
	 */
	public final void setCurrentUUID(UUID uuid) {
		this.uuid = uuid;
	}
}
