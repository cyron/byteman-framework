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

package jp.co.ntt.oss.jboss.byteman.framework.instrumentor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.ControlIF;

import org.junit.Test;

public class DistributedInstrumentorImplTest {

	private DistributedInstrumentorImpl instrumentor;

	@Test
	public void init_1() throws Exception {
		instrumentor = new DistributedInstrumentorImpl();

		assertNull(instrumentor.getAdapter());
		instrumentor.init();
		assertNotNull(instrumentor.getAdapter());

		Registry registry = LocateRegistry.getRegistry(1199);
		registry.lookup(ControlIF.CONTROL_ID);

		instrumentor.destroy();
		try {
			registry.lookup(ControlIF.CONTROL_ID);
			fail();
		} catch (Exception e) {
		}
	}

}
