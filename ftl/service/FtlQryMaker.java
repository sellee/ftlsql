package com.ftl.service;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FtlQryMaker {
	private static freemarker.template.Configuration cfg = null;

	@Autowired
	private Environment springEnv;

	public static String qryDir = null;

	private FtlMapPa paMap = null;

	public FtlQryMaker(FtlMapPa _paMap) {
		paMap = _paMap;
	}

	public String getQry() throws Exception {
		if (cfg == null) {
			/*
			qryDir = "D:/ecwork/qms-query/ftl_sql"; // bean 에서 등록, 값설정 할것
			 */

			cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_28);

			cfg.setDirectoryForTemplateLoading(new File(qryDir));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setLocale(Locale.KOREA);
			cfg.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);// RETHROW_HANDLER);
			cfg.setInterpolationSyntax(freemarker.template.Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
			cfg.setTagSyntax(freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX);

			List<String> macLst = new ArrayList<String>();
			macLst.add("zbase/autoInc.sql");
			cfg.setAutoIncludes(macLst);
		}

		Writer sw = new StringWriter();
		String qnm = paMap.qryNm;
		try {
			freemarker.template.Template template = cfg.getTemplate(qnm);
			template.process(paMap, sw);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return sw.toString();
	}

	/*
	 * <pre> #{pa}를 ? 로 바꾸게 in pa1:1 ,pa2:xx out "po1:cursor,po2:str" </pre>
	 */
	private static java.util.regex.Pattern ptnPa = java.util.regex.Pattern.compile("#\\{[a-zA-Z\\[\\]0-9_\\.\\s]*\\}");
	// https://blog.ostermiller.org/finding-comments-in-source-code-using-regular-expressions/
	// 제외 param
	private static java.util.regex.Pattern ptnC1 = java.util.regex.Pattern.compile("(--.*)|/\\*(?:.|[\\n])*?\\*/");

	private static String repl1st(String o, String s, String r) {
		int i1 = o.indexOf(s);
		if (i1 == -1)
			return o;

		int i2 = i1 + s.length();

		return o.substring(0, i1) + r + o.substring(i2);
	}

	/*
	 * <pre> return: 0에 qry 1..n 에 ?(parameter) </pre>
	 */
	public List<String> getQuestionMarkSql() throws Exception {
		List<String> arPa = new ArrayList<String>();
		List<String> arCmt = new ArrayList<String>();
		String qry = getQry();

		java.util.regex.Matcher matbc = ptnC1.matcher(qry);
		while (true) {
			if (matbc.find()) {
				String s = matbc.group();
				qry = repl1st(qry, s, "\007");
				arCmt.add(s);
			} else {
				break;
			}
		}

		java.util.regex.Matcher matQ = ptnPa.matcher(qry);
		while (true) {
			if (matQ.find()) {
				String s = matQ.group();
				arPa.add(s.trim());
			} else {
				break;
			}
		}

		for (int i = 0; i < arPa.size(); i++) {
			qry = repl1st(qry, arPa.get(i), "?");
		}
		for (int i = 0; i < arCmt.size(); i++) {
			qry = repl1st(qry, "\007", arCmt.get(i));
		}

		List<String> qmkList = new ArrayList<String>();
		qmkList.add(qry);
		for (int i = 0; i < arPa.size(); i++) {
			qmkList.add(arPa.get(i).replace("#{", "").replace("}", "").trim());
		}

		return qmkList;
	}

	public static void main(String[] args) throws Exception {
		// String qry ="11223344\r\nabc";
		// qry = repl1st(qry, "4\r\na", "\007");

		qryDir = "D:/ecwork/mes-qms-query/ftl_sql";
		FtlQryMaker ttu = new FtlQryMaker(null);
		ttu.test();
	}

	private void test() throws Exception {
		//			FtlMapPa pa1= new FtlMapPa().select("qms/specTest1").resultNm("specTest1").put("pa1", "fadfaf");
		//			FtlQryMaker ftl_1 = new FtlQryMaker(pa1);
		//			List<String> plsqlParams_1 = ftl_1.getQuestionMarkSql();
		//
		//			for (int i = 0; i < plsqlParams_1.size(); i++) {
		//				System.err.println(plsqlParams_1.get(i));
		//			}

		ArrayList<FtlMapPa> ar = new ArrayList<>();
		ar.add(new FtlMapPa().put("cd", "val1"));
		ar.add(new FtlMapPa().put("cd", "val2"));

		FtlMapPa pa2 = new FtlMapPa().select("PMS/TN_PMS_PROC_SGMT/TN_PMS_PROC_SGMT-IDNM").resultNm("tnPmsProcSgmt");
		pa2.put("useStatCd", "Usable");

		ArrayList lst = new ArrayList();
		lst.add(1);
		lst.add(2);
		pa2.put("lst", lst);
		pa2.put("lst_map", ar);
		//pa2.put("specTest1", ar);

		FtlQryMaker ftl_2 = new FtlQryMaker(pa2);
		System.err.println(ftl_2.getQry());

	}// main

}
