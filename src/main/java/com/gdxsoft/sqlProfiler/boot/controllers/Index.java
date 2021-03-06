package com.gdxsoft.sqlProfiler.boot.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdxsoft.easyweb.data.DTTable;
import com.gdxsoft.easyweb.script.HtmlControl;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.utils.UAes;
import com.gdxsoft.easyweb.utils.UJSon;
import com.gdxsoft.sqlProfiler.HSqlDbServer;
import com.gdxsoft.sqlProfiler.ProfilerControl;
import com.gdxsoft.sqlProfiler.SqlServerProfiler;
import com.gdxsoft.sqlProfiler.boot.EmpScriptProfilerBootApplication;

@Controller
public class Index {
	private static Logger LOGGER = LoggerFactory.getLogger(Index.class);

	@RequestMapping({ "/", "/index" })
	@ResponseBody
	public String index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			com.gdxsoft.sqlProfiler.HSqlDbServer.getInstance();
		} catch (Exception e) {
			LOGGER.error("HSqlDbServer.getInstance {}", e.getMessage());
			return "HSqlDbServer.getInstance, " + e.getLocalizedMessage();
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
			LOGGER.error("HSqlDbServer.getInstance {}", e.getMessage());
			return "HSqlDbServer.getInstance, " + e.getLocalizedMessage();
		}

		RequestValue rv = new RequestValue(request);
		String method = rv.s("method");
		int tsId = -1;
		try {
			tsId = rv.getInt("ts_id");
		} catch (Exception err) {
			return UJSon.rstFalse("???????????????: ts_id").toString(2);
		}
		String sql = "select * from TRACE_SERVER where ts_id = " + tsId;
		DTTable tb = DTTable.getJdbcTable(sql, HSqlDbServer.CONN_STR);
		if (tb.getCount() == 0) {
			return UJSon.rstFalse("?????????????????????").toString(2);
		}
		SqlServerProfiler sp;
		try {
			String server = tb.getCell(0, "TS_HOST").toString();
			int port = tb.getCell(0, "TS_PORT").toInt();
			String database = tb.getCell(0, "TS_DATABASE").toString();
			String username = tb.getCell(0, "TS_UID").toString();
			String password = tb.getCell(0, "TS_PWD").toString();
			if (password != null && password.trim().length() > 0) {
				password = UAes.defaultDecrypt(password);
			}

			sp = SqlServerProfiler.getInstance(tsId);
			if (sp.getServer().equals(server) && sp.getPort() == port && sp.getPassword().equals(password)
					&& sp.getUsername().equals(username) && sp.getDatabase().equals(database)) {
				if (!EmpScriptProfilerBootApplication.SqlServerProfilers.containsKey(tsId)) {
					EmpScriptProfilerBootApplication.SqlServerProfilers.put(tsId, sp);
				}
			} else {
				// ???????????????
				sp.stopProfiling();
				SqlServerProfiler.removeInstance(tsId);
				
				EmpScriptProfilerBootApplication.SqlServerProfilers.remove(tsId);
				return UJSon.rstFalse("Conf changed, delete the instance").toString(2);
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
