package com.gdxsoft.sqlProfiler.boot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.gdxsoft.easyweb.datasource.DataConnection;
import com.gdxsoft.sqlProfiler.HSqlDbServer;
import com.gdxsoft.sqlProfiler.SqlServerProfiler;

@SpringBootApplication
@ComponentScan(basePackages = { "com.gdxsoft.easyweb.spring.controllers" /* ewa and define */
		, "com.gdxsoft.easyweb.spring.restful.controllers" /* restfull */
		, "com.gdxsoft.easyweb.spring.staticResources.controllers" /* 静态资源 */
		, "com.gdxsoft.sqlProfiler.boot.controllers" })
public class EmpScriptProfilerBootApplication {

	public static Map<Integer, SqlServerProfiler> SqlServerProfilers;

	public static void main(String[] args) {
		SqlServerProfilers = new ConcurrentHashMap<Integer, SqlServerProfiler>();
		try {
			HSqlDbServer.getInstance();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		SpringApplication.run(EmpScriptProfilerBootApplication.class, args);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("shutdown");
				try {
					// 关闭HsqlDb
					DataConnection.updateAndClose("SHUTDOWN", HSqlDbServer.CONN_STR, null);
				} catch (Exception ioEx) {
					ioEx.printStackTrace();
				}
				for (int sid : SqlServerProfilers.keySet()) {
					// 关闭所有跟踪
					SqlServerProfiler sp = SqlServerProfilers.get(sid);
					System.out.println("Stop profiler: " + sid);
					try {
						sp.stopProfiling();
					} catch (Exception ioEx) {
						ioEx.printStackTrace();
					}
				}
			}
		});
	}

	public EmpScriptProfilerBootApplication() {
		System.out.println("1");

	}
}
