package com.ftl.service;

import java.util.List;

public interface FtlQueryService {

	public FtlMapRet doDB(FtlMapOptPa paOpt, FtlMapPa... paVaArg) throws Exception;

	public FtlMapRet doDB(FtlMapOptPa paOpt, List<FtlMapPa> paLst) throws Exception;

}
