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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;
import jp.co.ntt.oss.jboss.byteman.framework.util.ResultRepository;
import jp.co.ntt.oss.jboss.byteman.framework.util.ServerCommandManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ResultFileCollectorTest {

	@Mock
	private ResultRepository repository;

	@Mock
	private Iterator<String> iterator;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void constructor_1() throws Exception {
		ResultFileCollector collector = new ResultFileCollector(
				repository, "IDENTIFIER", "TARGETPATH");
		assertEquals(repository, TestUtil.getValue(collector, "resultRepository"));
		assertEquals("IDENTIFIER", TestUtil.getValue(collector, "identifier"));
		assertEquals("TARGETPATH", TestUtil.getValue(collector, "targetPath"));
	}

	@Test
	public void setFilter_1() throws Exception {
		ResultFileCollector collector = new ResultFileCollector(
				repository, "IDENTIFIER", "TARGETPATH");
		FileFilter filter = mock(FileFilter.class);

		collector.setFilter(filter);

		assertEquals(filter, TestUtil.getValue(collector, "filter"));
	}

	@Test
	public void collect_1() throws Exception {
		ResultFileCollector collector = spy(new ResultFileCollector(
				repository, "IDENTIFIER", "TARGETPATH"));
		doReturn("data").when(collector).destPath(anyString());
		ServerCommandManager manager = mock(ServerCommandManager.class);
		collector.commandManager = manager;

		collector.collect();

		verify(manager).download("IDENTIFIER", "TARGETPATH", "data");
	}

	@Test
	public void getResult_1() {
		ResultFileCollector collector = spy(new ResultFileCollector(
				repository, "server1", "TARGETPATH"));
		doReturn(new ArrayList<Iterator<String>>()).when(collector).getCollectedFiles(any(File.class));
		when(repository.getPath()).thenReturn("data");

		List<Iterator<String>> result = collector.getResult();

		assertTrue(result.isEmpty());

		verify(collector).getCollectedFiles(new File("data", "server1"));
	}

	@Test
	public void getCollectedFiles_1() {
		ResultFileCollector collector = spy(new ResultFileCollector(
				repository, "IDENTIFIER", "TARGETPATH"));
		doReturn(iterator).when(collector).getFileIterator(any(File.class));

		File directory = mock(File.class);
		File file = mock(File.class);
		when(directory.listFiles(any(FileFilter.class))).thenReturn(new File[]{file});
		when(file.isDirectory()).thenReturn(false);

		List<Iterator<String>> result = collector.getCollectedFiles(directory);

		assertEquals(1, result.size());
		assertEquals(iterator, result.get(0));

		verify(collector).getFileIterator(file);
	}

	@Test
	public void getCollectedFiles_2() {
		ResultFileCollector collector = spy(new ResultFileCollector(
				repository, "IDENTIFIER", "TARGETPATH"));
		doReturn(iterator).when(collector).getFileIterator(any(File.class));

		File directory = mock(File.class);
		File subDirectory = mock(File.class);
		File file = mock(File.class);
		when(directory.listFiles(any(FileFilter.class))).thenReturn(new File[]{subDirectory});
		when(subDirectory.isDirectory()).thenReturn(true);
		when(subDirectory.listFiles(any(FileFilter.class))).thenReturn(new File[]{file});
		when(file.isDirectory()).thenReturn(false);

		List<Iterator<String>> result = collector.getCollectedFiles(directory);

		assertEquals(1, result.size());
		assertEquals(iterator, result.get(0));

		verify(collector).getFileIterator(file);
	}

	@Test
	public void getFileIterator_1() {
		ResultFileCollector collector = new ResultFileCollector(
				repository, "IDENTIFIER", "TARGETPATH");
		File file = new File("data", "server.log");

		Iterator<String> result = collector.getFileIterator(file);

		for(int i = 0; i < 4; i++) {
			assertTrue(result.hasNext());
			assertNotNull(result.next());
		}
		assertFalse(result.hasNext());
		assertNull(result.next());
	}

	@Test(expected = RuntimeException.class)
	public void getFileIterator_2() {
		ResultFileCollector collector = new ResultFileCollector(
				repository, "IDENTIFIER", "TARGETPATH");
		File file = new File("hoge.txt");

		Iterator<String> result = collector.getFileIterator(file);
		result.hasNext();
	}
}
