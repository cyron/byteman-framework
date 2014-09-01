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

package jp.co.ntt.oss.jboss.byteman.framework.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;

import org.junit.Test;

public class ResultRepositoryTest {
	@Test
	public void constructor_1() throws Exception {
		ResultRepository repository = new ResultRepository("ClassName", "methodName");
		assertEquals("ClassName", TestUtil.getValue(repository, "className"));
		assertEquals("methodName", TestUtil.getValue(repository, "methodName"));
	}

	@Test
	public void constructor_2() {
		try {
			new ResultRepository(null, "methodName");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("The class name is empty.", e.getMessage());
		}
	}

	@Test
	public void constructor_3() {
		try {
			new ResultRepository("", "methodName");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("The class name is empty.", e.getMessage());
		}
	}

	@Test
	public void constructor_4() {
		try {
			new ResultRepository("ClassName", null);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("The method name is empty.", e.getMessage());
		}
	}

	@Test
	public void constructor_5() {
		try {
			new ResultRepository("ClassName", "");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("The method name is empty.", e.getMessage());
		}
	}

	@Test
	public void getPath_1() {
		ResultRepository repository = new ResultRepository("ClassName", "methodName");
		assertEquals(
			"data" + File.separator + "ClassName" + File.separator + "methodName",
			repository.getPath());
	}

	@Test
	public void clear_1() {
		ResultRepository repository = new ResultRepository("ClassName", "methodName");

		File dir = new File(new File("data", "ClassName"), "methodName");
		assertTrue(dir.mkdirs());

		try {
			repository.clear();
			assertFalse(dir.exists());
		} finally {
			dir.delete();
			dir.getParentFile().delete();
		}
	}

	@Test
	public void clear_2() throws IOException {
		ResultRepository repository = new ResultRepository("ClassName", "methodName");

		File dir = new File(new File("data", "ClassName"), "methodName");
		assertTrue(dir.mkdirs());
		File file = new File(dir, "file");
		assertTrue(file.createNewFile());

		try {
			repository.clear();
			assertFalse(file.exists());
			assertFalse(dir.exists());
		} finally {
			file.delete();
			dir.delete();
			dir.getParentFile().delete();
		}
	}

}