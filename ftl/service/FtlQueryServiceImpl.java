package com.ftl.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("FtlQueryService")
@Transactional
public class FtlQueryServiceImpl implements FtlQueryService {

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Autowired
	private PlatformTransactionManager txManager;

	@Value("${FTL.qryDir}")
	public String ftlQryDir;

	public FtlMapRet doDB(FtlMapOptPa paOpt, List<FtlMapPa> paLst) throws Exception {
		if (FtlQryMaker.qryDir == null)
			FtlQryMaker.qryDir = ftlQryDir;

		for (int i = 0; i < paLst.size(); i++) {
			paLst.get(i).validate(paOpt);
		}

		//TransactionStatus txStatus = txManager.getTransaction(new DefaultTransactionDefinition());
		//DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		//txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		//TransactionStatus txStatus = txManager.getTransaction(txDefinition);

		FtlMapRet retMap = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		Connection conn = sqlSession.getConnection();
		try {
			retMap = doDBloop(conn, paOpt, paLst);
			//txManager.commit(txStatus);
			sqlSession.commit();
			return retMap;
		} catch (Exception e) {
			//txManager.rollback(txStatus);
			sqlSession.rollback();
			throw e;
		}
		//finally {
		//	conn.close();
		//	sqlSession.close();
		//}
	}

	@Override
	public FtlMapRet doDB(FtlMapOptPa paOpt, FtlMapPa... paVaArg) throws Exception {
		List<FtlMapPa> lst = new ArrayList<FtlMapPa>();
		for (int i = 0; i < paVaArg.length; i++) {
			lst.add(paVaArg[i]);
		}
		return doDB(paOpt, lst);
	}

	private FtlMapRet doDBloop(Connection conn, FtlMapOptPa paOpt, List<FtlMapPa> paLst) throws Exception {

		FtlMapRet retMap = new FtlMapRet();

		for (int qnum = 0; qnum < paLst.size(); qnum++) {
			FtlMapPa pa1 = paLst.get(qnum);
			if (pa1 == null)
				continue;

			retMap.copyToParam(pa1);

			retMap.retAdd(conn, pa1);

		} // for paLst
		return retMap;
	}
}
