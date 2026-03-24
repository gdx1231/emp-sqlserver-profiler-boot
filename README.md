# emp-sqlserver-profiler-boot

Spring Boot wrapper for the emp-sqlserver-profiler project, providing a web interface for SQL Server profiling.

## Overview

The `emp-sqlserver-profiler-boot` project is a Spring Boot application that wraps the core `emp-sqlserver-profiler` functionality, providing a web-based interface for monitoring and analyzing SQL Server database activities. It builds upon the original Java-based SQL Server profiler by adding a web UI layer and REST API endpoints.

## Features

- Web-based interface for SQL Server profiling
- REST API endpoints for programmatic access
- Multiple profiler instance management
- Persistent configuration storage
- Real-time monitoring capabilities
- Trace data visualization
- Integrated shutdown hooks for clean resource cleanup

## Architecture

### Core Components

#### EmpScriptProfilerBootApplication.java
Main Spring Boot application class that:
- Initializes the HSQLDB server
- Manages multiple SqlServerProfiler instances
- Sets up shutdown hooks for clean resource cleanup
- Provides global access to profiler instances

#### Index Controller
Handles web requests and provides:
- Main dashboard view
- Profiler control endpoints
- Configuration management
- Trace data display

### Configuration

#### application.yml
- Server runs on port 39999 by default
- Context path is `/sp`
- Logging configured for DEBUG level on profiler components
- Multipart file upload limits configured

#### ewa_conf.xml
- Security configuration with AES encryption
- Database connection settings
- Path configurations for static resources
- Admin user configuration

## Endpoints

### Main Interface
- `/` - Main dashboard
- `/index` - Alternative index endpoint

### Profiler Control
- `/run.jsp` - Profiler control endpoint
- `/sqlprofiler/run.jsp` - Alternative profiler endpoint

## Usage

### Building the Project
```bash
mvn package
```

### Running the Application
```bash
java -jar target/emp-script-profiler-boot-1.0.0.jar
```

### Accessing the Interface
After starting, navigate to:
```
http://localhost:39999/sp
```

## Dependencies

- Spring Boot 2.7.0
- emp-sqlserver-profiler (core profiling engine)
- emp-script ecosystem libraries
- HSQLDB for local data storage
- JSON processing utilities

## Project Structure

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

## Configuration Options

### Database Connection Settings
The application stores SQL Server connection configurations in the TRACE_SERVER table, including:
- Host and port information
- Authentication credentials (encrypted)
- Database name
- Connection status

### HSQLDB Path Configuration
The `sqlprofiler_hsqldb_path` parameter in `ewa_conf.xml` controls where the local HSQLDB files are stored.

## Shutdown Behavior

The application registers a shutdown hook that:
1. Shuts down the HSQLDB server
2. Stops all active profiler instances
3. Ensures clean resource cleanup

## Security

- Credentials are encrypted using AES-256-GCM
- Default admin user configured in `ewa_conf.xml`
- Secure credential handling through the emp-script security framework

## Integration with Core Profiler

The boot application integrates with the core profiler by:
- Managing SqlServerProfiler instances
- Providing web-based control of profiler operations
- Storing and retrieving configuration from HSQLDB
- Handling profiler lifecycle events

## Default URL

The application is accessible at:
```
http://localhost:39999/sp
```

## Maven Coordinates

```xml
<groupId>com.gdxsoft</groupId>
<artifactId>emp-script-profiler-boot</artifactId>
<version>1.0.0</version>
```