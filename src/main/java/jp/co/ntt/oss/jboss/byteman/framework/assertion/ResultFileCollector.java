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

package jp.co.ntt.oss.jboss.byteman.framework.assertion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.ntt.oss.jboss.byteman.framework.util.ResultRepository;
import jp.co.ntt.oss.jboss.byteman.framework.util.ServerCommandManager;

/**
 * The collector class for file.
 *
 * This class can be collected file (or directory)
 * by the {@link ServerCommandManager#download(String, String, String)}.
 */
public class ResultFileCollector extends AbstractResultCollector<String> {
	private String identifier;
	private String targetPath;

	private FileFilter filter;

	/** For executing a command. */
	protected ServerCommandManager commandManager = new ServerCommandManager();

	/**
	 * Constructs a new instance with the identifier.
	 *
	 * @param repo the {@link ResultRepository}
	 * @param identifier the identifier
	 * @param targetPath the source path
	 */
	public ResultFileCollector(ResultRepository repo,
			String identifier, String targetPath) {
		super(repo);
		this.identifier = identifier;
		this.targetPath = targetPath;
	}

	/**
	 * Sets the {@link FileFilter}.
	 * @param filter the {@link FileFilter}
	 */
	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}

	/**
	 * Retrieves the file (or directory).
	 */
	@Override
	public void collect() throws Exception {
		commandManager.download(identifier, targetPath, destPath(identifier));
	}

	/**
	 * Returns collected files by {@link #collect()} method.
	 *
	 * @return list of collected files
	 */
	@Override
	public List<Iterator<String>> getResult() {
		return getCollectedFiles(new File(destPath(identifier)));
	}

	/**
	 * Returns files which matches a filter setting with {@link #setFilter(FileFilter)}
	 * from specified directory.
	 *
	 * @param directory the retrieving directory
	 * @return collected files
	 */
	protected List<Iterator<String>> getCollectedFiles(File directory) {
		List<Iterator<String>> list = new ArrayList<Iterator<String>>();

		for(File file : directory.listFiles(filter)) {
			if(file.isDirectory()) {
				list.addAll(getCollectedFiles(file));
			} else {
				list.add(getFileIterator(file));
			}
		}
		return list;
	}

	/**
	 * Returns an iterator per file.
	 *
	 * @param file the target file
	 * @return an iterator over the target file
	 */
	protected Iterator<String> getFileIterator(final File file) {
		return new Iterator<String>() {
			private BufferedReader reader;
			private String line;

			@Override
			public boolean hasNext() {
				boolean isError = false;
				try {
					if(reader == null) {
						reader = new BufferedReader(
							new InputStreamReader(
								new FileInputStream(file)));
					}
					line = reader.readLine();

				} catch(IOException e) {
					isError = true;
					throw new RuntimeException(e);
				} finally {
					if((isError || line == null) && reader != null) {
						try {
							reader.close();
						} catch(IOException e) {}
					}
				}
				return line != null;
			}

			@Override
			public String next() {
				return line;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}