/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @authors Nippon Telegraph and Telephone Corporation
 */

package jp.co.ntt.oss.jboss.byteman.framework.assertion;

/**
 * The implementation of {@link Matcher} for keyword matching.
 *
 */
public class KeywordMatcher implements Matcher<String> {

	private String keyword;

	/**
	 * Constructs a new instance with a keyword.
	 *
	 * @param keyword a keyword
	 */
	public KeywordMatcher(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * Checks whether specified line matches with keyword.
	 *
	 * @param line a contents of the line
	 * @return true if the line matches, otherwise false.
	 */
	@Override
	public boolean matches(String line) {
		return line.contains(keyword);
	}

	/**
	 * Returns the {@link TestType#SUCCESS_IF_MATCH}.
	 *
	 * @return the {@link TestType#SUCCESS_IF_MATCH}
	 */
	@Override
	public TestType getTestType() {
		return TestType.SUCCESS_IF_MATCH;
	}
}
