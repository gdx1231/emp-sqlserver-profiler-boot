---
name: graalvm-native-sb2
description: 将 Spring Boot 2.x 项目用 GraalVM Native Image 编译为原生可执行文件（无需 spring-native 依赖）
source: auto-skill
extracted_at: '2026-06-27T09:11:38.791Z'
---

# GraalVM Native Image 编译 Spring Boot 2.x

适用于没有 AOT / spring-native 依赖的 SB 2.x 项目，直接用 GraalVM `native-image` 编译 fat jar。

## 前置条件

- GraalVM JDK 已安装（Oracle GraalVM 或 GraalVM CE）
- `native-image` 可用（`gu install native-image` 或 GraalVM 自带）
- 本机 C 编译器可用（macOS: `xcode-select --install`；Linux: `gcc`）

## 步骤

### 1. 定位 GraalVM JAVA_HOME

macOS:

```bash
/usr/libexec/java_home -V | grep -i graalvm
```

设置环境变量（每个 shell 会话都需要）：

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/graalvm-jdk-21/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

验证：

```bash
java -version        # 应显示 GraalVM
native-image --version
```

### 2. 修复 pom.xml 版本范围（如需要）

Spring Boot 2.x 项目常用版本范围 `[1.0.0,]`，Maven 可能从远程仓库解析到本地不存在的版本导致编译失败。

将版本范围改为精确版本：

```xml
<!-- 改前 -->
<version>[1.1.3,]</version>

<!-- 改后 -->
<version>1.1.10</version>
```

### 3. Maven 编译

```bash
./mvnw clean package -DskipTests -q
```

### 4. 解压 fat jar 获取 classpath

```bash
mkdir -p target/native
cd target/native
jar xf ../<artifact>-<version>.jar
```

查看 `META-INF/MANIFEST.MF` 获取主类：

```bash
unzip -p ../<artifact>-<version>.jar META-INF/MANIFEST.MF
```

关注 `Start-Class`（SB 2.x 的实际入口），注意 MANIFEST.MF 每行最多 72 字符，主类名可能跨行。

### 5. 构建 classpath 并运行 native-image

```bash
CP="BOOT-INF/classes:$(echo BOOT-INF/lib/*.jar | tr ' ' ':')"

native-image \
  --no-fallback \
  -H:+ReportExceptionStackTraces \
  -cp "$CP" \
  com.example.boot.Application \
  ../app-name-native
```

- `--no-fallback`：禁止降级到 JVM 模式（纯原生）
- `-H:+ReportExceptionStackTraces`：构建时异常打印完整堆栈

### 6. 验证产物

```bash
file ../app-name-native
ls -lh ../app-name-native
```

## 注意事项

- SB 2.x 没有 AOT 处理，lib 中的 `native-image.properties`（如 tomcat-embed-core）会自动生效
- 项目自有反射/动态代理/资源加载需要手动补充 `reflect-config.json` 等，否则运行时报错
- 可用 GraalVM tracing agent 运行时收集配置：`java -agentlib:native-image-agent=config-output-dir=... -jar app.jar`
- 产物大小通常在 80-120 MB（包含整个 JDK 子集及所有依赖）
