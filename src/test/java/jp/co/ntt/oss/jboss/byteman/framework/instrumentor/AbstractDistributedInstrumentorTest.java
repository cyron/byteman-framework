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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.ntt.oss.jboss.byteman.framework.adapter.DistributedAdapter;
import jp.co.ntt.oss.jboss.byteman.framework.instrumentor.AbstractDistributedInstrumentor.SubmitWrapper;

import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class AbstractDistributedInstrumentorTest {

	private AbstractDistributedInstrumentor instrumentor = new AbstractDistributedInstrumentor() {

		@Override
		public DistributedAdapter getAdapter() {
			return null;
		}

	};

	@Test
	public void init_1() throws Exception {
		assertNull(instrumentor.submits);
		instrumentor.init();
		assertEquals(3, instrumentor.submits.size());
		{
			SubmitWrapper submit = instrumentor.submits.get("server1");
			assertEquals("127.0.1.1", submit.submit.getAddress());
			assertEquals(9091, submit.submit.getPort());
		}
		{
			SubmitWrapper submit = instrumentor.submits.get("server2");
			assertEquals("127.0.2.1", submit.submit.getAddress());
			assertEquals(9092, submit.submit.getPort());
		}
		{
			SubmitWrapper submit = instrumentor.submits.get("server3");
			assertEquals("127.0.3.1", submit.submit.getAddress());
			assertEquals(9091, submit.submit.getPort());
		}
	}

	@Test
	public void installScript_1() throws Exception {
		// stubbing
		instrumentor.submits = new HashMap<String, AbstractDistributedInstrumentor.SubmitWrapper>();
		SubmitWrapper submit = mock(SubmitWrapper.class);
		instrumentor.submits.put("server1", submit);

		instrumentor.installScript("server1", "script1", "scriptText1");

		ArgumentCaptor<ScriptText> captor = ArgumentCaptor.forClass(ScriptText.class);
		verify(submit).addScript(captor.capture());
		ScriptText script = captor.getValue();
		assertEquals("script1", script.getFileName());
		assertEquals("scriptText1", script.getText());
	}

	@Test
	public void installScript_2() throws Exception {
		// stubbing
		instrumentor.submits = new HashMap<String, AbstractDistributedInstrumentor.SubmitWrapper>();

		try {
			instrumentor.installScript("server1", "script1", "scriptText1");
		} catch (IllegalArgumentException e) {
			assertEquals("[server1] is not defined.", e.getMessage());
		}
	}
	
	@Test
	public void installScriptFromFilePath_1() throws Exception {
		// stubbing
		instrumentor.submits = new HashMap<String, AbstractDistributedInstrumentor.SubmitWrapper>();
		SubmitWrapper submit = mock(SubmitWrapper.class);
		instrumentor.submits.put("server1", submit);
		
		instrumentor.installScript("server1", "src/test/resources/jp/co/ntt/oss/jboss/byteman/framework/instrumentor/AbstractDistributedInstrumentorTest.btm");
		
		ArgumentCaptor<ScriptText> captor = ArgumentCaptor.forClass(ScriptText.class);
		verify(submit).addScript(captor.capture());
		ScriptText script = captor.getValue();
		assertEquals("src/test/resources/jp/co/ntt/oss/jboss/byteman/framework/instrumentor/AbstractDistributedInstrumentorTest.btm", script.getFileName());
		assertEquals("testscript\n", script.getText());
	}

	@Test
	public void installScriptFromStream_1() throws Exception {
		// stubbing
		instrumentor.submits = new HashMap<String, AbstractDistributedInstrumentor.SubmitWrapper>();
		SubmitWrapper submit = mock(SubmitWrapper.class);
		instrumentor.submits.put("server1", submit);
		
		instrumentor.installScript("server1", "AbstractDistributedInstrumentorTest.btm",
				Thread.currentThread().getContextClassLoader().getResourceAsStream(
						"jp/co/ntt/oss/jboss/byteman/framework/instrumentor/AbstractDistributedInstrumentorTest.btm"));
		
		ArgumentCaptor<ScriptText> captor = ArgumentCaptor.forClass(ScriptText.class);
		verify(submit).addScript(captor.capture());
		ScriptText script = captor.getValue();
		assertEquals("AbstractDistributedInstrumentorTest.btm", script.getFileName());
		assertEquals("testscript\n", script.getText());
	}
	
	@Test
	public void destroy_1() throws Exception {
		// stubbing
		instrumentor.submits = new HashMap<String, AbstractDistributedInstrumentor.SubmitWrapper>();
		SubmitWrapper submit1 = mock(SubmitWrapper.class);
		instrumentor.submits.put("server1", submit1);
		SubmitWrapper submit2 = mock(SubmitWrapper.class);
		instrumentor.submits.put("server2", submit2);

		instrumentor.destroy();

		assertEquals(0, instrumentor.submits.size());
		verify(submit1).removeScripts();
		verify(submit2).removeScripts();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void addScript_1() throws Exception {
		Submit submit = mock(Submit.class);
		SubmitWrapper submitWrapper = new SubmitWrapper(submit);

		ScriptText script = new ScriptText("name", "text");
		submitWrapper.addScript(script);

		assertEquals(1, submitWrapper.scripts.size());
		assertEquals(submitWrapper.scripts.get(0), script);

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(submit).addScripts(captor.capture());
		@SuppressWarnings("rawtypes")
		List scripts = captor.getValue();
		assertEquals(script, scripts.get(0));
	}

	@Test
	public void removeScripts_1() throws Exception {
		Submit submit = mock(Submit.class);
		SubmitWrapper submitWrapper = new SubmitWrapper(submit);

		List<ScriptText> scripts = new ArrayList<ScriptText>();
		scripts.add(new ScriptText("name1", "text1"));
		submitWrapper.scripts = scripts;

		submitWrapper.removeScripts();

		verify(submit).deleteScripts(scripts);
		assertEquals(0, scripts.size());
	}

	@Test
	public void removeScripts_2() throws Exception {
		Submit submit = mock(Submit.class);
		SubmitWrapper submitWrapper = new SubmitWrapper(submit);

		submitWrapper.removeScripts();

		verifyZeroInteractions(submit);
	}

	/**
	 * Exception occur when delete scripts
	 */
	@Test
	public void removeScripts_3() throws Exception {
		Submit submit = mock(Submit.class);
		SubmitWrapper submitWrapper = new SubmitWrapper(submit);

		List<ScriptText> scripts = new ArrayList<ScriptText>();
		scripts.add(new ScriptText("name1", "text1"));
		submitWrapper.scripts = scripts;
		doThrow(new IOException()).when(submit).deleteScripts(scripts);

		submitWrapper.removeScripts();

		verify(submit).deleteScripts(scripts);
		assertEquals(0, scripts.size());
	}
}
