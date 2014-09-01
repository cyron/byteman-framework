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
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServerCommandManagerTest {
	
	private ServerCommandManager commandManager;

	@Mock
	private SSH ssh;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		commandManager = new ServerCommandManager();
		commandManager.ssh = ssh;
	}
	
	@Test
	public void testExecute_1() throws Exception {
		int result = commandManager.execute("server1", "ls", "ps");
		assertEquals(0, result);
		
		verify(ssh, times(1)).execute("127.0.1.1", "username", "password", "ls", "ps");
	}
	
	@Test
	public void testUpload_1() throws Exception {
		commandManager.upload("server1", "file", "/tmp");
		
		verify(ssh, times(1)).upload("127.0.1.1", "username", "password", "file", "/tmp");
	}
	
	@Test
	public void testDownload_1() throws Exception {
		commandManager.download("server1", "/var/log", "download");
		
		verify(ssh, times(1)).download("127.0.1.1", "username", "password", "/var/log", "download");
	}
	
	@Test
	public void testDelete_1() throws Exception {
		commandManager.delete("server1", "/var/log");
		
		verify(ssh, times(1)).execute("127.0.1.1", "username", "password", "rm -rf /var/log");
	}
}
