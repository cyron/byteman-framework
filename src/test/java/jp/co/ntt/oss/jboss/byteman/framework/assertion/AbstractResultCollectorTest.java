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
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;
import jp.co.ntt.oss.jboss.byteman.framework.util.ResultRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractResultCollectorTest {

	@Mock
	private ResultRepository repository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void constructor_1() throws Exception {
		AbstractResultCollector<Object> collector = getInstance();
		assertEquals(repository, TestUtil.getValue(collector, "resultRepository"));
	}

	@Test
	public void destPath_1() {
		AbstractResultCollector<Object> collector = getInstance();
		when(repository.getPath()).thenReturn("data");

		String actual = null;
		try {
			actual = collector.destPath("identifier");
			assertEquals("data" + File.separator + "identifier", actual);
		} finally {
			if(actual != null) new File(actual).delete();
		}
	}

	private AbstractResultCollector<Object> getInstance() {
		return new AbstractResultCollector<Object>(repository) {
			@Override
			public void collect() throws Exception {}
			@Override
			public List<Iterator<Object>> getResult() {
				return null;
			}
		};
	}

}