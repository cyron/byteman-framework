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

import static org.junit.Assert.*;
import jp.co.ntt.oss.jboss.byteman.framework.assertion.Matcher.TestType;

import org.junit.Test;

public class KeywordMatcherTest {

	@Test
	public void matches_1() {
		assertTrue(new KeywordMatcher("ERROR").matches("2011-12-13 11:51:29,316 ERROR [org.jboss.logging.Log4jService] Installed System.out adapter"));
		assertTrue(new KeywordMatcher("jboss").matches("2011-12-13 11:51:29,316 ERROR [org.jboss.logging.Log4jService] Installed System.out adapter"));
		assertTrue(new KeywordMatcher("2011-12-14 11:51").matches("2011-12-14 11:51:29,316 ERROR [org.jboss.logging.Log4jService] Installed System.out adapter"));
		assertTrue(new KeywordMatcher("adapter").matches("2011-12-14 11:51:29,316 ERROR [org.jboss.logging.Log4jService] Installed System.out adapter"));

		assertFalse(new KeywordMatcher("ERROR").matches("2011-12-13 11:51:29,316 DEBUG [org.jboss.logging.Log4jService] Installed System.out adapter"));
		assertFalse(new KeywordMatcher("JBoss").matches("2011-12-13 11:51:29,316 ERROR [org.jboss.logging.Log4jService] Installed System.out adapter"));
	}

	@Test
	public void matches_2() {
		assertFalse(new KeywordMatcher("ERROR").matches("2011-12-13 11:51:29,316 DEBUG [org.jboss.logging.Log4jService] Installed System.out adapter"));
		assertFalse(new KeywordMatcher("JBoss").matches("2011-12-13 11:51:29,316 ERROR [org.jboss.logging.Log4jService] Installed System.out adapter"));
	}
	@Test
	public void getTestType() {
		assertEquals(TestType.SUCCESS_IF_MATCH, new KeywordMatcher("").getTestType());
	}
}
