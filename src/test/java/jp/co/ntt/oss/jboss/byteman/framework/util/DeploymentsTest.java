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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DeploymentsTest {
	
	@Mock
	private ServerCommandManager commandManager;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void deploy_1() throws Exception {
		Deployments deployments = spy(new Deployments());
		deployments.commandManager = commandManager;
		when(deployments.getDeploymentDestination()).thenReturn(null);
		
		deployments.deploy();
		
		verify(commandManager, never()).upload(anyString(), anyString(), anyString());
	}

	@Test
	public void deploy_2() throws Exception {
		Deployments deployments = spy(new Deployments());
		deployments.commandManager = commandManager;
		when(deployments.getDeploymentDestination()).thenReturn("");
		
		deployments.deploy();
		
		verify(commandManager, never()).upload(anyString(), anyString(), anyString());
	}
	
	@Test
	public void deploy_3() throws Exception {
		Deployments deployments = new Deployments();
		deployments.commandManager = commandManager;
		
		deployments.deploy();
		
		verify(commandManager, never()).upload(anyString(), anyString(), anyString());
	}
	
	@Test
	public void deploy_4() throws Exception {
		File dir = new File("deployments");
		dir.mkdir();
		
		File dummy = new File(dir, "dummy.txt");
		dummy.createNewFile();
		
		try {
			Deployments deployments = new Deployments();
			deployments.commandManager = commandManager;
			
			deployments.deploy();
			
			verify(commandManager).upload("server1", dummy.getAbsolutePath(), "/opt");
			verify(commandManager).upload("server2", dummy.getAbsolutePath(), "/opt");
		} finally {
			dummy.delete();
			dir.delete();
		}
	}
}
