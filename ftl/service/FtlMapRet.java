package com.ftl.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FtlMapRet extends HashMap<String, Object> {
	private static final long serialVersionUID = 15435435L;

	public HashMap<String, Object> _embedded = new HashMap<>();
	public HashMap<String, Object> _page = new HashMap<>();

	public FtlMapRet() {
		put("_embedded", _embedded);
		put("page", _page);
	}

	private String nowRsKey = null;

	public FtlMapRet retAdd(Connection conn, FtlMapPa pa1) throws Exception {
		if (this.containsKey(pa1.resultNm))
			throw new Exception("result [" + pa1.resultNm + "] duplicated");

		List<Map<String, Object>> selLst = pa1.getResultList(conn);
		if (selLst == null) { // update,proc
			return this;
		}
		_embedded.put(pa1.resultNm, selLst);

		if (pa1.doTotalPages) {
			_page.put("number", 0);
			_page.put("page", pa1.get("page")); // from ui
			_page.put("size", pa1.get("size")); // from ui

			pa1.put("total_pages", "y");

			selLst = pa1.getResultList(conn);

			if (selLst.size() > 0) {
				_page.put("totalElements", selLst.get(0).get("totalElements"));
				_page.put("totalPages", selLst.get(0).get("totalPages"));
			}
			pa1.remove("total_pages");
		}
		return this;
	}

	public FtlMapRet copyToParam(FtlMapPa pa1) throws Exception {
		// _embedded 를 pa1에 복사
		// _embedded -> pa1
		for (String retsk : _embedded.keySet()) {
			if (pa1.get(retsk) == null)
				pa1.put(retsk, _embedded.get(retsk));
			else
				throw new Exception(nowRsKey + ": previous result has same name [" + retsk + "] ");
		}
		return this;
	}
}
