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
 * The interface for matching of a result.
 *
 * @param <T> the type of the fragments
 * @see Assertion#assertMatches(ResultCollector, java.util.List)
 */
public interface Matcher<T> {

	/**
	 * Checks whether the specified fragment(e.g. a contents of the line) is matched.
	 *
	 * @param fragment a contents of the fragment
	 * @return true if the fragment matches, otherwise false.
	 */
	public boolean matches(T fragment);

	/**
	 * Returns the {@link TestType}.
	 *
	 * @return the {@link TestType}
	 */
	public TestType getTestType();

	/**
	 * The test type.
	 *
	 */
	public enum TestType {

		/**
		 * Success if matched.
		 */
		SUCCESS_IF_MATCH,

		/**
		 * Success if not matched. In other words, it will be failed if matched.
		 */
		SUCCESS_IF_NOT_MATCH;
	}
}