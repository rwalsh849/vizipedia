package net.vizipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

public class WikipediaParser {

	private final static String rawMappingFileKey = "rawMappingFile";
	private final static String processedMappingFileKey = "processedMappingFile";
	private final static String rawLinkFileKey = "rawLinkFile";
	private final static String processedLinkFileKey = "processedLinkFile";
	private final static String forceOverwriteKey = "forceOverwrite";

	private final static String[] allKeys = { rawMappingFileKey,
			processedMappingFileKey, rawLinkFileKey, processedLinkFileKey,
			forceOverwriteKey };

	public static void main(final String[] args) {

		final HashMap<String, String> argMap = new HashMap<String, String>();

		processArgsIntoMap(args, argMap);

		// if the user has not set the force overwrite flag and
		// the output file exists, then abort
		if (!Boolean.parseBoolean(argMap.get(forceOverwriteKey))
				&& new File(argMap.get(processedLinkFileKey)).exists()) {
			System.err
					.println("The link structure output file already exists. In order to overwrite this file, you must set the -"
							+ forceOverwriteKey + " flag to true");
			return;
		}

		doProcessing(argMap);

	}

	public static void doProcessing(final HashMap<String, String> argMap) {
		long startTime;

		System.out.println("starting at : "
				+ (startTime = System.currentTimeMillis()));

		try {
			final HashBiMap<String, Integer> mapping = loadOrCreateMemoryMapping(
					argMap.get(rawMappingFileKey),
					argMap.get(processedMappingFileKey));

			createLinkFile(argMap.get(rawLinkFileKey),
					argMap.get(processedLinkFileKey), mapping,
					Boolean.parseBoolean(argMap.get(forceOverwriteKey)));

			System.out.println("elapsed time (s): "
					+ (System.currentTimeMillis() - startTime) / 1000.0);

		} catch (final IOException e) {
			System.err.println("ERROR: " + e);
			System.exit(1);
		}
	}

	private static void processArgsIntoMap(final String[] args,
			final HashMap<String, String> argMap) {
		int i = 0;
		String userKey, value;

		final List<String> allKeysAsList = Lists.newArrayList(allKeys);
		while (i < args.length && args[i].startsWith("-")) {

			userKey = args[i].substring(1);
			value = args[++i];

			if (allKeysAsList.contains(userKey)) {
				argMap.put(userKey, value);
			}

			i++;
		}

		// if we didn't get all the info we were hoping for, abort
		if (allKeysAsList.size() != argMap.size()) {
			// TODO abort with error here
		}

	}

	private static void createLinkFile(final String inputFile,
			final String outputFile, final HashBiMap<String, Integer> mapping,
			final boolean forceOverwrite) throws IOException {

		final LineMatcher lm = LineMatchers.getLinkLineMatcher(mapping);

		lm.processFile(inputFile, outputFile);

	}

	private static HashBiMap<String, Integer> loadOrCreateMemoryMapping(
			final String rawDataFile, final String processedDataFile)
			throws IOException {

		final HashBiMap<String, Integer> mapping = HashBiMap.create(9090000);

		final File file = new File(processedDataFile);

		if (!file.exists()) {
			createMemoryMapping(rawDataFile, processedDataFile, mapping);
			return mapping;
		}

		loadMemoryMappingFromInputStream(mapping, new FileInputStream(file));

		return mapping;

	}

	static void loadMemoryMappingFromInputStream(
			final HashBiMap<String, Integer> mapping, final InputStream in)
			throws UnsupportedEncodingException, IOException,
			NumberFormatException {
		String strLine;
		final BufferedReader br = new BufferedReader(new InputStreamReader(in,
				Constants.UTF_8));

		while ((strLine = br.readLine()) != null) {

			if (strLine.equalsIgnoreCase(Constants.EMPTY_STRING)) {
				continue;
			}

			final String[] tokens = strLine.split(Constants.TAB);

			mapping.put(tokens[0], Integer.valueOf(tokens[1]));

		}

		// TODO doc that this method will close the stream
		in.close();
		br.close();
	}

	private static void createMemoryMapping(final String inputFile,
			final String outputFile, final HashBiMap<String, Integer> mapping)
			throws IOException {

		final LineMatcher lm = LineMatchers.getMappingLineMatcher(mapping);

		lm.processFile(inputFile, outputFile);

	}

}
