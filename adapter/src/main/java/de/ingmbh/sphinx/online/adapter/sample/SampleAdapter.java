package de.ingmbh.sphinx.online.adapter.sample;

import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter;
import de.ingmbh.sphinx.online.common.vo.DataType;
import de.ingmbh.sphinx.online.common.vo.Value;
import de.ingmbh.sphinx.online.common.vo.ValueDefinition2;

/**
 * A sample adapter that mocks an actual data source by directly using the valuePool.<br>
 * It implements value changes pro-actively by starting a timer that increments a value.
 * <p>
 * Note that changing this class name must be reflected in the file META-INF/services/de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapter.
 *
 * @see PublisherBase#getValuePool()
 * @author fke
 * @see IDataSourceAdapter
 */
public class SampleAdapter extends DataSourceAdapter {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceAdapter.class);

	/**
	 * Reserved field name for adapter discovery via class path. Holds the adapter type name.
	 */
	public static final String ADAPTER_TYPE_NAME = "sample";

	/**
	 * Reserved field name for adapter discovery via class path. Holds the adapter type description.
	 */
	public static final String ADAPTER_TYPE_DESCRIPTION = "Sample adapter to demonstrate the Adapter API";

	/**
	 * The adapter defines a property that specifies the interval for value changes in milliseconds.
	 *
	 * @see #supplyDefaultProperties(Map)
	 * @see AdapterSpec2#getProperties()
	 * @see #ADAPTER_DEFAULT_INTERVAL_MILLIS
	 */
	public static final String ADAPTER_PROPERTY_INTERVAL_MILLIS = "interval.millis";

	/**
	 * The default value for {@link #ADAPTER_PROPERTY_INTERVAL_MILLIS}.
	 */
	public static final Long ADAPTER_DEFAULT_INTERVAL_MILLIS = 500l;

	/**
	 * The native id of a value that is supplied by this adapter (usually ids are created by querying the actual data source).
	 */
	private static final String MY_VALUE_ID_A = "my.value.a";

	/**
	 * The native id of a value that is supplied by this adapter (usually ids are created by querying the actual data source).
	 */
	private static final String MY_VALUE_ID_B = "my.value.b";

	/**
	 * Holds the actual value of {@link #ADAPTER_PROPERTY_INTERVAL_MILLIS}.
	 */
	private long intervalMillis = ADAPTER_DEFAULT_INTERVAL_MILLIS;

	/**
	 * Our background timer.
	 */
	private Timer timer;

	@Override
	public void afterPropertiesSet() {
		// fetch one our properties
		// it is guaranteed to be set with the default value at least (NPE safe) because of supplyDefaultProperties()
		intervalMillis = dataSourceAdapterContext.getAdapterSpecConfiguration().getLong(ADAPTER_PROPERTY_INTERVAL_MILLIS);
		// now we can start "acting"

		// place some values in the pool
		Long timeOfValue = System.currentTimeMillis();
		getValuePool().putValue(new Value(MY_VALUE_ID_A, 0, timeOfValue));
		getValuePool().putValue(new Value(MY_VALUE_ID_B, "b", timeOfValue));

		// produce a timer thread with the name of the adapter instance
		timer = new Timer(getDataSourceAdapterContext().createTimerId());
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					// pass to a method of the adapter
					SampleAdapter.this.tick();
				}
				catch (Throwable e) {
					logger.error(e.getMessage(), e);
					// make sure we keep running within the Timer
				}
			}
		}, 0, intervalMillis);
	}

	/**
	 * Invoked by our own timer.
	 */
	protected void tick() {
		Collection<Value> valuesForChange = new Vector<Value>();
		// modify the values in our pool to mock a change
		for (Map.Entry<String, Value> entry : getValuePool().entrySet()) {
			Value value = entry.getValue();
			if (value.getValue() instanceof Integer) {
				// increment all integers by one
				Integer old = (Integer) value.getValue();
				value.setValue(new Integer(old.intValue() + 1));
				// keep for change
				valuesForChange.add(value);
			}
		}
		// inform our subscribers
		if (valuesForChange.size() > 0) {
			informValuesChanged(valuesForChange, true);
		}
	}

	@Override
	public void supplyDefaultProperties(Map<String, String> defaults) {
		// as an example we have a timer interval in which we increment our fake values
		defaults.put(ADAPTER_PROPERTY_INTERVAL_MILLIS, ADAPTER_DEFAULT_INTERVAL_MILLIS.toString());
	}

	@Override
	public boolean setValues(Collection<Value> values) {
		boolean success = true;
		// well, our source is our "pool" so we might as well...
		for (Value value : values) {
			// we might as well check whether it is a value we actually have
			if (valuePool.containsKey(value.getId())) {
				valuePool.putValue(value);
			} else {
				success = false;
			}
		}
		// it is the task of the adapter to inform (requesting to change a value does not mean it actually changes)
		// but for this adapter this holds true
		informValuesChanged(values, true);
		return success;
	}

	@Override
	public Collection<ValueDefinition2> getAvailableData() {
		Collection<ValueDefinition2> valueDefinitions = new Vector<ValueDefinition2>();
		// here we have the task to inform about or model
		// in this sample we do this "backwards" by deriving the model from our value pool
		for (Value value : getValuePool().values()) {
			ValueDefinition2 valueDefinition = new ValueDefinition2(getAdapterId(), value.getId());
			valueDefinition.setDataType(deriveDataTypeFromValue(value));
			valueDefinitions.add(valueDefinition);
		}
		return valueDefinitions;
	}

	private DataType deriveDataTypeFromValue(Value value) throws IllegalArgumentException {
		DataType dataType = DataType.UNSPECIFIED;
		if (value.getValue() instanceof Integer) {
			dataType = DataType.INT;
		} else if (value.getValue() instanceof String) {
			dataType = DataType.STRING;
		} else {
			throw new IllegalArgumentException("unable to derive data type from class: " + (value.getValue() != null ? value.getValue().getClass() : null));
		}
		return dataType;
	}

	@Override
	public Collection<Value> getValues(Collection<String> nativeIds) {
		// since we already actively maintain the value pool, simply make use of it
		return getValuePoolValues(nativeIds, valuePool, null);
	}

	@Override
	public boolean isPollingNeeded() {
		// we inform our subscribers by ourselves
		return false;
	}

	@Override
	protected void doDisposeAdapter() throws Throwable {
		// clean up our timer
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
