package com.ftl.rest;

import com.ftl.service.FtlMapPa;
import com.ftl.service.FtlMapRet;

public interface QmsItemSpecQueryService {

	public FtlMapRet selectQmsInspSpec(FtlMapPa pa1) throws Exception;
	public FtlMapRet specTest() throws Exception;

}
