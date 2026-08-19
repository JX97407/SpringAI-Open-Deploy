# Spring AI 项目学习进度

## 项目目标

边学习 Java、Spring Boot 和 Spring AI，边完成一个可以持续扩展的本地 AI 对话项目，后续逐步接入持久化、前端和更完整的 AI 应用能力。

## 当前环境

- 操作系统：Windows 11
- Java：21
- Spring Boot：4.1.0
- Spring AI：2.0.0
- Maven：项目构建工具
- Ollama：本地运行
- Ollama 模型：`qwen2.5:1.5b`
- 接口测试：APIFox
- 代码仓库：GitHub

## 已完成内容

### 项目基础

- 创建 Spring Boot 项目。
- 配置 Maven 依赖和 `application.yml`。
- 接入 Ollama 和 Spring AI `ChatClient`。
- 使用 APIFox 调用 `/ai/chat` 接口。
- 配置 Git、GitHub 和代理，完成项目远程仓库连接。

### 分层和数据对象

- 创建 `ChatController` 接收聊天请求。
- 创建 `ChatService` 编排 AI 对话业务。
- 创建 `ChatQueryVO` 接收前端请求参数。
- 创建 `ChatResponse` 返回聊天结果。
- 创建泛型统一返回对象 `ReturnVO<T>`。
- 理解 VO、DTO 和统一返回对象的职责区别。
- 使用 Lombok 的 `@Data`、`@Getter`、`@NoArgsConstructor` 和 `@AllArgsConstructor`。
- 使用 Java `record` 定义简单数据对象。

### AI 能力

- 使用 `system prompt` 改变模型的回答身份和风格。
- 从 `application.yml` 读取默认系统提示词。
- 使用 `ChatRole` 枚举管理 Java 老师、面试官和代码审查员角色。
- 使用 `role` 请求参数切换 AI 角色。
- 创建 `/ai/roles` 接口返回可用角色列表。
- 使用 `sessionId` 区分不同聊天会话。
- 创建 `ConversationMessage` 保存单条历史消息。
- 创建 `ChatMemoryService` 保存内存聊天记录。
- 将历史消息组装到当前用户提示词中，实现基础多轮对话。

### 异常和接口管理

- 创建 `AIChatException` 自定义 AI 调用异常。
- 创建 `GlobalExceptionHandler` 统一处理参数校验、请求体格式、非法角色和服务异常。
- 创建 `ChatMemoryController`。
- 创建查询历史记录接口：`GET /ai/sessions/{sessionId}/messages`。
- 创建清空会话接口：`DELETE /ai/sessions/{sessionId}`。
- 修正历史消息拼接使用的换行符 `\n`。

## 当前学习节点

当前阶段：完成配置类重构并准备接入 MySQL。

当前阶段补充：配置类 `AIProperties` 已创建，并使用 `@ConfigurationProperties(prefix = "app.ai")` 绑定自定义配置；接下来完成重复注入清理，再进入 MySQL 持久化。

- 在 `application.yml` 中增加 `app.ai.memory.max-messages` 配置。
- 已在 `ChatMemoryService` 中通过 `AIProperties` 读取最大消息数量。
- 添加新消息后删除最早消息，避免内存和模型提示词无限增长。
- 使用 APIFox 连续发送多轮消息，确认历史记录最多保留配置数量。
- 待清理 `ChatService` 中重复的 `systemPrompt` 字段和 `@Value` 注入，并使用 `mvn test`、`mvn spring-boot:run` 验证配置重构。

## 学习中需要重点理解的知识

- Java 文本块 `"""` 和 `String.formatted()`。
- `List.of()`、`List.copyOf()`、`Map.getOrDefault()` 和 `computeIfAbsent()`。
- Stream、Lambda、`map()`、`toList()` 和 `Collectors.joining()`。
- `record`、泛型、方法重载和静态工厂方法。
- Spring 依赖注入、`@Value`、请求参数校验和全局异常处理。
- `sessionId`、内存会话和持久化会话的区别。

## 后续学习路线

1. 完成并验证内存聊天记录数量限制。
2. 学习配置类绑定，逐步减少散落的 `@Value`。
3. 将聊天记忆从内存集合迁移到 SQLite 或 MySQL。
4. 学习用户、会话和消息的数据库关系。
5. 增加用户隔离和会话管理。
6. 学习前端接入、跨域和接口联调。
7. 学习 RAG、向量数据库和知识库问答。

## 学习习惯

- 代码默认由用户自己输入，助手负责展示、解释和检查。
- 新增代码需要说明文件位置、类的用途和调用链路。
- 关键代码加必要注释，不逐行添加无意义注释。
- 优先使用 APIFox 测试，不依赖 PowerShell 的中文输出判断结果。

## 最新进度补充

- `AIProperties` 已创建，用于绑定 `app.ai` 配置。
- 已增加 `app.ai.memory.max-messages`，并在内存服务中限制历史消息数量。
- `ChatService` 中仍有重复的 `systemPrompt` 字段和 `@Value` 注入，下一步先清理。
- 当前数据库方案确定为 MySQL，计划使用 Spring Data JPA。
- 后续每个学习阶段由助手自动更新本文件和 `AGENTS.md`，用户检查后提交到 GitHub。
