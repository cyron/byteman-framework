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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.ntt.oss.jboss.byteman.framework.assertion.Matcher.TestType;

/**
 * The utility class for asserting a result.
 *
 */
public class Assertion {

	/**
	 * Delegates to {@link #assertMatches(ResultCollector, List)}.
	 */
	public static <T> void assertMatches(ResultCollector<T> collector, Matcher<T> matcher) {
		List<Matcher<T>> matchers = new ArrayList<Matcher<T>>();
		matchers.add(matcher);
		assertMatches(collector, matchers);
	}

	/**
	 * Delegates to {@link #assertMatches(ResultCollector, List)}.
	 */
	public static <T> void assertMatches(ResultCollector<T> collector, Matcher<T> matcher1, Matcher<T> matcher2) {
		List<Matcher<T>> matchers = new ArrayList<Matcher<T>>();
		matchers.add(matcher1);
		matchers.add(matcher2);
		assertMatches(collector, matchers);
	}

	/**
	 * Delegates to {@link #assertMatches(ResultCollector, List)}.
	 */
	public static <T> void assertMatches(ResultCollector<T> collector, Matcher<T> matcher1, Matcher<T> matcher2, Matcher<T> matcher3) {
		List<Matcher<T>> matchers = new ArrayList<Matcher<T>>();
		matchers.add(matcher1);
		matchers.add(matcher2);
		matchers.add(matcher3);
		assertMatches(collector, matchers);
	}

	/**
	 * Checks whether all the matchers match collected data.
	 * If all the matchers don't match, throws {@link AssertionError}.
	 *
	 * @param collector {@link ResultCollector}
	 * @param matchers the list of {@link Matcher}
	 */
	public static <T> void assertMatches(ResultCollector<T> collector, List<Matcher<T>> matchers) {
		List<Matcher<T>> matcherList = new ArrayList<Matcher<T>>(matchers);
		// per data
		for (Iterator<T> ite : collector.getResult()) {
			while(ite.hasNext()) {
				T line = ite.next();
                int size = matcherList.size();
                for(int i = 0; i < size; i++){
                	Matcher<T> matcher = matcherList.get(i);
                	if(matcher.matches(line)){
    					switch (matcher.getTestType()) {
    					case SUCCESS_IF_MATCH:
    						matcherList.remove(i);
    						size--;
    						i--;
    						break;
    					case SUCCESS_IF_NOT_MATCH:
    						throw new AssertionError(String.format("Matched by [%s] at [%s]", matcher, line));
    					}
                	}
                }
                if(matcherList.isEmpty()){
                	return;
                }
			}
		}
		for(Matcher<T> matcher: matcherList){
			if(TestType.SUCCESS_IF_MATCH == matcher.getTestType()){
				throw new AssertionError(String.format("Not matched by [%s]", matcher));
			}
		}
	}

}