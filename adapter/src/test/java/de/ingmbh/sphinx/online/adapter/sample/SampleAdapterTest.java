package de.ingmbh.sphinx.online.adapter.sample;

import de.ingmbh.sphinx.online.adapter.common.DataSourceAdapterTest;
import de.ingmbh.sphinx.online.common.exceptions.FeatureNotSupportedException;
import de.ingmbh.sphinx.online.common.vo.AdapterSpec2;
import de.ingmbh.sphinx.online.common.vo.AdapterType;

public class SampleAdapterTest extends DataSourceAdapterTest<SampleAdapter> {

	@Override
	protected AdapterSpec2 getAdapterSpec(AdapterType adapterType) {
		return new AdapterSpec2(null, adapterType, "Sample Adapter Instance Name", "Sample Adapter Instance Description", null);
	}

	@Override
	protected void provokeValueChange(SampleAdapter adapter) {
		// simply wait more than an update interval in order for a value change to happen
		long intervalMillis = adapter.getDataSourceAdapterContext().getAdapterSpecConfiguration().getLong(SampleAdapter.ADAPTER_PROPERTY_INTERVAL_MILLIS);
		try {
			Thread.sleep(intervalMillis * 3 / 2);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void provokeValueDefinitionAdded(SampleAdapter adapter) throws Exception {
		throw new FeatureNotSupportedException("not supported");
	}

	@Override
	protected void provokeValueDefinitionChanged(SampleAdapter adapter) throws Exception {
		throw new FeatureNotSupportedException("not supported");
	}

	@Override
	protected void provokeValueDefinitionRemoved(SampleAdapter adapter) throws Exception {
		throw new FeatureNotSupportedException("not supported");
	}

}
