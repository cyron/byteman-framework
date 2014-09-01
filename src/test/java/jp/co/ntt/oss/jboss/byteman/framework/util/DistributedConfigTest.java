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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.Map;

import jp.co.ntt.oss.jboss.byteman.framework.util.DistributedConfig.DistributedNodeConfig;

import org.junit.Test;

public class DistributedConfigTest {

	@Test
	public void getConfig_1() {
		DistributedConfig config = DistributedConfig.getConfig();
		assertEquals("127.0.0.1", config.getRmiHost());
		assertEquals(1199, config.getRmiPort());
		assertEquals("data", config.getResultDir());
		Map<String, DistributedNodeConfig> nodeConfigs = config.getNodeConfigs();
		assertEquals(4, nodeConfigs.size());
		{
			DistributedNodeConfig nodeConfig = config.getNodeConfig("server1");
			assertSame(nodeConfigs.get("server1"), nodeConfig);
			assertEquals("127.0.1.1", nodeConfig.get(DistributedConfig.NODE_ADDRESS));
			assertEquals("9091", nodeConfig.get(DistributedConfig.NODE_BYTEMAN_PORT));
			assertEquals("/lib/byteman.jar", nodeConfig.get(DistributedConfig.NODE_BYTEMAN_JAR));
			assertEquals("/lib/byteman-framework.jar", nodeConfig.get(DistributedConfig.NODE_BYTEMANFW_JAR));
			assertEquals("it", nodeConfig.get("node.jboss.server"));
		}
		{
			DistributedNodeConfig nodeConfig = config.getNodeConfig("server2");
			assertSame(nodeConfigs.get("server2"), nodeConfig);
			assertEquals("127.0.2.1", nodeConfig.get(DistributedConfig.NODE_ADDRESS));
			assertEquals("9092", nodeConfig.get(DistributedConfig.NODE_BYTEMAN_PORT));
			assertNull(nodeConfig.get(DistributedConfig.NODE_BYTEMAN_JAR));
			assertEquals("/opt/byteman-framework.jar", nodeConfig.get(DistributedConfig.NODE_BYTEMANFW_JAR));
			assertEquals("it", nodeConfig.get("node.jboss.server"));
		}
		{
			DistributedNodeConfig nodeConfig = config.getNodeConfig("server3");
			assertSame(nodeConfigs.get("server3"), nodeConfig);
			assertEquals("127.0.3.1", nodeConfig.get(DistributedConfig.NODE_ADDRESS));
			assertEquals("9091", nodeConfig.get(DistributedConfig.NODE_BYTEMAN_PORT));
			assertEquals("/opt/byteman.jar", nodeConfig.get(DistributedConfig.NODE_BYTEMAN_JAR));
			assertNull(nodeConfig.get(DistributedConfig.NODE_BYTEMANFW_JAR));
			assertEquals("it.clustered", nodeConfig.get("node.jboss.server"));
		}

	}

	/**
	 * controller.rmi.address is not defined.
	 */
	@Test
	public void getConfig_2() {
		DistributedConfig config = DistributedConfig.getConfig();
		try {
			config.init("byteman-framework-err1.properties");
			fail();
		} catch (IllegalStateException e) {
			assertEquals("controller.rmi.address is not defined.", e.getMessage());
		} finally {
			config.init("byteman-framework.properties");
		}
	}

	/**
	 * node.address is not defined.
	 */
	@Test
	public void getConfig_3() {
		DistributedConfig config = DistributedConfig.getConfig();
		try {
			config.init("byteman-framework-err2.properties");
			fail();
		} catch (IllegalStateException e) {
			assertEquals("node.address.server2 is not defined.", e.getMessage());
		} finally {
			config.init("byteman-framework.properties");
		}
	}

	/**
	 * nodes is not defined.
	 */
	@Test
	public void getConfig_4() {
		DistributedConfig config = DistributedConfig.getConfig();
		try {
			config.init("byteman-framework-err3.properties");
			fail();
		} catch (IllegalStateException e) {
			assertEquals("The setup of the node is not defined at all.", e.getMessage());
		} finally {
			config.init("byteman-framework.properties");
		}
	}

	@Test
	public void getConfig_5() {
		DistributedConfig config = DistributedConfig.getConfig();
		try {
			config.init("dummy.properties");
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getCause() instanceof FileNotFoundException);
		}
	}
}
