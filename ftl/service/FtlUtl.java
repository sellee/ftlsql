package com.ftl.service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FtlUtl {
	static public boolean isNull(String aa) {
		if (aa == null || aa.trim().length() == 0) {
			return true;
		}
		return false;
	}

	static public String toCamelCase(String _s) {
		return camelCase(_s);
	}

	static public String camelCase(String _s) {
		String s = _s.toLowerCase();
		StringBuffer sb = new StringBuffer();
		char c = ' ';
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (c == '_') {
				while (true) {
					i++;
					if (i >= s.length())
						break;
					c = s.charAt(i);
					if (c != '_') {
						sb.append((c + "").toUpperCase());
						break;
					}
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	static public String toSnakeCase(String str) {
		return snakeCase(str);
	}

	static public String snakeCase(String str) {
		String result = "";

		char c = str.charAt(0);
		result = result + Character.toLowerCase(c);

		for (int i = 1; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (Character.isUpperCase(ch)) {
				result = result + '_';
				result = result + Character.toLowerCase(ch);
			} else {
				result = result + ch;
			}
		}

		return result;
	}

	static public String snakeUpperCase(String str) {
		return snakeCase(str).toUpperCase();
	}

	/*
	 * 쿼리에 pa1.k1 , pa1[0].k1 같은것을 찾음
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Object getMapVal(Map<String, Object> p1, String oriKey) throws Exception {
		int ixTen = oriKey.indexOf('.');
		if (ixTen == 0)
			throw new Exception(oriKey + " : ex) lst1.pa1 ");
		if (ixTen > 0) {
			String fld = oriKey.substring(ixTen + 1);
			String kk = oriKey.substring(0, ixTen);
			int ixSt = kk.indexOf('[');
			int ixEd = kk.indexOf(']');
			if (ixSt != -1 || ixEd != -1) {
				if (ixSt == -1 || ixEd == -1) {
					throw new Exception(oriKey + " : [] not match.");
				}
				String k2 = kk.substring(0, ixSt);
				Object o = p1.get(k2);
				if (o == null)
					return null;
				if (o instanceof List) {
					int no = 0;
					try {
						no = Integer.parseInt(kk.substring(ixSt + 1, ixEd));
					} catch (Exception e) {
						throw new Exception(oriKey + " is not INT.");
					}
					if (no >= ((List) o).size())
						return null;
					Map m = (Map) ((List) o).get(no);
					return mapVal((Map) m, fld);
				} else {
					throw new Exception(oriKey + " is not List. [" + (o.getClass()) + "] passed.");
				}
			} else {
				Object o = p1.get(kk);
				if (o == null)
					return null;
				if (o instanceof Map) {
					return mapVal((Map) o, fld);
				} else {
					throw new Exception(oriKey + " is not Map. [" + (o.getClass()) + "] passed.");
				}
			}
		}

		return mapVal(p1, oriKey);
	}

	private static Object mapVal(Map<String, Object> map1, String key) {
		Object ret = map1.get(key);
		if (ret == null)
			return null;
		return ret;
	}

	protected static List<Map<String, Object>> getRecordsAll(ResultSet rs, boolean doCamel) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (rs == null)
			return list;
		try {
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();
			while (rs.next()) {
				HashMap<String, Object> paMap1 = new HashMap<String, Object>();
				for (int i = 1; i <= columns; ++i) {
					int type = md.getColumnType(i);
					String okey = md.getColumnName(i);
					String key = doCamel ? toCamelCase(okey) : okey;
					if (type == java.sql.Types.DOUBLE || type == java.sql.Types.FLOAT || type == java.sql.Types.REAL) {
						paMap1.put(key, rs.getDouble(i));
						//
					} else if (type == java.sql.Types.INTEGER || type == java.sql.Types.TINYINT || type == java.sql.Types.SMALLINT) {//
						paMap1.put(key, rs.getInt(i));
						//
					} else if (type == java.sql.Types.BIGINT //
							|| type == java.sql.Types.NUMERIC || type == java.sql.Types.DECIMAL) {
						paMap1.put(key, rs.getLong(i));
						//
					} else if (type == java.sql.Types.TIMESTAMP //
							|| type == java.sql.Types.DATE //
							|| type == java.sql.Types.TIME) {
						paMap1.put(key, rs.getTimestamp(i));
						//
					} else {
						paMap1.put(key, rs.getString(i));
					}
				}
				list.add(paMap1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			rs.close();
		}
		return list;
	}

	/*
	public FtlMapPa parse(String paStr) {
		if (paStr == null)
			return;
		paStr = paStr.trim();
		if (paStr.length() == 0)
			return;
		String[] par = paStr.split(",");
		for (String p1 : par) {
			String[] p1ar = p1.split(":");
			if (p1ar.length == 0)
				continue;
			else if (p1ar.length == 1)
				put(p1ar[0], "");
			else
				put(p1ar[0], p1ar[0].trim());
		}
	}
	*/

}
