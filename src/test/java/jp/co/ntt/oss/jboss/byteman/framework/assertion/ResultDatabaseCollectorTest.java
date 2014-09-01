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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;
import jp.co.ntt.oss.jboss.byteman.framework.util.ResultRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ResultDatabaseCollectorTest {

	@Mock
	private ResultRepository repository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void constructor_1() throws Exception {
		ResultDatabaseCollector collector = new ResultDatabaseCollector(
				repository, "URL", "USERNAME", "PASSWORD", "SQL");
		assertEquals(repository, TestUtil.getValue(collector, "resultRepository"));
		assertEquals("URL", TestUtil.getValue(collector, "url"));
		assertEquals("USERNAME", TestUtil.getValue(collector, "username"));
		assertEquals("PASSWORD", TestUtil.getValue(collector, "password"));
		assertEquals("SQL", TestUtil.getValue(collector, "sql"));
	}

	@Test
	public void collect_1() throws Exception {
		Connection con = mock(Connection.class);
		PreparedStatement ps = mock(PreparedStatement.class);
		ResultSet rs = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);

		ResultDatabaseCollector collector = spy(new ResultDatabaseCollector(
				repository, "URL", "USERNAME", "PASSWORD", "SQL"));
		doReturn(con).when(collector).getConnection();
		when(con.prepareStatement(anyString())).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.getMetaData()).thenReturn(meta);
		when(rs.next()).thenReturn(true, false);
		when(rs.getObject(anyInt())).thenReturn("value");
		when(meta.getColumnCount()).thenReturn(1);
		when(meta.getColumnName(anyInt())).thenReturn("ColumnName");

		collector.collect();

		assertEquals(1, collector.getResult().size());
		assertTrue(collector.getResult().get(0).hasNext());
		Map<String, Object> expected = new HashMap<String, Object>();
		expected.put("columnname", "value");
		assertEquals(expected, collector.getResult().get(0).next());
		assertFalse(collector.getResult().get(0).hasNext());

		verify(con).prepareStatement("SQL");
		verify(con).close();
		verify(ps).executeQuery();
		verify(ps).close();
		verify(rs, times(2)).next();
		verify(rs).close();
	}

	@Test
	public void getResult_1() throws Exception {
		ResultDatabaseCollector collector = new ResultDatabaseCollector(
				repository, "URL", "USERNAME", "PASSWORD", "SQL");

		assertEquals(TestUtil.getValue(collector, "rows"), collector.getResult());
	}
}
