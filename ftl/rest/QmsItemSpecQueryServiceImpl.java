package com.ftl.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ftl.service.FtlMapOptPa;
import com.ftl.service.FtlMapPa;
import com.ftl.service.FtlMapRet;
import com.ftl.service.FtlQueryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("QmsItemSpecQueryService")
@Transactional
public class QmsItemSpecQueryServiceImpl implements QmsItemSpecQueryService {
	@Autowired
	private FtlQueryService ftlq;

	@Override
	public FtlMapRet selectQmsInspSpec(FtlMapPa pa1) throws Exception {
		FtlMapOptPa pa0 = new FtlMapOptPa();
		pa1.select("QMS/QmsItemSpec/QmsItemSpec-select").resultNm("qmsInspSpecList").pageOn();

		FtlMapRet retMap = ftlq.doDB(pa0, pa1);
		return retMap;
	}

	public FtlMapRet specTest() throws Exception {
		FtlMapOptPa pa0 = new FtlMapOptPa().put("pa1", "fadfaf");
		FtlMapPa pa1 = new FtlMapPa().select("qms/specTest1").resultNm("specTest1");
		FtlMapPa pa2 = new FtlMapPa().select("qms/specTest2").resultNm("specTest2");

		FtlMapRet retMap = ftlq.doDB(pa0, pa1, pa2);
		return retMap;
	}
}
