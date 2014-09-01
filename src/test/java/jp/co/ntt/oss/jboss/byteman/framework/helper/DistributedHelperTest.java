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

package jp.co.ntt.oss.jboss.byteman.framework.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.DistributedAdapter;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.CallbackIF;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.CallbackInvoker;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.ControlHost;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.ControlIF;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DistributedHelperTest {

	private DistributedHelper helper;

	@Mock
	private DistributedAdapter adapter;

	private Remote stub;

	private static Registry registry;

	@Mock
	private Rule rule;

	@org.junit.Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setupBefore() throws Exception {
		System.setProperty(DistributedHelper.PROP_PORT, "9099");
		registry = LocateRegistry.createRegistry(9099);
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(rule.getName()).thenReturn(name.getMethodName());
	}

	@After
	public void tearDown() {
		try {
			registry.unbind(ControlIF.CONTROL_ID);
		} catch (Exception e) {
		}
	}

	private void setupHelper() throws Exception {
		setupRegistry();
		helper = new DistributedHelper(rule);
		helper.adapter = adapter;
	}

	private void setupRegistry() throws Exception {
		ControlHost host = mock(ControlHost.class);
		stub = UnicastRemoteObject.exportObject(host, 0);
		registry.rebind(ControlIF.CONTROL_ID, stub);
	}

	@Test
	public void initAdapter_1() throws Exception {
		// stubbing
		setupRegistry();

		helper = new DistributedHelper(rule);
		assertEquals(helper.adapter, stub);
	}

	@Test
	public void initAdapter_2() {
		try {
			helper = new DistributedHelper(rule);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule initAdapter_2 : initAdapter ", e.getMessage());
		}
	}

	/* waiting */
	@Test
	public void waiting_1() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		when(adapter.waiting(identifier)).thenReturn(true);

		boolean result = helper.waiting(identifier);

		assertTrue(result);
		verify(adapter).waiting(identifier);
	}

	@Test
	public void waiting_2() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		when(adapter.waiting(identifier)).thenThrow(new Exception("error!!!"));

		try {
			helper.waiting(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule waiting_2 : waiting ", e.getMessage());
			verify(adapter).waiting(identifier);
		}
	}

	/* waitFor */
	/**
	 * no argument
	 */
	@Test
	public void waitFor_1() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		helper.waitFor(identifier);

		verify(adapter).waitFor(identifier, 0);
	}

	/**
	 * argument is specified.
	 */
	@Test
	public void waitFor_2() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		helper.waitFor(identifier, 1);

		verify(adapter).waitFor(identifier, 1);
	}

	@Test
	public void waitFor_3() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		doThrow(new Exception()).when(adapter).waitFor(identifier, 0);

		try {
			helper.waitFor(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule waitFor_3 : waitFor ", e.getMessage());
			verify(adapter).waitFor(identifier, 0);
		}
	}

	/* signalWake */
	/**
	 * no arguments
	 */
	@Test
	public void signalWake_1() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		when(adapter.signalWake(identifier, false)).thenReturn(true);

		boolean result = helper.signalWake(identifier);

		assertTrue(result);
		verify(adapter).signalWake(identifier, false);
	}

	/**
	 * argument is specified.
	 */
	@Test
	public void signalWake_2() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		when(adapter.signalWake(identifier, true)).thenReturn(true);

		boolean result = helper.signalWake(identifier, true);

		assertTrue(result);
		verify(adapter).signalWake(identifier, true);
	}

	@Test
	public void signalWake_3() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		when(adapter.signalWake(identifier, false)).thenThrow(new Exception());

		try {
			helper.signalWake(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule signalWake_3 : signalWake ", e.getMessage());
			verify(adapter).signalWake(identifier, false);
		}
	}

	/* signalThrow */
	/**
	 * no arguments.
	 */
	@Test
	public void signalThrow_1() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		when(adapter.signalThrow(identifier, false)).thenReturn(true);

		boolean result = helper.signalThrow(identifier);

		assertTrue(result);
		verify(adapter).signalThrow(identifier, false);
	}

	/**
	 * argument is specified.
	 */
	@Test
	public void signalThrow_2() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		when(adapter.signalThrow(identifier, true)).thenReturn(true);

		boolean result = helper.signalThrow(identifier, true);

		assertTrue(result);
		verify(adapter).signalThrow(identifier, true);
	}

	@Test
	public void signalThrow_3() throws Exception {
		// stubbing
		setupHelper();

		String identifier = "test";
		when(adapter.signalThrow(identifier, false)).thenThrow(new Exception());

		try {
			helper.signalThrow(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule signalThrow_3 : signalThrow ", e.getMessage());
			verify(adapter).signalThrow(identifier, false);
		}
	}

	/* rendezvous */
	@Test
	public void createRendezvous_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createRendezvous(identifier, 5, false)).thenReturn(true);

		boolean createRendezvous = helper.createRendezvous(identifier, 5);

		assertTrue(createRendezvous);
		verify(adapter).createRendezvous(identifier, 5, false);
	}

	@Test
	public void createRendezvous_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createRendezvous(identifier, 5, true)).thenReturn(true);

		boolean createRendezvous = helper.createRendezvous(identifier, 5, true);

		assertTrue(createRendezvous);
		verify(adapter).createRendezvous(identifier, 5, true);
	}

	@Test
	public void createRendezvous_3() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createRendezvous(identifier, 5, false)).thenThrow(new Exception());

		try {
			helper.createRendezvous(identifier, 5);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule createRendezvous_3 : createRendezvous ", e.getMessage());
			verify(adapter).createRendezvous(identifier, 5, false);
		}
	}

	@Test
	public void isRendezvous_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.isRendezvous(identifier, 5)).thenReturn(true);

		boolean result = helper.isRendezvous(identifier, 5);

		assertTrue(result);
		verify(adapter).isRendezvous(identifier, 5);
	}

	@Test
	public void isRendezvous_2() throws Exception {
		setupHelper();
		String identifier = "test";
		when(adapter.isRendezvous(identifier, 5)).thenThrow(new Exception());

		try {
			helper.isRendezvous(identifier, 5);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule isRendezvous_2 : isRendezvous ", e.getMessage());
			verify(adapter).isRendezvous(identifier, 5);
		}
	}

	@Test
	public void getRendezvous_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.getRendezvous(identifier, 5)).thenReturn(3);

		int result = helper.getRendezvous(identifier, 5);

		assertEquals(3, result);
		verify(adapter).getRendezvous(identifier, 5);
	}

	@Test
	public void getRendezvous_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.getRendezvous(identifier, 5)).thenThrow(new Exception());

		try {
			helper.getRendezvous(identifier, 5);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule getRendezvous_2 : getRendezvous ", e.getMessage());
			verify(adapter).getRendezvous(identifier, 5);
		}
	}

	@Test
	public void rendezvous_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.rendezvous(identifier)).thenReturn(4);

		int result = helper.rendezvous(identifier);

		assertEquals(4, result);
		verify(adapter).rendezvous(identifier);
	}

	@Test
	public void rendezvous_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.rendezvous(identifier)).thenThrow(new Exception());

		try {
			helper.rendezvous(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule rendezvous_2 : rendezvous ", e.getMessage());
			verify(adapter).rendezvous(identifier);
		}
	}

	@Test
	public void deleteRendezvous_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.deleteRendezvous(identifier, 4)).thenReturn(true);

		boolean result = helper.deleteRendezvous(identifier, 4);

		assertTrue(result);
		verify(adapter).deleteRendezvous(identifier, 4);
	}

	@Test
	public void deleteRendezvous_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.deleteRendezvous(identifier, 4)).thenThrow(new Exception());

		try {
			helper.deleteRendezvous(identifier, 4);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule deleteRendezvous_2 : deleteRendezvous ", e.getMessage());
			verify(adapter).deleteRendezvous(identifier, 4);
		}
	}

	/* createJoin */
	@Test
	public void createJoin_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createJoin(identifier, 10)).thenReturn(true);

		boolean result = helper.createJoin(identifier, 10);

		assertTrue(result);
		verify(adapter).createJoin(identifier, 10);
	}

	@Test
	public void createJoin_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createJoin(identifier, 10)).thenThrow(new Exception());

		try {
			helper.createJoin(identifier, 10);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule createJoin_2 : createJoin ", e.getMessage());
			verify(adapter).createJoin(identifier, 10);
		}
	}

	@Test
	public void isJoin_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.isJoin(identifier, 10)).thenReturn(true);

		boolean result = helper.isJoin(identifier, 10);

		assertTrue(result);
		verify(adapter).isJoin(identifier, 10);
	}

	@Test
	public void isJoin_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.isJoin(identifier, 10)).thenThrow(new Exception());

		try {
			helper.isJoin(identifier, 10);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule isJoin_2 : isJoin ", e.getMessage());
			verify(adapter).isJoin(identifier, 10);
		}
	}

	@Test
	public void joinEnlist_1() throws Exception {
		// stubbing
		setupRegistry();
		helper = new DistributedHelper(rule);
		ControlIF adapter = mock(ControlIF.class);
		helper.adapter = adapter;

		String identifier = name.getMethodName();
		String serverThreadKey = "key";

		when(adapter.joinEnlistStart(identifier)).thenReturn(serverThreadKey);

		@SuppressWarnings("unchecked")
		Map<Object, List<Thread>> threadMap = (Map<Object, List<Thread>>) TestUtil.getValue(helper, "joinThreadMap");

		assertEquals(0, threadMap.size());
		// first time
		boolean result = helper.joinEnlist(identifier);
		assertTrue(result);

		assertEquals(1, threadMap.size());

		List<Thread> threads = threadMap.get(identifier);
		assertEquals(1, threads.size());
		assertSame(Thread.currentThread(), threads.get(0));

		// second time
		result = helper.joinEnlist(identifier);
		assertFalse(result);

		verify(adapter).joinEnlistStart(identifier);
	}

	@Test
	public void joinEnlist_2() throws Exception {
		// stubbing
		setupRegistry();
		helper = new DistributedHelper(rule);
		ControlIF adapter = mock(ControlIF.class);
		helper.adapter = adapter;

		String identifier = name.getMethodName();

		boolean result = helper.joinEnlist(identifier);
		assertFalse(result);

		verify(adapter).joinEnlistStart(identifier);
	}

	/**
	 * 2 threads use same identifier
	 */
	@Test
	public void joinEnlist_3() throws Exception {
		// stubbing
		setupRegistry();
		helper = new DistributedHelper(rule);
		ControlIF adapter = mock(ControlIF.class);
		helper.adapter = adapter;

		String identifier = name.getMethodName();
		String serverThreadKey1 = "key1";
		String serverThreadKey2 = "key2";

		when(adapter.joinEnlistStart(identifier)).thenReturn(serverThreadKey1).thenReturn(serverThreadKey2);

		TestJoinEnlistThread thread1 = new TestJoinEnlistThread(identifier);
		thread1.start();
		Thread.sleep(500);
		assertTrue(thread1.isResult());

		TestJoinEnlistThread thread2 = new TestJoinEnlistThread(identifier);
		thread2.start();
		Thread.sleep(500);
		assertTrue(thread2.isResult());

		verify(adapter, times(2)).joinEnlistStart(identifier);
		verify(adapter).joinEnlistEnd(serverThreadKey1);
		verify(adapter).joinEnlistEnd(serverThreadKey2);
	}

	private class TestJoinEnlistThread extends Thread {
		private boolean result;
		private Object identifier;

		public TestJoinEnlistThread(Object identifier) {
			this.identifier = identifier;
		}

		@Override
		public void run() {
			result = helper.joinEnlist(identifier);
		}

		public boolean isResult() {
			return result;
		}
	}

	@Test
	public void joinEnlist_4() throws Exception {
		// stubbing
		setupRegistry();
		DistributedHelper helper = new DistributedHelper(rule);
		ControlIF adapter = mock(ControlIF.class);
		helper.adapter = adapter;
		String identifier = name.getMethodName();
		when(adapter.joinEnlistStart(identifier)).thenThrow(new RemoteException());

		try {
			helper.joinEnlist(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule joinEnlist_4 : joinEnlist ", e.getMessage());
			verify(adapter).joinEnlistStart(identifier);
		}
	}

	@Test
	public void joinWait_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.joinWait(identifier, 3)).thenReturn(true);

		boolean result = helper.joinWait(identifier, 3);

		assertTrue(result);
		verify(adapter).joinWait(identifier, 3);
	}

	@Test
	public void joinWait_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.joinWait(identifier, 3)).thenThrow(new Exception());

		try {
			helper.joinWait(identifier, 3);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule joinWait_2 : joinWait ", e.getMessage());
			verify(adapter).joinWait(identifier, 3);
		}
	}

	/* flag */
	@Test
	public void flag_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.flag(identifier)).thenReturn(true);

		boolean result = helper.flag(identifier);

		assertTrue(result);
		verify(adapter).flag(identifier);
	}

	@Test
	public void flag_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.flag(identifier)).thenThrow(new Exception());

		try {
			helper.flag(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule flag_2 : flag ", e.getMessage());
			verify(adapter).flag(identifier);
		}
	}

	@Test
	public void flagged_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.flagged(identifier)).thenReturn(true);

		boolean result = helper.flagged(identifier);

		assertTrue(result);
		verify(adapter).flagged(identifier);
	}

	@Test
	public void flagged_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.flagged(identifier)).thenThrow(new Exception());

		try {
			helper.flagged(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule flagged_2 : flagged ", e.getMessage());
			verify(adapter).flagged(identifier);
		}
	}

	@Test
	public void clear_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.clear(identifier)).thenReturn(true);

		boolean result = helper.clear(identifier);

		assertTrue(result);
		verify(adapter).clear(identifier);
	}

	@Test
	public void clear_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.clear(identifier)).thenThrow(new Exception());

		try {
			helper.clear(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule clear_2 : clear ", e.getMessage());
			verify(adapter).clear(identifier);
		}
	}

	/* countdown */
	@Test
	public void isCountDown_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.isCountDown(identifier)).thenReturn(true);

		boolean result = helper.isCountDown(identifier);

		assertTrue(result);
		verify(adapter).isCountDown(identifier);
	}

	@Test
	public void isCountDown_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.isCountDown(identifier)).thenThrow(new Exception());

		try {
			helper.isCountDown(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule isCountDown_2 : isCountDown ", e.getMessage());
			verify(adapter).isCountDown(identifier);
		}
	}

	@Test
	public void createCountDown_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.createCountDown(identifier, 5)).thenReturn(true);

		boolean result = helper.createCountDown(identifier, 5);

		assertTrue(result);
		verify(adapter).createCountDown(identifier, 5);
	}

	@Test
	public void createCountDown_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createCountDown(identifier, 5)).thenThrow(new Exception());

		try {
			helper.createCountDown(identifier, 5);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule createCountDown_2 : createCountDown ", e.getMessage());
			verify(adapter).createCountDown(identifier, 5);
		}
	}

	@Test
	public void countDown_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.countDown(identifier)).thenReturn(true);

		boolean result = helper.countDown(identifier);

		assertTrue(result);
		verify(adapter).countDown(identifier);
	}

	@Test
	public void countDown_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.countDown(identifier)).thenThrow(new Exception());

		try {
			helper.countDown(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule countDown_2 : countDown ", e.getMessage());
			verify(adapter).countDown(identifier);
		}
	}

	/* counter */
	@Test
	public void createCounter_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.createCounter(identifier, 0)).thenReturn(true);

		boolean result = helper.createCounter(identifier);

		assertTrue(result);
		verify(adapter).createCounter(identifier, 0);
	}

	@Test
	public void createCounter_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.createCounter(identifier, 5)).thenReturn(true);

		boolean result = helper.createCounter(identifier, 5);

		assertTrue(result);
		verify(adapter).createCounter(identifier, 5);
	}

	@Test
	public void createCounter_3() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createCounter(identifier, 5)).thenThrow(new Exception());

		try {
			helper.createCounter(identifier, 5);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule createCounter_3 : createCounter ", e.getMessage());
			verify(adapter).createCounter(identifier, 5);
		}
	}

	@Test
	public void deleteCounter_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.deleteCounter(identifier)).thenReturn(true);

		boolean result = helper.deleteCounter(identifier);

		assertTrue(result);
		verify(adapter).deleteCounter(identifier);
	}

	@Test
	public void deleteCounter_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.deleteCounter(identifier)).thenThrow(new Exception());

		try {
			helper.deleteCounter(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule deleteCounter_2 : deleteCounter ", e.getMessage());
			verify(adapter).deleteCounter(identifier);
		}
	}

	@Test
	public void readCounter_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.readCounter(identifier, false)).thenReturn(4);

		int result = helper.readCounter(identifier);

		assertEquals(4, result);
		verify(adapter).readCounter(identifier, false);
	}

	@Test
	public void readCounter_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.readCounter(identifier, true)).thenReturn(5);

		int result = helper.readCounter(identifier, true);

		assertEquals(5, result);
		verify(adapter).readCounter(identifier, true);
	}

	@Test
	public void readCounter_3() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.readCounter(identifier, false)).thenThrow(new Exception());

		try {
			helper.readCounter(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule readCounter_3 : readCounter ", e.getMessage());
			verify(adapter).readCounter(identifier, false);
		}
	}

	@Test
	public void incrementCounter_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.incrementCounter(identifier, 1)).thenReturn(2);

		int result = helper.incrementCounter(identifier);

		assertEquals(2, result);
		verify(adapter).incrementCounter(identifier, 1);
	}

	@Test
	public void incrementCounter_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.incrementCounter(identifier, 3)).thenReturn(5);

		int result = helper.incrementCounter(identifier, 3);

		assertEquals(5, result);
		verify(adapter).incrementCounter(identifier, 3);
	}

	@Test
	public void incrementCounter_3() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.incrementCounter(identifier, 1)).thenThrow(new Exception());

		try {
			helper.incrementCounter(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule incrementCounter_3 : incrementCounter ", e.getMessage());
			verify(adapter).incrementCounter(identifier, 1);
		}
	}

	@Test
	public void decrementCounter_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		when(adapter.decrementCounter(identifier)).thenReturn(1);

		int result = helper.decrementCounter(identifier);

		assertEquals(1, result);
		verify(adapter).decrementCounter(identifier);
	}

	@Test
	public void decrementCounter_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.decrementCounter(identifier)).thenThrow(new Exception());

		try {
			helper.decrementCounter(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule decrementCounter_2 : decrementCounter ", e.getMessage());
			verify(adapter).decrementCounter(identifier);
		}
	}

	/* timer */
	@Test
	public void createTimer_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createTimer(identifier)).thenReturn(true);

		boolean result = helper.createTimer(identifier);

		assertTrue(result);
		verify(adapter).createTimer(identifier);
	}

	@Test
	public void createTimer_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.createTimer(identifier)).thenThrow(new Exception());

		try {
			helper.createTimer(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule createTimer_2 : createTimer ", e.getMessage());
			verify(adapter).createTimer(identifier);
		}
	}

	@Test
	public void deleteTimer_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.deleteTimer(identifier)).thenReturn(true);

		boolean result = helper.deleteTimer(identifier);

		assertTrue(result);
		verify(adapter).deleteTimer(identifier);
	}

	@Test
	public void deleteTimer_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.deleteTimer(identifier)).thenThrow(new Exception());

		try {
			helper.deleteTimer(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule deleteTimer_2 : deleteTimer ", e.getMessage());
			verify(adapter).deleteTimer(identifier);
		}
	}

	@Test
	public void getElapsedTimeFromTimer_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.getElapsedTimeFromTimer(identifier)).thenReturn(5L);

		long result = helper.getElapsedTimeFromTimer(identifier);

		assertEquals(5L, result);
		verify(adapter).getElapsedTimeFromTimer(identifier);
	}

	@Test
	public void getElapsedTimeFromTimer_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.getElapsedTimeFromTimer(identifier)).thenThrow(new Exception());

		try {
			helper.getElapsedTimeFromTimer(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule getElapsedTimeFromTimer_2 : getElapsedTimeFromTimer ", e.getMessage());
			verify(adapter).getElapsedTimeFromTimer(identifier);
		}
	}

	@Test
	public void resetTimer_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.resetTimer(identifier)).thenReturn(10L);

		long result = helper.resetTimer(identifier);

		assertEquals(10L, result);
		verify(adapter).resetTimer(identifier);
	}

	@Test
	public void resetTimer_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.resetTimer(identifier)).thenThrow(new Exception());

		try {
			helper.resetTimer(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule resetTimer_2 : resetTimer ", e.getMessage());
			verify(adapter).resetTimer(identifier);
		}
	}

	/* trace */
	@Test
	public void traceOpen_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.traceOpen(identifier, null)).thenReturn(true);

		boolean result = helper.traceOpen(identifier);

		assertTrue(result);
		verify(adapter).traceOpen(identifier, null);
	}

	@Test
	public void traceOpen_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.traceOpen(identifier, "test")).thenReturn(true);

		boolean result = helper.traceOpen(identifier, "test");

		assertTrue(result);
		verify(adapter).traceOpen(identifier, "test");
	}

	@Test
	public void traceOpen_3() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.traceOpen(identifier, null)).thenThrow(new Exception());

		try {
			helper.traceOpen(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule traceOpen_3 : traceOpen ", e.getMessage());
			verify(adapter).traceOpen(identifier, null);
		}
	}

	@Test
	public void traceClose_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.traceClose(identifier)).thenReturn(true);

		boolean result = helper.traceClose(identifier);

		assertTrue(result);
		verify(adapter).traceClose(identifier);
	}

	@Test
	public void traceClose_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.traceClose(identifier)).thenThrow(new Exception());

		try {
			helper.traceClose(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule traceClose_2 : traceClose ", e.getMessage());
			verify(adapter).traceClose(identifier);
		}
	}

	public void trace_1() throws Exception {
		// stubbing
		setupHelper();
		when(adapter.trace("out", "msg")).thenReturn(true);

		boolean result = helper.trace("msg");

		assertTrue(result);
		verify(adapter).trace("out", "msg");
	}

	@Test
	public void trace_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.trace(identifier, "msg")).thenReturn(true);

		boolean result = helper.trace(identifier, "msg");

		assertTrue(result);
		verify(adapter).trace(identifier, "msg");
	}

	@Test
	public void trace_3() throws Exception {
		// stubbing
		setupHelper();
		when(adapter.trace("out", "msg")).thenThrow(new Exception());

		try {
			helper.trace("msg");
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule trace_3 : trace ", e.getMessage());
			verify(adapter).trace("out", "msg");
		}
	}

	@Test
	public void traceln_1() throws Exception {
		// stubbing
		setupHelper();
		when(adapter.traceln("out", "msg")).thenReturn(true);

		boolean result = helper.traceln("msg");

		assertTrue(result);
		verify(adapter).traceln("out", "msg");
	}

	@Test
	public void traceln_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		when(adapter.traceln(identifier, "msg")).thenReturn(true);

		boolean result = helper.traceln(identifier, "msg");

		assertTrue(result);
		verify(adapter).traceln(identifier, "msg");
	}

	@Test
	public void traceln_3() throws Exception {
		// stubbing
		setupHelper();
		when(adapter.traceln("out", "msg")).thenThrow(new Exception());

		try {
			helper.traceln("msg");
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule traceln_3 : traceln ", e.getMessage());
			verify(adapter).traceln("out", "msg");
		}
	}

	/* killRemoteJVM */
	@Test
	public void prepareKillJVM_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		helper.prepareKillJVM(identifier);

		ArgumentCaptor<CallbackIF> callbackCaptor = ArgumentCaptor.forClass(CallbackIF.class);
		verify(adapter).registerCallback(eq(identifier), callbackCaptor.capture());

		CallbackIF callback = callbackCaptor.getValue();
		assertNotNull(callback);
	}

	@Test
	public void prepareKillJVM_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		doThrow(new Exception()).when(adapter).registerCallback(anyObject(), anyObject());

		try {
			helper.prepareKillJVM(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule prepareKillJVM_2 : prepareKillJVM ", e.getMessage());
		}
	}

	@Test
	public void killRemoteJVM_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		helper.killRemoteJVM(identifier);

		verify(adapter).killRemoteJVM(identifier, -1);
	}

	@Test
	public void killRemoteJVM_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		helper.killRemoteJVM(identifier, -9);

		verify(adapter).killRemoteJVM(identifier, -9);
	}

	@Test
	public void killRemoteJVM_3() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";

		doThrow(new Exception()).when(adapter).killRemoteJVM(identifier, -1);


		try {
			helper.killRemoteJVM(identifier);
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule killRemoteJVM_3 : killRemoteJVM ", e.getMessage());
			verify(adapter).killRemoteJVM(identifier, -1);
		}
	}

	/* callback */
	@Test
	public void registerCallback_1() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		Object target = new Dummy4Callback();

		helper.registerCallback(identifier, target, "toString");

		ArgumentCaptor<CallbackInvoker> captor = ArgumentCaptor.forClass(CallbackInvoker.class);
		verify(adapter).registerCallback(eq(identifier), captor.capture());
		CallbackInvoker callback = captor.getValue();
		assertEquals("test!!!", callback.invoke());
	}

	@Test
	public void registerCallback_2() throws Exception {
		// stubbing
		setupHelper();
		String identifier = "test";
		Object target = new Dummy4Callback();

		doThrow(new Exception()).when(adapter).registerCallback(anyObject(), anyObject());

		try {
			helper.registerCallback(identifier, target, "toString");
			fail();
		} catch (ExecuteException e) {
			assertEquals("rule registerCallback_2 : registerCallback ", e.getMessage());
		}
	}

	public static class Dummy4Callback {
		@Override
		public String toString() {
			return "test!!!";
		}
	}
}
