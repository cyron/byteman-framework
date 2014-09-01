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

package jp.co.ntt.oss.jboss.byteman.framework.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import jp.co.ntt.oss.jboss.byteman.framework.TestUtil;
import jp.co.ntt.oss.jboss.byteman.framework.util.DistributedConfig.DistributedNodeConfig;
import jp.co.ntt.oss.jboss.byteman.framework.util.Logger;
import jp.co.ntt.oss.jboss.byteman.framework.util.ServerCommandManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractNodeControllerTest {

	@Mock
	private ServerCommandManager commandManager;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void constructor_1() throws Exception {
		TestNodeController controller = new TestNodeController("server1");
		assertNotNull(TestUtil.getValue(controller, "config"));
		assertNotNull(TestUtil.getValue(controller, "commandManager"));
	}

	@Test
	public void constructor_2() {
		try {
			new TestNodeController("dummy");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("dummy is not defined.", e.getMessage());
		}
	}

	@Test
	public void executeWithSSH_1() throws Exception {
		TestNodeController controller = new TestNodeController("server1");
		TestUtil.setValue(controller, "commandManager", commandManager);

		controller.executeWithSSH("ls -l");

		verify(commandManager).execute("server1", "ls -l");
	}

	@Test
	public void getNodeConfig() throws Exception {
		TestNodeController controller = new TestNodeController("server1");
		DistributedNodeConfig config = mock(DistributedNodeConfig.class);
		when(config.get("key")).thenReturn("value");
		TestUtil.setValue(controller, "config", config);

		String result = controller.getNodeConfig("dummy");
		assertNull(result);
		verify(config).get("dummy");

		result = controller.getNodeConfig("key");
		assertEquals("value", result);
		verify(config).get("key");
	}

	/**
	 * no script.
	 */
	@Test
	public void getBytemanAgentProperties_1() throws Exception {
		TestNodeController controller = new TestNodeController("server1");
		String agentProperties = controller.getBytemanAgentProperties();

		assertEquals("-javaagent:/lib/byteman.jar=sys:/lib/byteman-framework.jar," +
												 "address:127.0.1.1," +
												 "port:9091," +
												 "prop:org.jboss.byteman.jp.co.ntt.oss.jboss.byteman.framework.host=127.0.0.1," +
												 "prop:org.jboss.byteman.jp.co.ntt.oss.jboss.byteman.framework.port=1199", agentProperties);
	}

	/**
	 * 1 script
	 */
	@Test
	public void getBytemanAgentProperties_2() throws Exception {
		TestNodeController controller = new TestNodeController("server1");
		String agentProperties = controller.getBytemanAgentProperties("/test1.btm");

		assertEquals("-javaagent:/lib/byteman.jar=sys:/lib/byteman-framework.jar," +
												 "address:127.0.1.1," +
												 "port:9091," +
												 "prop:org.jboss.byteman.jp.co.ntt.oss.jboss.byteman.framework.host=127.0.0.1," +
												 "prop:org.jboss.byteman.jp.co.ntt.oss.jboss.byteman.framework.port=1199," +
												 "script:/test1.btm", agentProperties);
	}

	/**
	 * 2 scripts.
	 */
	@Test
	public void getBytemanAgentProperties_3() throws Exception {
		TestNodeController controller = new TestNodeController("server1");
		String agentProperties = controller.getBytemanAgentProperties("/test1.btm", "/test2.btm");

		assertEquals("-javaagent:/lib/byteman.jar=sys:/lib/byteman-framework.jar," +
												 "address:127.0.1.1," +
												 "port:9091," +
												 "prop:org.jboss.byteman.jp.co.ntt.oss.jboss.byteman.framework.host=127.0.0.1," +
												 "prop:org.jboss.byteman.jp.co.ntt.oss.jboss.byteman.framework.port=1199," +
												 "script:/test1.btm," +
												 "script:/test2.btm", agentProperties);
	}

	/**
	 * node.byteman.jar is not defined.
	 */
	@Test
	public void getBytemanAgentProperties_4() throws Exception {
		TestNodeController controller = new TestNodeController("server2");
		try {
			controller.getBytemanAgentProperties();
			fail();
		} catch (IllegalStateException e) {
			assertEquals("node.byteman.jar is not defined.", e.getMessage());
		}
	}

	/**
	 * node.bytemanframework.jar is not defined.
	 */
	@Test
	public void getBytemanAgentProperties_5() throws Exception {
		TestNodeController controller = new TestNodeController("server3");
		try {
			controller.getBytemanAgentProperties();
			fail();
		} catch (IllegalStateException e) {
			assertEquals("node.bytemanframework.jar is not defined.", e.getMessage());
		}
	}

	/**
	 * get default value.
	 */
	@Test
	public void getNodeConfig_with_default() throws Exception {
		TestNodeController controller = new TestNodeController("server1");
		DistributedNodeConfig config = mock(DistributedNodeConfig.class);
		when(config.get("key")).thenReturn("value");
		TestUtil.setValue(controller, "config", config);

		String result = controller.getNodeConfig("dummy", "default");
		assertEquals("default", result);
		verify(config).get("dummy");

		result = controller.getNodeConfig("key", "default");
		assertEquals("value", result);
		verify(config,times(2)).get("key");
	}

	/**
	 * get identifier.
	 */
	@Test
	public void getIdenifier() throws Exception {
		TestNodeController controller = new TestNodeController("server3");
		assertEquals("server3", controller.getIdentifier());
	}

	/**
	 * set options.
	 */
	@Test
	public void setOptions() throws Exception {
		TestNodeController controller = new TestNodeController("server1");
		controller.setOptions("-Dkey=value");
		assertEquals("-Dkey=value", TestUtil.getValue(controller, "options"));
	}

	/**
	 * get options.
	 */
	@Test
	public void getOptions_1() {
		TestNodeController controller = new TestNodeController("server1");
		assertTrue(controller.getOptions().isEmpty());
	}

	@Test
	public void getOptions_2() {
		TestNodeController controller = new TestNodeController("server1");
		controller.setOptions("-Dkey=value");
		assertEquals("-Dkey=value", controller.getOptions());
	}

	@Test
	public void getOptions_3() {
		System.setProperty(Logger.SYSTEM_PROPERTY, "true");
		try {
			TestNodeController controller = new TestNodeController("server1");
			controller.setOptions("-Dkey=value");
			assertEquals(String.format("-Dkey=value -D%s=true", Logger.SYSTEM_PROPERTY), controller.getOptions());
		} finally {
			System.clearProperty(Logger.SYSTEM_PROPERTY);
		}
	}

	private static class TestNodeController extends AbstractNodeController {

		public TestNodeController(String identifier) {
			super(identifier);
		}

		@Override
		public void start() throws Exception {

		}

		@Override
		public void stop() throws Exception {

		}
	}
}
