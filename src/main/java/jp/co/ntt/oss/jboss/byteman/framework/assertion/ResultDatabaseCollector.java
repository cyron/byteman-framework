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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.co.ntt.oss.jboss.byteman.framework.util.ResultRepository;

/**
 * The collector class for database.
 *
 * This class can be selected rows by the specified SQL. These are stored in a
 * {@code ResultDatabaseCollector} object.
 * For example, if the following database record,
 * <pre>
 *  USER_ID | USER_NAME
 * ---------+-------------
 *        1 | Sample Name
 * </pre>
 * stored at following format.
 * <pre>
 *  // per row
 *  Map<String, Object> row = new HashMap<String, Object>();
 *  row.put("user_id", 1);
 *  row.put("user_name", "Sample Name");
 * </pre>
 *
 * Note that when {@code #collect()} method executed, retrieves all rows(i.e. retained in memory).
 */
public class ResultDatabaseCollector extends AbstractResultCollector<Map<String, Object>> {
	private String url;
	private String username;
	private String password;
	private String sql;

	private List<Iterator<Map<String, Object>>> rows = new ArrayList<Iterator<Map<String, Object>>>();

	/**
	 * Constructs a new instance with the specified database URL.
	 *
	 * @param repo the {@link ResultRepository}
	 * @param url the database URL
	 * @param username the database user
	 * @param password the user's password
	 * @param sql the retrieving SQL
	 */
	public ResultDatabaseCollector(ResultRepository repo,
			String url, String username, String password, String sql) {
		super(repo);
		this.url = url;
		this.username = username;
		this.password = password;
		this.sql = sql;
	}

	/**
	 * Executes the SQL query and retrieves row data.
	 */
	@Override
	public void collect() throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			ResultSetMetaData metadata = rs.getMetaData();
			final int cols = metadata.getColumnCount();

			while (rs.next()) {
				final Map<String, Object> row = new HashMap<String, Object>();
				for (int i = 1; i <= cols; i++) {
					row.put(metadata.getColumnName(i).toLowerCase(), rs.getObject(i));
				}
				rows.add(new Iterator<Map<String, Object>>() {
					private boolean hasNext = true;

					@Override
					public boolean hasNext() {
						return hasNext;
					}

					@Override
					public Map<String, Object> next() {
						hasNext = false;
						return row;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				});
			}
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch(SQLException e) {}
			}
			if(statement != null) {
				try {
					statement.close();
				} catch(SQLException e) {}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch(SQLException e) {}
			}
		}
	}

	/**
	 * Returns values retrieving by {@link #collect()} method.
	 *
	 * @return list of retrieved row data
	 */
	@Override
	public List<Iterator<Map<String, Object>>> getResult() {
		return rows;
	}

	/**
	 * Returns the {@link Connection}.
	 * @return the {@link Connection}
	 * @throws SQLException
	 */
	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

}