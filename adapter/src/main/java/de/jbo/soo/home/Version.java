//
// Copyright (c) 2010 by jbo - Josef Baro
//
// Project: jbogx2D
// File: Version.java
// Created: 20.02.2010 - 20:20:47
//
package de.jbo.soo.home;

import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jbo.soo.home.lang.StringUtils;
import de.jbo.soo.home.lang.WrapperInteger;

/**
 * Defines version information.
 *
 * @author Josef Baro (jbo) <br>
 * @version 23.12.2018 jbo - ported from jbogx2d <br>
 */
public class Version {
    private static final Logger LOG = LoggerFactory.getLogger(Version.class);

    /** Manifest implementation version. */
    private static final String MANIFEST_IMPLEMENTATION_VERSION = "Implementation-Version";

    /** Manifest implementation build. */
    private static final String MANIFEST_IMPLEMENTATION_BUILD = "Implementation-Build";

    /** Major version. */
    private int versionMajor = 0;

    /** Minor version. */
    private int versionMinor = 0;

    /** Bugfix version. */
    private int versionBugfix = 0;

    /** Build date timestamp. */
    private Calendar versionBuildDate = new GregorianCalendar();

    /**
     * Creates a new instance.
     *
     * @param version
     *            In format "major.minor.bugfix", e.g. "1.0.0"
     * @param buildDate
     *            In format "b<year><month><day><hours><minutes><seconds>", e.g.
     *            "b20131224120001"
     */
    public Version(String version, String buildDate) {
        WrapperInteger major = new WrapperInteger();
        WrapperInteger minor = new WrapperInteger();
        WrapperInteger bugfix = new WrapperInteger();
        parseVersion(version, major, minor, bugfix);
        Calendar build = parseBuildDate(buildDate);

        init(major.getValue(), minor.getValue(), bugfix.getValue(), build);
    }

    /**
     * Parse the build date out of the given String.
     *
     * @param buildDate
     *            In format "b<year><month><day><hours><minutes><seconds>", e.g.
     *            "b20131224120001"
     * @return The parsed value.
     */
    private Calendar parseBuildDate(String buildDate) {
        final int indexStartYear = 1;
        final int indexEndYear = 5;
        final int indexStartMonth = 5;
        final int indexEndMonth = 7;
        final int indexStartDay = 7;
        final int indexEndDay = 9;
        final int indexStartHours = 9;
        final int indexEndHours = 11;
        final int indexStartMinutes = 11;
        final int indexEndMinutes = 13;
        final int indexStartSeconds = 13;
        final int indexEndSeconds = 15;
        Calendar ret = null;

        int year = Integer.parseInt(buildDate.substring(indexStartYear, indexEndYear));
        int month = Integer.parseInt(buildDate.substring(indexStartMonth, indexEndMonth));
        int day = Integer.parseInt(buildDate.substring(indexStartDay, indexEndDay));
        int hours = Integer.parseInt(buildDate.substring(indexStartHours, indexEndHours));
        int minutes = Integer.parseInt(buildDate.substring(indexStartMinutes, indexEndMinutes));
        int seconds = Integer.parseInt(buildDate.substring(indexStartSeconds, indexEndSeconds));

        ret = new GregorianCalendar(year, month - 1, day, hours, minutes, seconds);

        return ret;
    }

    /**
     * @param version
     *            The version to be parsed, e.g. "2.1.0". Always requires major,
     *            minor and bugfix version numbers.
     * @param major
     *            Out-param storing the parsed version number.
     * @param minor
     *            Out-param storing the parsed version number.
     * @param bugfix
     *            Out-param storing the parsed version number.
     */
    private void parseVersion(String version, WrapperInteger major, WrapperInteger minor, WrapperInteger bugfix) {
        int index = version.indexOf('.');
        int start = 0;
        if (index > -1) {
            major.setValue(Integer.parseInt(version.substring(start, index)));
            start = index + 1;
        }
        index = version.indexOf('.', start);
        if (index > -1) {
            minor.setValue(Integer.parseInt(version.substring(start, index)));
            start = index + 1;
        }
        bugfix.setValue(Integer.parseInt(version.substring(start)));
    }

    /**
     * Creates a new instance.
     *
     * @param major
     *            The major version.
     * @param minor
     *            The minor version.
     * @param bugfix
     *            The bugfix version.
     * @param buildDate
     *            The build-date.
     */
    public Version(final int major, final int minor, final int bugfix, final Calendar buildDate) {
        init(major, minor, bugfix, buildDate);
    }

