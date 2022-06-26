package com.gdxsoft.sqlProfiler.boot.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdxsoft.easyweb.data.DTTable;
import com.gdxsoft.easyweb.script.HtmlControl;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.utils.UJSon;
import com.gdxsoft.sqlProfiler.HSqlDbServer;
import com.gdxsoft.sqlProfiler.ProfilerControl;
import com.gdxsoft.sqlProfiler.SqlServerProfiler;
import com.gdxsoft.sqlProfiler.boot.EmpScriptProfilerBootApplication;

@Controller
public class Index {

	@RequestMapping({ "/", "/index" })
	@ResponseBody
	public String index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			com.gdxsoft.sqlProfiler.HSqlDbServer.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestValue rv = new RequestValue(request);
		HtmlControl ht = new HtmlControl();
		String xmlName = "sqlprofiler.xml";
		String itemName = "TRACE_SERVER.LF.M";
		String paras = "";
		ht.init(xmlName, itemName, paras, rv, response);

		if (rv.s("ewa_ajax") != null) {
			return ht.getHtml();
		} else {
			return ht.getAllHtml();
		}
	}

	@RequestMapping({ "/run.jsp", "/sqlprofiler/run.jsp" })
	@ResponseBody
	public String runJsp(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			com.gdxsoft.sqlProfiler.HSqlDbServer.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestValue rv = new RequestValue(request);
		String method = rv.s("method");
		int tsId = -1;
		try {
			tsId = rv.getInt("ts_id");
		} catch (Exception err) {
			return UJSon.rstFalse("错误的参数: ts_id").toString(2);
		}
		if (method == null) {
			String sql = "select * from TRACE_SERVER where ts_id = @ts_id";
			DTTable tb = DTTable.getJdbcTable(sql, HSqlDbServer.CONN_STR, rv);
			if (tb.getCount() == 0) {
				return UJSon.rstFalse("配置信息不存在").toString(2);
			}
		}
		SqlServerProfiler sp;
		try {
			sp = SqlServerProfiler.getInstance(tsId);
			if (!EmpScriptProfilerBootApplication.SqlServerProfilers.containsKey(tsId)) {
				EmpScriptProfilerBootApplication.SqlServerProfilers.put(tsId, sp);
			}
		} catch (Exception e) {
			return UJSon.rstFalse(e.getMessage()).toString(2);
		}
		if (method != null) {
			JSONObject result;
			try {
				result = ProfilerControl.control(sp, method);
				result.put("work_path", HSqlDbServer.WORK_PATH);
			} catch (Exception e) {
				return UJSon.rstFalse(e.getMessage()).toString(2);
			}
			if (method.equals("clear")) {
				sp.stopProfiling();
				SqlServerProfiler.removeInstance(tsId);
			}
			return result.toString(2);
		}
		String xmlName = "sqlprofiler.xml";
		String itemName = "TRACE_LOG.LF.M";
		String paras = "";
		HtmlControl ht = new HtmlControl();
		ht.init(xmlName, itemName, paras, request, request.getSession(), response);
		if (rv.s("ewa_ajax") != null) {
			return ht.getHtml();
		}

		if (rv.s("ewa_ajax") != null) {
			return ht.getHtml();
		} else {
			return ht.getAllHtml();
		}
	}

}
