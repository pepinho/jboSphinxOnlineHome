sooConversion = {
	/**
	 * Converts a time value (long, since 1970) to a Java Date object.
	 * @param timeInMillis The time in milliseconds.
	 * @return The Java Date of the given time.
	 */
	longToDate : function(timeInMillis) {
		importPackage(Packages.java.util);
		var date = new Date();
		date.setTime(timeInMillis);
		return date;
	}
};
