package net.vizipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides the general file processing structure that allows a file to be -
 * </p>
 * 
 * <p>
 * 1) matched according to a provided regular expression, and<br>
 * 2) processed according to the behaviour of the concrete class
 * </p>
 * 
 * <p>
 * The result of the processing step is written to the specified
 * location on disk in buffered chunks.
 * </p>
 * 
 * @author rwalsh849
 *
 */
public abstract class LineMatcher {

	/** the length of the StringBuilder at which to dump the contents of the StringBuilder to disk*/
	private static final int _50000000 = 50000000;

	/** the initial capacity of the StringBuilder */
	private static final int _51000000 = 51000000;

	private final Matcher m;
	private final StringBuilder sb;

	/**
	 * the constructor
	 * 
	 * @param pattern a valid regular expression, representing the
	 * groups that should be extracted from each line of the file
	 */
	public LineMatcher(final String pattern) {

		// TODO consider making this dynamic
		sb = new StringBuilder(_51000000);

		m = Pattern.compile(pattern).matcher(Constants.EMPTY_STRING);
	}

	public abstract void onMatchFound(Matcher m, StringBuilder sb);

	public void processFile(final String inputFile, final String outputFile)
			throws IOException {

		boolean writeToDisk = true;

		if (outputFile == null) {
			writeToDisk = false;
		}

		final File file = new File(inputFile);

		if (!file.exists()) {
			throw new InvalidParameterException(file + " does not exist.");
		}

		final InputStream in = new FileInputStream(file);

		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;

		if (writeToDisk) {
			fOut = new FileOutputStream(new File(outputFile), false);
			osw = new OutputStreamWriter(fOut, Constants.UTF_8);
		}

		final BufferedReader br = new BufferedReader(new InputStreamReader(in,
				Constants.UTF_8));

		String strLine;

		while ((strLine = br.readLine()) != null) {

			processSingleLine(writeToDisk, osw, strLine);

		}

		if (writeToDisk) {
			osw.write(sb.toString());
			osw.flush();

			osw.close();
			fOut.close();
		}

		br.close();
		in.close();

	}

	final void processSingleLine(final boolean writeToDisk,
			final OutputStreamWriter osw, final String strLine)
			throws IOException {

		if (strLine.equalsIgnoreCase(Constants.EMPTY_STRING)) {
			return;
		}

		m.reset(strLine);

		while (m.find()) {
			onMatchFound(m, sb);
		}

		// TODO consider making this dynamic
		if (writeToDisk && sb.length() > _50000000) {

			osw.write(sb.toString());
			osw.flush();
			sb.setLength(0);

		}
	}

	/** intended for use with unit tests */
	public StringBuilder getSB() {
		return sb;
	}

	protected static final String filterTicks(final String group) {
		return group.replaceAll(Constants.BACKSLASH_TICK, Constants.TICK);
	}

}
