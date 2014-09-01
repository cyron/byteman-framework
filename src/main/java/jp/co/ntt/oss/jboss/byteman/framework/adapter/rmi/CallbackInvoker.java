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

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * The callback object which invokes the specified method.
 *
 */
public class CallbackInvoker extends UnicastRemoteObject implements CallbackIF {

	private static final long serialVersionUID = 1L;

	private Object target;

	private String methodName;

	/**
	 * Constructs with the target object and the method name to invoke.
	 *
	 * @param target the target object
	 * @param methodName the method name to invoke
	 * @throws RemoteException
	 */
	public CallbackInvoker(Object target, String methodName) throws RemoteException {
		super();
		this.target = target;
		this.methodName = methodName;
	}

	/**
	 * Invokes the method of the target object specified by the constructor.
	 * @param parameters parameters for the method
	 */
	@Override
	public Object invoke(Object... parameters) throws RemoteException {
		Method method = null;
		try {
			method = getMethod(parameters);
			return method.invoke(target, parameters);
		} catch (Exception e) {
			throw new RemoteException(String.format("Failed to invoke method [%s].", method), e);
		}
	}

	private Method getMethod(Object... parameters) throws Exception {
		List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
		for (Object param : parameters) {
			parameterTypes.add(param.getClass());
		}
		return target.getClass().getMethod(methodName, parameterTypes.toArray(new Class<?>[parameterTypes.size()]));
	}

}
