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

import static org.mockito.Mockito.*;
import static jp.co.ntt.oss.jboss.byteman.framework.assertion.Matcher.TestType.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AssertionTest {

	@Mock
	private ResultCollector<String> collector;

	@Mock
	private Matcher<String> matcher1;

	@Mock
	private Matcher<String> matcher2;
	
	@Mock
	private Matcher<String> matcher3;
	
	@Mock
	private Iterator<String> iterator;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Success {@link SUCCESS_IF_MATCH}
	 */
	@Test
	public void assertMatches_1() {
		List<Iterator<String>> list = new ArrayList<Iterator<String>>();
		list.add(iterator);

		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn("value");
		when(collector.getResult()).thenReturn(list);
		when(matcher1.matches(anyString())).thenReturn(true);
		when(matcher1.getTestType()).thenReturn(SUCCESS_IF_MATCH);

		Assertion.assertMatches(collector, matcher1);

		verify(collector).getResult();
		verify(iterator, times(1)).hasNext();
		verify(iterator).next();
		verify(matcher1).matches("value");
		verify(matcher1).getTestType();
	}

	/**
	 * Success {@link SUCCESS_IF_NOT_MATCH}
	 */
	@Test
	public void assertMatches_2() {
		List<Iterator<String>> list = new ArrayList<Iterator<String>>();
		list.add(iterator);

		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn("value");
		when(collector.getResult()).thenReturn(list);
		when(matcher1.matches(anyString())).thenReturn(false);
		when(matcher1.getTestType()).thenReturn(SUCCESS_IF_NOT_MATCH);

		Assertion.assertMatches(collector, matcher1);

		verify(collector).getResult();
		verify(iterator, times(2)).hasNext();
		verify(iterator).next();
		verify(matcher1).matches("value");
		verify(matcher1).getTestType();
	}

	/**
	 * Failure {@link SUCCESS_IF_MATCH}
	 */
	@Test(expected = AssertionError.class)
	public void assertMatches_3() {
		List<Iterator<String>> list = new ArrayList<Iterator<String>>();
		list.add(iterator);

		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn("value");
		when(collector.getResult()).thenReturn(list);
		when(matcher1.matches(anyString())).thenReturn(false);
		when(matcher1.getTestType()).thenReturn(SUCCESS_IF_MATCH);

		Assertion.assertMatches(collector, matcher1);
	}

	/**
	 * Failure {@link SUCCESS_IF_NOT_MATCH}
	 */
	@Test(expected = AssertionError.class)
	public void assertMatches_4() {
		List<Iterator<String>> list = new ArrayList<Iterator<String>>();
		list.add(iterator);

		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn("value");
		when(collector.getResult()).thenReturn(list);
		when(matcher1.matches(anyString())).thenReturn(true);
		when(matcher1.getTestType()).thenReturn(SUCCESS_IF_NOT_MATCH);

		Assertion.assertMatches(collector, matcher1);
	}

	@Test
	public void assertMatches_5() {
		List<Iterator<String>> list = new ArrayList<Iterator<String>>();
		list.add(iterator);

		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn("value");
		when(collector.getResult()).thenReturn(list);

		when(matcher1.matches(anyString())).thenReturn(true);
		when(matcher1.getTestType()).thenReturn(SUCCESS_IF_MATCH);
		
		when(matcher2.matches(anyString())).thenReturn(true);
		when(matcher2.getTestType()).thenReturn(SUCCESS_IF_MATCH);

		Assertion.assertMatches(collector, matcher1, matcher2);

		verify(matcher1).matches("value");
		verify(matcher1).getTestType();
		verify(matcher2).matches("value");
		verify(matcher2).getTestType();
	}

	@Test
	public void assertMatches_6() {
		List<Iterator<String>> list = new ArrayList<Iterator<String>>();
		list.add(iterator);

		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn("value");
		when(collector.getResult()).thenReturn(list);

		when(matcher1.matches(anyString())).thenReturn(true);
		when(matcher1.getTestType()).thenReturn(SUCCESS_IF_MATCH);

		when(matcher2.matches(anyString())).thenReturn(false);
		when(matcher2.getTestType()).thenReturn(SUCCESS_IF_NOT_MATCH);
		
		when(matcher3.matches(anyString())).thenReturn(true);
		when(matcher3.getTestType()).thenReturn(SUCCESS_IF_MATCH);
		
		Assertion.assertMatches(collector, matcher1, matcher2, matcher3);

		verify(matcher1).matches("value");
		verify(matcher1).getTestType();
		verify(matcher2).matches("value");
		verify(matcher2).getTestType();
		verify(matcher3).matches("value");
		verify(matcher3).getTestType();
	}

}