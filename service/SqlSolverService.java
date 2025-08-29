package com.example.hiring.service;

import com.example.hiring.util.RegNoUtil;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SqlSolverService {
  private final String q1;
  private final String q2;

  public SqlSolverService(Environment env) {
    this.q1 = env.getProperty("app.sql.q1");
    this.q2 = env.getProperty("app.sql.q2");
  }

  public String resolveFinalSql(String regNo) {
    return RegNoUtil.lastTwoDigitsOdd(regNo) ? q1 : q2;
  }
}
