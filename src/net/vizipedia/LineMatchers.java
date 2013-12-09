package net.vizipedia;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

import com.google.common.collect.HashBiMap;

/**
 * 
 * Contains the implementations of the {@link LineMatcher} abstract class
 * 
 * @author rwalsh849
 *
 */
public class LineMatchers {

	public static LineMatcher getLinkLineMatcher(
			final HashBiMap<String, Integer> mapping) {
		final LineMatcher lm = new LineMatcher(
				"[\\s|,??]\\((\\d+?),(\\d+?),'(.+?)'\\)") {

			private Integer previousLinkFrom = Constants.NO_MAPPING;
			private boolean previousExists = false;

			@Override
			public final void onMatchFound(final Matcher m,
					final StringBuilder sb) {

				try {

					// only process items in namespace 0
					if (m.group(2).compareTo(Constants._0) == 0) {

						final Integer linkFrom = Integer.valueOf(m.group(1));

						// if we have been looking at the same linkFrom as the
						// previous matched item, then just process the linkTo
						if (linkFrom.compareTo(previousLinkFrom) == 0) {

							if (previousExists) {
								final String linkTo = filterTicks(m.group(3));

								appendIfMappingExists(mapping, sb, linkTo,
										Constants.SEMI_COLON);
							}

						} else {
							// we have a new linkFrom id, so start a new line,
							// if there was a previous item
							if (previousExists) {
								sb.append(Constants.CRLF);
							}

							previousLinkFrom = linkFrom;

							// TODO check that this is required etc
							final String linkTo = filterTicks(m.group(3));

							// only process this linkFrom if it exists in the
							// mapping
							final boolean mappingExists = appendIfMappingExists(
									mapping, sb, linkFrom, Constants.COLON);

							previousExists = mappingExists;

							if (mappingExists) {
								appendIfMappingExists(mapping, sb, linkTo,
										Constants.SEMI_COLON);
							}
						}
					}

				} catch (final ExecutionException e) {
					System.err.println("ERROR: " + e);
					System.exit(1);
				}

			}

			private final boolean appendIfMappingExists(
					final HashBiMap<String, Integer> mapping,
					final StringBuilder sb, final Object key,
					final String separator) throws ExecutionException {

				boolean mappingExists = true;

				Object value = null;
				String appendValue = null;

				if ((key instanceof String && (value = mapping.get(key)) != null)) {
					appendValue = ((Integer) value).toString();
				} else if ((key instanceof Integer && (mapping.inverse()
						.containsKey(key)))) {
					appendValue = ((Integer) key).toString();
				} else {
					mappingExists = false;
				}

				if (mappingExists) {
					sb.append(appendValue).append(separator);
				}

				return mappingExists;
			}
		};
		return lm;
	}

	public final static LineMatcher getMappingLineMatcher(
			final HashBiMap<String, Integer> mapping) {
		final LineMatcher lm = new LineMatcher(
				"[\\s|,??]\\((\\d+?),(\\d+?),'(.+?)','.+?\\)") {

			@Override
			public final void onMatchFound(final Matcher m,
					final StringBuilder sb) {
				final int ns = Integer.valueOf(m.group(2));

				if (ns == 0) {
					// || ns == 14) {
					// (14 is the namespace for categories)

					final String name = filterTicks(m.group(3));
					final String id = filterTicks(m.group(1));

					sb.append(name).append(Constants.TAB).append(id)
							.append(Constants.TAB).append(ns)
							.append(Constants.CRLF);

					mapping.put(name, Integer.valueOf(id));
				}

			}

		};
		return lm;
	}

}
