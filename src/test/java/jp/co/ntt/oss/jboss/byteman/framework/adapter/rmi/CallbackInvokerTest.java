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

import java.rmi.RemoteException;

import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;

import org.junit.Test;

public class CallbackInvokerTest {

	@Test
	public void constructor() throws Exception {
		Object target = new Object();
		CallbackInvoker invoker = new CallbackInvoker(target, "toString");

		assertSame(target, TestUtil.getValue(invoker, "target"));
		assertEquals("toString", TestUtil.getValue(invoker, "methodName"));
	}

	/**
	 * invoke void method
	 */
	@Test
	public void invoke_1() throws RemoteException {
		Dummy4Callback target = new Dummy4Callback();
		CallbackInvoker invoker = new CallbackInvoker(target, "method1");
		Object result = invoker.invoke();

		assertNull(result);
		assertTrue(target.method1Flg);
	}

	/**
	 * invoke method with no parameter
	 */
	@Test
	public void invoke_2() throws RemoteException {
		Dummy4Callback target = new Dummy4Callback();
		CallbackInvoker invoker = new CallbackInvoker(target, "method2");

		Object result = invoker.invoke();

		assertEquals("test!!!", result);
	}

	/**
	 * invoke method with parameter
	 */
	@Test
	public void invoke_3() throws RemoteException {
		Dummy4Callback target = new Dummy4Callback();
		CallbackInvoker invoker = new CallbackInvoker(target, "method2");

		Object result = invoker.invoke("value");

		assertEquals("test value", result);
	}

	@Test
	public void invoke_4() throws RemoteException {
		Dummy4Callback target = new Dummy4Callback();
		CallbackInvoker invoker = new CallbackInvoker(target, "method3");

		try {
			invoker.invoke();
			fail();
		} catch (RemoteException e) {
			assertEquals("Failed to invoke method [public java.lang.String jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.CallbackInvokerTest$Dummy4Callback.method3()].; nested exception is: \n" +
						 "	java.lang.reflect.InvocationTargetException", e.getMessage());
		}
	}


	public static class Dummy4Callback {

		private boolean method1Flg = false;

		public void method1() {
			method1Flg = true;
		}

		public String method2(String value) {
			return "test " + value;
		}

		public String method2() {
			return "test!!!";
		}

		public String method3() {
			throw new IllegalStateException("error");
		}
	}
}
