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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.co.ntt.oss.jboss.byteman.framework.util.DistributedConfig;
import jp.co.ntt.oss.jboss.byteman.framework.util.DistributedConfig.DistributedNodeConfig;

import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;

/**
 * The base class for {@link DistributedInstrumentor} implementations.
 *
 */
public abstract class AbstractDistributedInstrumentor implements DistributedInstrumentor {
	/** {@link Submit} map. */
	protected Map<String, SubmitWrapper> submits;

	/**
	 * Initializes the instrumentor so that a rule script can be installed to the remote node.
	 *
	 * @see Submit
	 */
	@Override
	public void init() throws Exception {
		submits = new HashMap<String, SubmitWrapper>();
		for(Entry<String, DistributedNodeConfig> entry : DistributedConfig.getConfig().getNodeConfigs().entrySet()) {
			if("*".equals(entry.getKey())) {
				continue;
			}
			DistributedNodeConfig config = entry.getValue();
			String address = config.get(DistributedConfig.NODE_ADDRESS);
			int port = Integer.parseInt(config.get(DistributedConfig.NODE_BYTEMAN_PORT));
			submits.put(entry.getKey(), new SubmitWrapper(new Submit(address, port)));
		}
	}

	/**
	 * Installs a rule with the specified script file to the remote node.
	 *
	 * @param identifier an identifier of remote node
	 * @param filePath a script file path
	 * @see #installScript(Object, String, InputStream)
	 */
	@Override
	public void installScript(Object identifier, String filePath) throws Exception {
		installScript(identifier, filePath, new FileInputStream(filePath));
	}

	/**
	 * Installs a rule with the specified script name and input stream to the remote node.
	 *
	 * @param identifier an identifier of remote node
	 * @param scriptName a script name
	 * @param resourceStream an input stream of a script file
	 * @see #installScript(Object, String, String)
	 */
	@Override
	public void installScript(Object identifier, String scriptName, InputStream resourceStream) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(resourceStream));

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		installScript(identifier, scriptName, sb.toString());
	}

	/**
	 * Installs a rule with the specified script name and script text to the remote node.
	 * The specified identifier corresponds to settings of node in the configuration file.
	 *
	 * @param identifier an identifier of remote node
	 * @param scriptName a script name
	 * @param scriptText a script text
	 * @see Submit#addScripts(List)
	 * @see DistributedConfig
	 */
	@Override
	public void installScript(Object identifier, String scriptName, String scriptText) throws Exception {
		SubmitWrapper submit = submits.get(identifier);
		if(submit == null) {
			throw new IllegalArgumentException(String.format("[%s] is not defined.", identifier));
		}
		submit.addScript(new ScriptText(scriptName, scriptText));
	}

	/**
	 * Destroys the function of installing a rule script and removes all the installed scripts.
	 *
	 */
	@Override
	public void destroy() throws Exception {
		for(SubmitWrapper submit : submits.values()) {
			submit.removeScripts();
		}
		submits.clear();
	}

	/**
	 * The wrapper class for {@link Submit}.
	 *
	 */
	protected static class SubmitWrapper {
		/** Submit. */
		protected Submit submit;
		/** Rule script. */
		protected List<ScriptText> scripts;
		/** Constructs with {@link Submit} */
		protected SubmitWrapper(Submit submit) {
			this.submit = submit;
			this.scripts = new ArrayList<ScriptText>();
		}

		/**
		 * Adds the rule script.
		 *
		 * @param script the target {@link ScriptText}
		 * @see Submit#addScripts(List)
		 * @throws Exception
		 */
		protected void addScript(ScriptText script) throws Exception {
			List<ScriptText> scripts = new ArrayList<ScriptText>();
			scripts.add(script);
			submit.addScripts(scripts);
			this.scripts.add(script);
		}

		/**
		 * Removes all the installed scripts.
		 *
		 * @see Submit#deleteScripts(List)
		 * @throws Exception
		 */
		protected void removeScripts() throws Exception {
			if(scripts.size() > 0) {
				try {
					submit.deleteScripts(scripts);
				} catch (Exception e) {
					// fail if the server is downed, so we ignore.
				}
			}
			scripts.clear();
		}
	}

}