    /**
     * Initializes the values.
     *
     * @param major
     *            The major version.
     * @param minor
     *            The minor version.
     * @param bugfix
     *            The bugfix version.
     * @param buildDate
     *            The build-date.
     */
    private void init(final int major, final int minor, final int bugfix, final Calendar buildDate) {
        versionMajor = major;
        versionMinor = minor;
        versionBugfix = bugfix;
        versionBuildDate = buildDate;
    }

    /**
     * Returns the current major-version.
     *
     * @return The current major-version.
     */
    public final int getVersionMajor() {
        return versionMajor;
    }

    /**
     * Returns the current minor-version.
     *
     * @return The current minor-version.
     */
    public final int getVersionMinor() {
        return versionMinor;
    }

    /**
     * Returns the current bugfix-version.
     *
     * @return The current bugfix-version.
     */
    public final int getVersionBugfix() {
        return versionBugfix;
    }

    /**
     * Returns the build date.
     *
     * @return The build date.
     */
    public final Calendar getVersionBuildDate() {
        return versionBuildDate;
    }

    /**
     * Returns a string-version of this instance. <br>
     * <b>Format:</b><br>
     *
     * <pre>
     * major.minor.bugfix bYYYYMMddhhmmss
     * </pre>
     *
     * @return String version.
     */
    public final String getVersionString() {
        return toString();
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        final int length2 = 2;
        final int length4 = 4;
        int temp = 0;
        StringBuffer buffer = new StringBuffer();

        buffer.append(getVersionMajor());
        buffer.append('.');
        buffer.append(getVersionMinor());
        buffer.append('.');
        buffer.append(getVersionBugfix());
        buffer.append(' ');
        buffer.append('b');
        temp = versionBuildDate.get(Calendar.YEAR);
        StringUtils.fillWithLeadingZeros(temp, length4, buffer);
        temp = versionBuildDate.get(Calendar.MONTH) + 1;
        StringUtils.fillWithLeadingZeros(temp, length2, buffer);
        temp = versionBuildDate.get(Calendar.DAY_OF_MONTH);
        StringUtils.fillWithLeadingZeros(temp, length2, buffer);
        temp = versionBuildDate.get(Calendar.HOUR_OF_DAY);
        StringUtils.fillWithLeadingZeros(temp, length2, buffer);
        temp = versionBuildDate.get(Calendar.MINUTE);
        StringUtils.fillWithLeadingZeros(temp, length2, buffer);
        temp = versionBuildDate.get(Calendar.SECOND);
        StringUtils.fillWithLeadingZeros(temp, length2, buffer);

        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean ret = false;

        if (obj instanceof Version) {
            Version v = (Version) obj;
            int major = v.getVersionMajor();
            int minor = v.getVersionMinor();
            int bugfix = v.getVersionBugfix();
            Calendar build = v.getVersionBuildDate();
            ret = ((major == getVersionMajor()) && (minor == getVersionMinor()) && (bugfix == getVersionBugfix()) && build.equals(getVersionBuildDate()));
        }

        return ret;
    }

    /**
     * @param clazz
     *            The class to retrieve the manifest from.
     * @return Version built based on the jar-file's manifest. NULL if not
     *         running from within a jar-file.
     */
    public static synchronized Version getVersionFromBuild(Class<?> clazz) {
        Version version = null;
        String className = clazz.getSimpleName() + ".class";
        LOG.info("Version className = {}", className);
        String classPath = clazz.getResource(className).toString();
        LOG.info("Version classPath = {}", classPath);
        String packageName = clazz.getPackage().getName();
        packageName = packageName.replaceAll("\\.", "/");
        LOG.info("Version packageName = {}", packageName);
        int indexOfPackageStart = classPath.indexOf(packageName);
        String manifestPath = classPath.substring(0, indexOfPackageStart) + "META-INF/MANIFEST.MF";
        version = getVersionFromManifest(manifestPath);

        return version;
    }

    /**
     * @param manifestPath
     *            Path to the manifest file.
     * @return Version built based on the jar-file's manifest. NULL if not
     *         running from within a jar-file.
     */
    private static Version getVersionFromManifest(String manifestPath) {
        Version version = null;
        try {
            LOG.info("Version manifestPath = {}", manifestPath);
            Manifest manifest = new Manifest(new URL(manifestPath).openStream());
            Attributes attr = manifest.getMainAttributes();
            String manifestVersion = attr.getValue(MANIFEST_IMPLEMENTATION_VERSION);
            String manifestBuild = attr.getValue(MANIFEST_IMPLEMENTATION_BUILD);
            version = new Version(manifestVersion, manifestBuild);
        } catch (Exception ex) {
            LOG.error("Version could not be extracted from manifest!", ex);
            version = new Version("0.0.0", "b000000000000");
        }
        return version;
    }
}
