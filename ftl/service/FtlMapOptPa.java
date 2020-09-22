package com.ftl.service;

import java.util.HashMap;

public class FtlMapOptPa extends HashMap<String, Object> {
	private static final long serialVersionUID = 19875443L;

	public String qryRoot = null;
	public boolean doCamelCase = true;

	public FtlMapOptPa camelOff() {
		doCamelCase = false;
		return this;
	}

	public FtlMapOptPa qryRoot(String qrt) {
		qryRoot = qrt;
		return this;
	}

	public FtlMapOptPa put(String key, Object val) {
		super.put(key, val);
		return this;
	}
}