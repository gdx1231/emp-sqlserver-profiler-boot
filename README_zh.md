# emp-sqlserver-profiler-boot

emp-sqlserver-profiler 的 Spring Boot 封装，提供 SQL Server 分析的 Web 界面。

## 概述

`emp-sqlserver-profiler-boot` 项目是一个 Spring Boot 应用程序，封装了核心 `emp-sqlserver-profiler` 功能，提供了用于监控和分析 SQL Server 数据库活动的 Web 界面。它在原始的基于 Java 的 SQL Server 分析器基础上增加了 Web UI 层和 REST API 端点。

## 功能特性

- SQL Server 分析的 Web 界面
- 用于程序化访问的 REST API 端点
- 多分析器实例管理
- 持久化配置存储
- 实时监控功能
- 跟踪数据显示
- 集成的关闭钩子以实现干净的资源清理

## 架构

### 核心组件

#### EmpScriptProfilerBootApplication.java
主 Spring Boot 应用程序类，负责：
- 初始化 HSQLDB 服务器
- 管理多个 SqlServerProfiler 实例
- 设置关闭钩子以实现干净的资源清理
- 提供对分析器实例的全局访问

#### Index 控制器
处理 Web 请求并提供：
- 主仪表板视图
- 分析器控制端点
- 配置管理
- 跟踪数据显示

### 配置

#### application.yml
- 服务器默认运行在端口 39999 上
- 上下文路径为 `/sp`
- 分析器组件的日志级别配置为 DEBUG
- 多部分文件上传限制已配置

#### ewa_conf.xml
- 使用 AES 加密的安全配置
- 数据库连接设置
- 静态资源的路径配置
- 管理员用户配置

## 端点

### 主界面
- `/` - 主仪表板
- `/index` - 替代索引端点

### 分析器控制
- `/run.jsp` - 分析器控制端点
- `/sqlprofiler/run.jsp` - 替代分析器端点

## 使用方法

### 构建项目
```bash
mvn package
```

### 运行应用程序
```bash
java -jar target/emp-script-profiler-boot-1.0.0.jar
```

### 访问界面
启动后，导航到：
```
http://localhost:39999/sp
```

## 依赖项

- Spring Boot 2.7.0
- emp-sqlserver-profiler (核心分析引擎)
- emp-script 生态系统库
- HSQLDB 用于本地数据存储
- JSON 处理工具

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/gdxsoft/sqlProfiler/boot/
│   │       ├── EmpScriptProfilerBootApplication.java
│   │       └── controllers/
│   │           └── Index.java
│   └── resources/
│       ├── application.yml
│       ├── ewa_conf.xml
│       └── sqlprofiler_define_xml/
│           ├── sqlprofiler.xml
│           └── sqlprofiler.xml.json
```

## 配置选项

### 数据库连接设置
应用程序在 TRACE_SERVER 表中存储 SQL Server 连接配置，包括：
- 主机和端口信息
- 认证凭据（加密）
- 数据库名称
- 连接状态

### HSQLDB 路径配置
`ewa_conf.xml` 中的 `sqlprofiler_hsqldb_path` 参数控制本地 HSQLDB 文件的存储位置。

## 关闭行为

应用程序注册了一个关闭钩子，该钩子：
1. 关闭 HSQLDB 服务器
2. 停止所有活跃的分析器实例
3. 确保干净的资源清理

## 安全性

- 凭据使用 AES-256-GCM 加密
- 在 `ewa_conf.xml` 中配置默认管理员用户
- 通过 emp-script 安全框架处理安全凭据

## 与核心分析器的集成

引导应用程序通过以下方式与核心分析器集成：
- 管理 SqlServerProfiler 实例
- 提供分析器操作的 Web 界面控制
- 从 HSQLDB 存储和检索配置
- 处理分析器生命周期事件

## 默认 URL

应用程序可通过以下地址访问：
```
http://localhost:39999/sp
```

## Maven 坐标

```xml
<groupId>com.gdxsoft</groupId>
<artifactId>emp-script-profiler-boot</artifactId>
<version>1.0.0</version>
```