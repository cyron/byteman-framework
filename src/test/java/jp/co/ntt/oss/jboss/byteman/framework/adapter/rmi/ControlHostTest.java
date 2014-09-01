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

package jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.ControlHost.JoinServerThread;

import org.jboss.byteman.rule.helper.Helper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ControlHostTest {

	private ControlHost controlHost;

	@Mock
	private Helper helper;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		controlHost = spy(new ControlHost());
		TestUtil.setValue(controlHost, "helper", helper);
	}

	@Test
	public void joinEnlist_1() throws Exception {
		try {
			controlHost.joinEnlist("");
			fail();
		} catch (UnsupportedOperationException e) {
		}
	}

	@Test
	public void joinEnlistStart_1() throws Exception {
		// stubbing
		String identifier = "test";
		@SuppressWarnings("unchecked")
		Map<String, JoinServerThread> threadMap = (Map<String, JoinServerThread>) TestUtil.getValue(controlHost, "joinThreadMap");

		when(helper.joinEnlist(identifier)).thenReturn(true);

		assertEquals(0, threadMap.size());

		String result = controlHost.joinEnlistStart(identifier);
		assertNotNull(result);
		assertEquals(1, threadMap.size());

		verify(helper).joinEnlist(identifier);
	}

	@Test
	public void joinEnlistStart_2() throws Exception {
		// stubbing
		String identifier = "test";
		@SuppressWarnings("unchecked")
		Map<String, JoinServerThread> threadMap = (Map<String, JoinServerThread>) TestUtil.getValue(controlHost, "joinThreadMap");

		when(helper.joinEnlist(identifier)).thenReturn(false);

		assertEquals(0, threadMap.size());

		String result = controlHost.joinEnlistStart(identifier);
		assertNull(result);
		assertEquals(0, threadMap.size());

		verify(helper).joinEnlist(identifier);
	}

	@Test
	public void joinEnlistEnd_1() throws Exception {
		// stubbing
		String identifier = "test";
		Map<String, JoinServerThread> threadMap = new HashMap<String, ControlHost.JoinServerThread>();
		JoinServerThread thread = mock(JoinServerThread.class);
		threadMap.put(identifier, thread);
		TestUtil.setValue(controlHost, "joinThreadMap", threadMap);

		assertEquals(1, threadMap.size());
		controlHost.joinEnlistEnd(identifier);
		assertEquals(0, threadMap.size());

	}

	@Test
	public void joinEnlistEnd_2() throws Exception {
		// stubbing
		String identifier = "test";
		Map<String, JoinServerThread> threadMap = new HashMap<String, ControlHost.JoinServerThread>();
		JoinServerThread thread = mock(JoinServerThread.class);
		threadMap.put(identifier, thread);
		TestUtil.setValue(controlHost, "joinThreadMap", threadMap);

		assertEquals(1, threadMap.size());
		controlHost.joinEnlistEnd("dummy");
		assertEquals(1, threadMap.size());

	}

	@Test
	public void testJoinServerThread() throws Exception {
		TestUtil.setValue(controlHost, "helper", helper);
		when(helper.joinEnlist("id")).thenReturn(true);
		Object parentLock = mock(Object.class);

		JoinServerThread thread = controlHost.new JoinServerThread("id", parentLock);
		thread.start();

		Thread.sleep(500);

		assertTrue(thread.isResult());
		assertTrue(thread.isEnlistFinished());
		assertTrue(thread.isAlive());

		synchronized(thread){
			thread.notify();
		}
		Thread.sleep(500);

		assertFalse(thread.isAlive());
	}

}
