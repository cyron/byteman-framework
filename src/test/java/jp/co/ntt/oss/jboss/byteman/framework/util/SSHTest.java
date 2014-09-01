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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SSHTest {

	private SSH ssh = new SSH(){
		@Override
		public SSHClient newSSHClient(){
			return client;
		}
	};

	@Mock
	private SSHClient client;
	
	@Mock
	private Session session;
	
	@Mock
	private Command command;
	
	@Mock
	private SCPFileTransfer scpTransfer;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testExecute_1() throws Exception {
		when(client.startSession()).thenReturn(session);
		when(session.exec(anyString())).thenReturn(command);
		
		int result = ssh.execute("127.0.1.1", "username", "password", "ls", "ps");
		assertEquals(0, result);
		
		verify(client, times(1)).connect("127.0.1.1");
		verify(client, times(1)).authPassword("username", "password".toCharArray());
		verify(session, times(1)).exec("ls\nps\n");
		verify(command, times(1)).join();
		
		verify(session, times(1)).close();
		verify(client, times(1)).disconnect();
	}

	@Test
	public void testExecute_2() throws Exception {
		when(client.startSession()).thenReturn(session);
		when(session.exec(anyString())).thenThrow(new ConnectionException("error!"));
		
		try {
			ssh.execute("127.0.1.1", "username", "password", "ls", "ps");
			fail();
		} catch(ConnectionException ex) {
			;
		}
		
		verify(client, times(1)).connect("127.0.1.1");
		verify(client, times(1)).authPassword("username", "password".toCharArray());
		verify(session, times(1)).exec("ls\nps\n");
		verify(command, never()).join();
		
		verify(session, times(1)).close();
		verify(client, times(1)).disconnect();
	}
	
	@Test
	public void testUpload_1() throws Exception {
		when(client.newSCPFileTransfer()).thenReturn(scpTransfer);
		
		ssh.upload("127.0.1.1", "username", "password", "file", "/tmp");
		
		verify(client, times(1)).connect("127.0.1.1");
		verify(client, times(1)).authPassword("username", "password".toCharArray());
		verify(client, times(1)).newSCPFileTransfer();
		verify(scpTransfer, times(1)).upload(new FileSystemFile(new File("file")), "/tmp");
		
		verify(client, times(1)).disconnect();
	}
	
	@Test
	public void testDownload_1() throws Exception {
		when(client.newSCPFileTransfer()).thenReturn(scpTransfer);
		
		ssh.download("127.0.1.1", "username", "password", "/var/log", "download");
		
		verify(client, times(1)).connect("127.0.1.1");
		verify(client, times(1)).authPassword("username", "password".toCharArray());
		verify(client, times(1)).newSCPFileTransfer();
		verify(scpTransfer, times(1)).download("/var/log", new FileSystemFile(new File("download")));
		
		verify(client, times(1)).disconnect();
	}

}
