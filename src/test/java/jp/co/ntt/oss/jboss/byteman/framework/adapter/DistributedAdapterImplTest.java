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

package jp.co.ntt.oss.jboss.byteman.framework.adapter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.CallbackIF;

import org.jboss.byteman.rule.helper.Helper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DistributedAdapterImplTest {

	private DistributedAdapterImpl adapter;

	@Mock
	private Helper helper;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		adapter = spy(new DistributedAdapterImpl());
		adapter.helper = helper;
	}

	@Test
	public void waiting_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.waiting(identifier)).thenReturn(true);

		boolean result = adapter.waiting(identifier);

		assertTrue(result);
		verify(helper).waiting(identifier);
	}

	@Test
	public void waitFor_1() throws Exception {
		// stubbing
		String identifier = "test";

		adapter.waitFor(identifier, 1234L);

		verify(helper).waitFor(identifier, 1234L);
	}

	@Test
	public void signalWake_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.signalWake(identifier, true)).thenReturn(true);

		boolean result = adapter.signalWake(identifier, true);

		assertTrue(result);
		verify(helper).signalWake(identifier, true);
	}

	@Test
	public void signalThrow_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.signalThrow(identifier, true)).thenReturn(true);

		boolean result = adapter.signalThrow(identifier, true);

		assertTrue(result);
		verify(helper).signalThrow(identifier, true);
	}

	@Test
	public void createRendezvous_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.createRendezvous(identifier, 5, true)).thenReturn(true);

		boolean result = adapter.createRendezvous(identifier, 5, true);

		assertTrue(result);
		verify(helper).createRendezvous(identifier, 5, true);
	}

	@Test
	public void isRendezvous_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.isRendezvous(identifier, 5)).thenReturn(true);

		boolean result = adapter.isRendezvous(identifier, 5);

		assertTrue(result);
		verify(helper).isRendezvous(identifier, 5);
	}

	@Test
	public void getRendezvous_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.getRendezvous(identifier, 5)).thenReturn(3);

		int result = adapter.getRendezvous(identifier, 5);

		assertEquals(3, result);
		verify(helper).getRendezvous(identifier, 5);
	}

	@Test
	public void rendezvous_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.rendezvous(identifier)).thenReturn(3);

		int result = adapter.rendezvous(identifier);

		assertEquals(3, result);
		verify(helper).rendezvous(identifier);
	}

	@Test
	public void deleteRendezvous_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.deleteRendezvous(identifier, 5)).thenReturn(true);

		boolean result = adapter.deleteRendezvous(identifier, 5);

		assertTrue(result);
		verify(helper).deleteRendezvous(identifier, 5);
	}

	@Test
	public void createJoin_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.createJoin(identifier, 5)).thenReturn(true);

		boolean result = adapter.createJoin(identifier, 5);

		assertTrue(result);
		verify(helper).createJoin(identifier, 5);
	}

	@Test
	public void isJoin_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.isJoin(identifier, 5)).thenReturn(true);

		boolean result = adapter.isJoin(identifier, 5);

		assertTrue(result);
		verify(helper).isJoin(identifier, 5);
	}

	@Test
	public void joinEnlist_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.joinEnlist(identifier)).thenReturn(true);

		boolean result = adapter.joinEnlist(identifier);

		assertTrue(result);
		verify(helper).joinEnlist(identifier);
	}

	@Test
	public void joinWait_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.joinWait(identifier, 5)).thenReturn(true);

		boolean result = adapter.joinWait(identifier, 5);

		assertTrue(result);
		verify(helper).joinWait(identifier, 5);
	}

	@Test
	public void flag_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.flag(identifier)).thenReturn(true);

		boolean result = adapter.flag(identifier);

		assertTrue(result);
		verify(helper).flag(identifier);
	}

	@Test
	public void flagged_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.flagged(identifier)).thenReturn(true);

		boolean result = adapter.flagged(identifier);

		assertTrue(result);
		verify(helper).flagged(identifier);
	}

	@Test
	public void clear_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.clear(identifier)).thenReturn(true);

		boolean result = adapter.clear(identifier);

		assertTrue(result);
		verify(helper).clear(identifier);
	}

	@Test
	public void isCountDown_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.isCountDown(identifier)).thenReturn(true);

		boolean result = adapter.isCountDown(identifier);

		assertTrue(result);
		verify(helper).isCountDown(identifier);
	}

	@Test
	public void countDown_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.countDown(identifier)).thenReturn(true);

		boolean result = adapter.countDown(identifier);

		assertTrue(result);
		verify(helper).countDown(identifier);
	}

	@Test
	public void createCountDown_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.createCountDown(identifier, 5)).thenReturn(true);

		boolean result = adapter.createCountDown(identifier, 5);

		assertTrue(result);
		verify(helper).createCountDown(identifier, 5);
	}

	@Test
	public void createCounter_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.createCounter(identifier, 5)).thenReturn(true);

		boolean result = adapter.createCounter(identifier, 5);

		assertTrue(result);
		verify(helper).createCounter(identifier, 5);
	}

	@Test
	public void deleteCounter_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.deleteCounter(identifier)).thenReturn(true);

		boolean result = adapter.deleteCounter(identifier);

		assertTrue(result);
		verify(helper).deleteCounter(identifier);
	}

	@Test
	public void readCounter_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.readCounter(identifier, true)).thenReturn(5);

		int result = adapter.readCounter(identifier, true);

		assertEquals(5, result);
		verify(helper).readCounter(identifier, true);
	}

	@Test
	public void incrementCounter_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.incrementCounter(identifier, 1)).thenReturn(5);

		int result = adapter.incrementCounter(identifier, 1);

		assertEquals(5, result);
		verify(helper).incrementCounter(identifier, 1);
	}

	@Test
	public void decrementCounter_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.decrementCounter(identifier)).thenReturn(5);

		int result = adapter.decrementCounter(identifier);

		assertEquals(5, result);
		verify(helper).decrementCounter(identifier);
	}

	@Test
	public void createTimer_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.createTimer(identifier)).thenReturn(true);

		boolean result = adapter.createTimer(identifier);

		assertTrue(result);
		verify(helper).createTimer(identifier);
	}

	@Test
	public void deleteTimer_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.deleteTimer(identifier)).thenReturn(true);

		boolean result = adapter.deleteTimer(identifier);

		assertTrue(result);
		verify(helper).deleteTimer(identifier);
	}

	@Test
	public void getElapsedTimeFromTimer_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.getElapsedTimeFromTimer(identifier)).thenReturn(1234L);

		long result = adapter.getElapsedTimeFromTimer(identifier);

		assertEquals(1234L, result);
		verify(helper).getElapsedTimeFromTimer(identifier);
	}

	@Test
	public void resetTimer_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.resetTimer(identifier)).thenReturn(1234L);

		long result = adapter.resetTimer(identifier);

		assertEquals(1234L, result);
		verify(helper).resetTimer(identifier);
	}

	@Test
	public void traceOpen_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.traceOpen(identifier, "file")).thenReturn(true);

		boolean result = adapter.traceOpen(identifier, "file");

		assertTrue(result);
		verify(helper).traceOpen(identifier, "file");
	}

	@Test
	public void traceClose_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.traceClose(identifier)).thenReturn(true);

		boolean result = adapter.traceClose(identifier);

		assertTrue(result);
		verify(helper).traceClose(identifier);
	}

	@Test
	public void trace_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.trace(identifier, "msg")).thenReturn(true);

		boolean result = adapter.trace(identifier, "msg");

		assertTrue(result);
		verify(helper).trace(identifier, "msg");
	}

	@Test
	public void traceln_1() throws Exception {
		// stubbing
		String identifier = "test";
		when(helper.traceln(identifier, "msg")).thenReturn(true);

		boolean result = adapter.traceln(identifier, "msg");

		assertTrue(result);
		verify(helper).traceln(identifier, "msg");
	}

	/* kill jvm */
	@Test
	public void killRemoteJVM_1() throws Exception {
		// stubbing
		String identifier = "test";
		
		// register callback
		Map<Object, CallbackIF> map = new HashMap<Object, CallbackIF>();
		CallbackIF callback = mock(CallbackIF.class);
		map.put(identifier, callback);
		TestUtil.setValue(adapter, "callbacks", map);
		
		adapter.killRemoteJVM(identifier);
		
		verify(callback).invoke(-1);
	}

	@Test
	public void killRemoteJVM_2() throws Exception {
		// stubbing
		String identifier = "test";

		// register callback
		Map<Object, CallbackIF> map = new HashMap<Object, CallbackIF>();
		CallbackIF callback = mock(CallbackIF.class);
		map.put(identifier, callback);
		TestUtil.setValue(adapter, "callbacks", map);
		
		adapter.killRemoteJVM(identifier, -9);

		verify(callback).invoke(-9);
	}

	/* callback */
	@Test
	public void registerCallback_1() throws Exception {
		// stubbing
		String identifier = "test";

		Object target = new CallbackIF() {
			@Override
			public Object invoke(Object... parameters) throws RemoteException {
				return null;
			}
		};
		TestUtil.setValue(adapter, "callbacks", new ConcurrentHashMap<Object, CallbackIF>());
		
		adapter.registerCallback(identifier, target);

		@SuppressWarnings("unchecked")
		Map<Object, CallbackIF> result = (Map<Object, CallbackIF>) TestUtil.getValue(adapter, "callbacks");
		assertEquals(1, result.size());
		assertSame(target, result.get(identifier));
	}

	@Test
	public void unregisterCallback_1() throws Exception {
		// stubbing
		String identifier = "test";
		Map<Object, CallbackIF> map = new HashMap<Object, CallbackIF>();
		map.put(identifier, new CallbackIF() {
			@Override
			public Object invoke(Object... parameters) throws RemoteException {
				return null;
			}
		});
		TestUtil.setValue(adapter, "callbacks", map);

		adapter.unregisterCallback(identifier);

		@SuppressWarnings("unchecked")
		Map<Object, CallbackIF> result = (Map<Object, CallbackIF>) TestUtil.getValue(adapter, "callbacks");
		assertEquals(0, result.size());
	}

	@Test
	public void doCallback_1() throws Exception {
		// stubbing
		String identifier = "test";
		Map<Object, CallbackIF> map = new HashMap<Object, CallbackIF>();
		map.put("dummy", new CallbackIF() {
			@Override
			public Object invoke(Object... parameters) throws RemoteException {
				return "dummy!!!";
			}
		});
		map.put(identifier, new CallbackIF() {
			@Override
			public Object invoke(Object... parameters) throws RemoteException {
				return "test!!!" + parameters[0] + parameters[1];
			}
		});
		TestUtil.setValue(adapter, "callbacks", map);

		Object result = adapter.doCallback(identifier, "param1", "param2");
		assertEquals("test!!!param1param2", result);
	}

	@Test
	public void doCallback_2() throws Exception {
		// stubbing
		String identifier = "test2";

		try {
			adapter.doCallback(identifier, "param1", "param2");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("The callback object of the identifier [test2] is not registered.", e.getMessage());
		}
	}

}
