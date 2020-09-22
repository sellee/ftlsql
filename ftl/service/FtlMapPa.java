package com.ftl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FtlMapPa extends HashMap<String, Object> {
	public FtlMapPa() {
	}

	public FtlMapPa(FtlMapPa pa) {
		super((HashMap<String, Object>) pa);
		this.opt = pa.opt;
		this.qIsCallable = pa.qIsCallable;
		this.qIsSel = pa.qIsSel;
		this.qIsUp = pa.qIsUp;
		this.qryNm = pa.qryNm;
		this.resultNm = pa.resultNm;
	}

	/**
	 * <pre>*: FtlMapOptPa.qryRoot 로 치환됨</pre>
	 */
	public String qryNm = null;

	/**
	 * <pre>select 결과 name</pre>
	 */
	public String resultNm = null;

	/**
	 * <pre>select TotalPages 실행여부 </pre>
	 */
	public boolean doTotalPages = false;

	/**
	 * <pre>update,merge,callable data list</pre>
	 */
	public List<Object> dataLst;

	private FtlMapOptPa opt;

	private boolean qIsSel = false;
	private boolean qIsUp = false;
	private boolean qIsMerge = false;
	private boolean qIsCallable = false;

	/**
	 * <pre>FtlMapOptPa 에 가진키를 복사</pre>
	 */
	public FtlMapPa validate(FtlMapOptPa _opt) throws Exception {
		opt = _opt;

		if (FtlUtl.isNull(qryNm)) {
			throw new Exception("qryNm not set");
		}
		if (qIsSel == false && qIsUp == false && qIsMerge == false && qIsCallable == false) {
			throw new Exception("do select,update,merge,exec");
		}
		if (qIsSel && FtlUtl.isNull(resultNm)) {
			throw new Exception("select resultNm not set");
		}

		// q0 를 pa1에 없는것 복사
		for (String q0k : opt.keySet()) {
			if (get(q0k) == null)
				put(q0k, opt.get(q0k));
			else
				throw new Exception("param  duplicated key[" + q0k + "] ");
		}

		if (qryNm.charAt(0) == '*')
			qryNm = opt.qryRoot + qryNm.substring(1);
		return this;
	}

	private void qtypeOn(char sumc) {
		qIsSel = sumc == 's' ? true : false;
		qIsUp = sumc == 'u' ? true : false;
		qIsMerge = sumc == 'm' ? true : false;
		qIsCallable = sumc == 'c' ? true : false;
	}

	public FtlMapPa select(String _qryNm) throws Exception {
		qtypeOn('s');
		return setQryNm(_qryNm);
	}

	public FtlMapPa update(String _qryNm) throws Exception {
		qtypeOn('u');
		return setQryNm(_qryNm);
	}

	public FtlMapPa merge() throws Exception {
		qtypeOn('m');
		return setQryNm("qms/cmm-merge.sql");
	}

	public FtlMapPa callable(String _qryNm) throws Exception {
		qtypeOn('c');
		return setQryNm(_qryNm);
	}

	private FtlMapPa setQryNm(String _qryNm) throws Exception {
		qryNm = _qryNm;
		if (FtlUtl.isNull(qryNm))
			throw new Exception("qryNm not set");
		if (!qryNm.endsWith(".sql"))
			qryNm += ".sql";
		return this;
	}

	public FtlMapPa pageOn() throws Exception {
		if (this.get("page") == null || this.get("size") == null)
			throw new Exception("pageOn required: page,size");
		if (qIsSel == false)
			throw new Exception("pageOn is select option ");
		doTotalPages = true;
		return this;
	}

	public FtlMapPa resultNm(String _resultNm) throws Exception {
		if (qIsSel == false)
			throw new Exception("resultNm is select option ");
		resultNm = _resultNm;
		return this;
	}

	private List<Object> lst = null;

	@SuppressWarnings("unchecked")
	public FtlMapPa dataLst(Object _lst) throws Exception {
		if (_lst == null || !(_lst instanceof List))
			throw new Exception("dataLst param must be not null, List<Object> ");
		if (qIsSel)
			throw new Exception("resultNm is update,merge,callable option ");

		lst = (List<Object>) _lst;
		dataLst = lst;
		if (lst.size() == 0)
			return this;

		if (qIsMerge) { // merge 안씀
			Object vo = lst.get(0);
			//if (vo == null || !(vo instanceof QmsCmmVO))
			//throw new Exception("param  vo must be type of [QmsCmmVO] ");
			//QmsCmmVO cmmvo = (QmsCmmVO) vo;
			//this.put("table_name", cmmvo.getTableName());
			//this.put("col_list", cmmvo.getColLst());
			//this.put("col_list_cmm", cmmvo.getColLstCmm());
			return this;
		}

		return this;
	}

	private Statement stmtNor = null;
	private PreparedStatement stmtUpSel = null;
	//private CallableStatement stmtPrc = null;

	public List<Map<String, Object>> getResultList(Connection conn) throws Exception {
		try {
			FtlQryMaker ftl = new FtlQryMaker(this);
			List<String> qmkLst = ftl.getQuestionMarkSql();
			String qmSql = qmkLst.get(0);
			String dbgQr = qmSql+"\n-------------------------" ;
			for (int i = 1; i < qmkLst.size(); i++) {
				Object vv = FtlUtl.getMapVal(this, qmkLst.get(i));
				dbgQr += "\n" + qmkLst.get(i) + " [" + vv + "]";
			}
			log.info("\n--------------------------\n QRY[" + qryNm + "]\n--------------------------\n" + dbgQr);

			if (qIsCallable) {
				stmtNor = conn.prepareCall(qmSql);
				//stmtPrc = (CallableStatement) stmtNor;
			} else {
				stmtNor = conn.prepareStatement(qmSql);
				stmtUpSel = (PreparedStatement) stmtNor;
				if (qIsSel) {
					for (int i = 1; i < qmkLst.size(); i++) {
						Object vv = FtlUtl.getMapVal(this, qmkLst.get(i));
						setStmtVal(i, vv);
					}
					ResultSet cur1 = stmtUpSel.executeQuery();
					List<Map<String, Object>> selList = FtlUtl.getRecordsAll(cur1, opt.doCamelCase);
					return selList;
				} else { // qIsUp
					for (int lstn = 0; lstn < lst.size(); lstn++) {
						Map<String, Object> vommm1 = voToMap(lst.get(lstn)); // vo -> map
						for (int i = 1; i < qmkLst.size(); i++) {
							Object vv = FtlUtl.getMapVal((vommm1), qmkLst.get(i));
							setStmtVal(i, vv);
						}
						stmtUpSel.executeUpdate();
					}
				}
			}

			return null;
		} finally {
			if (stmtNor != null)
				stmtNor.close();
		}
	}

	private Map<String, Object> voToMap(Object vo) throws Exception {
		Map<String, String> map = BeanUtils.describe(vo);
		Map<String, Object> newMap = new HashMap<>();
		for (String q0k : map.keySet()) {
			newMap.put(q0k, map.get(q0k));
			newMap.put(FtlUtl.snakeCase(q0k), map.get(q0k));
		}
		return newMap;
	}

	private void setStmtVal(int i, Object vv) throws SQLException {
		if (vv == null)
			stmtUpSel.setNull(i, java.sql.Types.NULL);
		else if (vv instanceof Integer)
			stmtUpSel.setInt(i, (Integer) vv);
		else if (vv instanceof Long)
			stmtUpSel.setLong(i, (Long) vv);
		else if (vv instanceof Float)
			stmtUpSel.setFloat(i, (Float) vv);
		else if (vv instanceof Double)
			stmtUpSel.setDouble(i, (Double) vv);
		else
			stmtUpSel.setString(i, (String) vv);
	}

	private static final long serialVersionUID = 198754L;

	public FtlMapPa put(String key, Object val) {
		super.put(key, val);
		return this;
	}
}
