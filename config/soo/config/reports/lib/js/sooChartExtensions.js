importPackage(Packages.org.eclipse.birt.chart.model.component.impl);
importPackage(Packages.org.eclipse.birt.chart.model.data.impl);
importPackage(Packages.org.eclipse.birt.chart.model.attribute);
importPackage(Packages.org.eclipse.birt.chart.model.attribute.impl);
importPackage(Packages.org.eclipse.birt.chart.model.type.impl);

sooChartExtensions = {
		/**
		 * The options of the chart.
		 */
	options : {
		/* the in "prepareYScale" calculated min y-value */
		minValue : Number.MAX_VALUE,
		/* the in "prepareYScale" calculated max y-value */
		maxValue : Number.MIN_VALUE
	},
	/**
	 * Calculates the y scale based on the dataset values.
	 * @param series Series
	 * @param dataSet DataSet
	 * @param icsc IChartScriptContext
	 */
	prepareYScale : function(series, dataSet, icsc) {
		importPackage(Packages.org.eclipse.birt.chart.util);
	    ps = PluginSettings.instance();

	    dsp = ps.getDataSetProcessor(series.getClass());
	    var currentMin = dsp.getMinimum(dataSet);
	    var currentMax = dsp.getMaximum(dataSet);
	    if (currentMax > this.options.maxValue)
	    {
	    	this.options.maxValue = currentMax;
	    }
	    if (currentMin < this.options.minValue)
	    {
	    	this.options.minValue = currentMin;
	    }
	},
	/**
	 * Sets the chart title.
	 * @param chart The BIRT chart.
	 * @param chart The
	 */
	setChartTitle : function(chart, title) {
		chart.getTitle().getLabel().getCaption().setValue(title);
	},

	/**
	 * Renders all limit lines.
	 * @param chart The BIRT chart.
	 * @param lines Java list of lines (in a hash map).
	 * @param boundsCheck Whether the bounds of the lines should be checked.
	 */
	renderHorizontalLines : function(chart, lines, boundsCheck) {
		if (lines) {
			for (var i = 0; i < lines.size(); ++i) {
				this.renderHorizontalLine(chart, lines.get(i), boundsCheck);
			}
		}
	},

	/**
	 * Render a limit line.
	 * @param chart The BIRT chart.
	 * @param line Java hash map containing line data (keys: value, label).
	 * @param boundsCheck Whether the bounds of the lines should be checked.
	 */
	renderHorizontalLine : function(chart, line, boundsCheck) {

		var yAxis = chart.getAxes().get(0).getAssociatedAxes().get(0);
		var limitValue = line.get('value');
		var limitLabel = line.get('label');

		var limitColorString = line.get('color');
		var limitColorSplit = limitColorString.split(',');
		var limitColorR = 127;
		var limitColorG = 0;
		var limitColorB = 0;

		if (limitColorSplit.length == 3) {
			limitColorR = limitColorSplit[0];
			limitColorG = limitColorSplit[1];
			limitColorB = limitColorSplit[2];
		}

		/* check if value is out of chart bounds */
		if (boundsCheck == false || limitValue <= this.options.maxValue && limitValue >= this.options.minValue) {
			if (limitValue != -9999) {
				var min_ml = MarkerLineImpl.create(yAxis, NumberDataElementImpl.create(limitValue));
		 		min_ml.getLabel().getCaption().setValue(limitLabel);
		 		min_ml.getLineAttributes().getColor().set(limitColorR, limitColorG, limitColorB);
		 	}
		}
	}
};

