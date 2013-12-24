package net.vizipedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.HashBiMap;

public class WikipediaParserTest {

	@Test
	public void basicTest1() {

		final HashBiMap<String, Integer> mapping = HashBiMap.create();

		final InputStream in1 = getClass().getClassLoader()
				.getResourceAsStream(
						"net/vizipedia/processedMappingSample1.txt");

		try {
			WikipediaParser.loadMemoryMappingFromInputStream(mapping, in1);
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail();
		}

		final LineMatcher lm = LineMatchers.getLinkLineMatcher(mapping);

		final InputStream in2 = getClass().getClassLoader()
				.getResourceAsStream("net/vizipedia/rawLinkSample2.txt");

		final String inputSample = getContentsOfResourceFileAsString(in2);

		try {
			lm.processSingleLine(false, null, inputSample);
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals("10:12;\r\n12:10;", lm.getSB().toString());

	}

	@Test
	public void basicTest2() {

		final InputStream in = getClass().getClassLoader().getResourceAsStream(
				"net/vizipedia/rawPageSample1.txt");

		final String inputSample = getContentsOfResourceFileAsString(in);

		final HashBiMap<String, Integer> mapping = HashBiMap.create();

		final LineMatcher lm = LineMatchers.getMappingLineMatcher(mapping);

		try {
			lm.processSingleLine(false, null, inputSample);
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals(10214, mapping.size());

	}

	private static String getContentsOfResourceFileAsString(final InputStream in) {

		final StringBuilder sb = new StringBuilder();

		try {
			String strLine;

			final BufferedReader br = new BufferedReader(new InputStreamReader(
					in));

			while ((strLine = br.readLine()) != null) {

				if (strLine.compareTo(Constants.EMPTY_STRING) == 0) {
					continue;
				}
				sb.append(strLine);
			}

			br.close();
			in.close();
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail();
		}

		return sb.toString();

	}
}
