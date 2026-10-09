# Spring AI 项目学习进度

## 项目目标

边学习 Java、Spring Boot 和 Spring AI，边完成一个可以持续扩展的本地 AI 对话项目，后续逐步接入更完善的用户体系、前端和 RAG 等 AI 应用能力。

## 当前环境

- 操作系统：Windows 11
- Java：21
- Spring Boot：4.1.0
- Spring AI：2.0.0
- Maven：3.9.16
- 数据库：MySQL，数据库名为 `spring_ai`
- 本地模型：Ollama `qwen2.5:1.5b`
- 接口测试：APIFox
- 代码仓库：GitHub

## 已完成内容

### 项目基础与接口分层

- 接入 Spring AI `ChatClient` 和 Ollama。
- 创建 `ChatController`、`ChatService`、VO、DTO 和统一返回对象 `ReturnVO<T>`。
- 使用 `ChatRole` 枚举管理 AI 角色。
- 使用 `AIProperties` 和 `@ConfigurationProperties` 绑定 `app.ai` 配置。
- 创建全局异常处理器和业务异常。
- 使用 Swagger 注解为主要接口补充名称和说明。

### MySQL 聊天记忆

- 创建 `ChatSession`、`ChatMessage` JPA 实体。
- 创建 `ChatSessionRepository`、`ChatMessageRepository`。
- 将聊天记忆从内存集合迁移到 MySQL。
- 将消息正文映射为 `LONGTEXT`，解决模型长回答无法保存的问题。
- 将用户问题和 AI 回答放入同一个 `addConversation()` 事务中保存。
- 按创建时间和消息 ID 稳定排序历史消息。
- 超出 `max-messages` 时按一轮对话两条消息成对删除。
- 完成查询历史消息、清空会话和查询会话列表接口。

### 用户隔离与数据关系

- 创建 `User` 实体、`UserRepository`、`UserCreateVO` 和 `UserResponse`。
- 创建用户接口 `POST /ai/users`。
- `ChatSession` 通过 `user_id` 外键关联用户。
- 聊天请求增加必填 `userId`。
- 查询会话时同时使用 `userId + sessionId`，防止读取其他用户的会话。
- 增加 `ChatSessionConflictException`，处理 `sessionId` 被其他用户占用的情况。
- 支持查询指定用户的会话列表，并按 `updatedAt` 倒序排列。

### 自动化测试

- 创建 `ChatMemoryServiceTest`，使用 JUnit 5、Mockito 和 AssertJ。
- 已覆盖：用户不存在、复用自己的会话、会话属于其他用户三个场景。
- 2026-10-07 执行 `mvn clean test`：28 个主源码文件编译成功，3 个测试全部通过。

## 当前调用链

```text
APIFox
  -> Controller 接收 VO
  -> ChatService 组装提示词并调用 Ollama
  -> ChatMemoryService 管理事务和业务规则
  -> Repository 操作 JPA 实体
  -> MySQL 保存 User、ChatSession、ChatMessage
```

## 当前学习节点

当前阶段：理解并验证事务边界、外键约束、会话清理和测试覆盖。

当前代码已经实现相关功能，下一步不是重新编写实体，而是理解为什么这样组织，并通过测试证明关键业务规则：

1. AI 调用不放进数据库事务，避免模型响应期间长期占用数据库事务。
2. `addConversation()` 在一个事务中保存用户问题和 AI 回答，避免只保存半轮对话。
3. 清空会话时先删除消息，再删除会话，满足外键约束。
4. `sessionId` 全局唯一，同时通过 `userId` 做用户隔离。
5. 扩充单元测试：新会话创建、成对保存、超限成对删除、清空会话和查询隔离。

## 重点知识索引

| 知识点 | 作用 | 项目位置 |
|---|---|---|
| `@RestController` | 声明 REST 接口控制器 | `controller` 包 |
| `@Valid` | 触发 VO 参数校验 | `ChatController`、`UserController` |
| VO / DTO / Entity | 分别负责请求、业务返回和数据库映射 | `vo`、`dto`、`entity` 包 |
| `ReturnVO<T>` | 统一接口业务响应结构 | `vo/ReturnVO.java` |
| `@ConfigurationProperties` | 将同一前缀配置绑定为 Java 对象 | `config/AIProperties.java` |
| `@Entity` | 将 Java 类映射到数据库表 | `User`、`ChatSession`、`ChatMessage` |
| `@ManyToOne` | 表示多个会话属于一个用户、多个消息属于一个会话 | `ChatSession.user`、`ChatMessage.chatSession` |
| `JpaRepository` | 提供基础增删改查和派生查询 | `repository` 包 |
| `Optional` | 显式表达查询结果可能不存在 | `ChatSessionRepository`、`UserRepository` |
| `@Transactional` | 保证一组数据库操作整体成功或回滚 | `ChatMemoryService` |
| 派生查询方法 | 根据方法名自动生成查询 | `findByUser_IdAndSessionId()` 等 |
| `record` | 简洁定义不可变数据载体 | `dto`、`ConversationMessage` |
| Stream / `map()` / `toList()` | 完成实体到 DTO 的集合转换 | `ChatMemoryService` |
| Mockito | 隔离 Repository，测试 Service 业务判断 | `ChatMemoryServiceTest` |
| AssertJ | 对异常类型和消息进行链式断言 | `ChatMemoryServiceTest` |

## 当前需要验证

- 使用 APIFox 创建两个用户。
- 验证两个用户不能共用同一个 `sessionId`。
- 验证应用重启后历史消息仍然存在。
- 验证删除会话后，对应消息和会话均被删除。
- 验证超过 `max-messages` 后，历史消息仍按完整问答轮次保留。

## 后续学习路线

1. 扩充 `ChatMemoryService` 单元测试并理解事务边界。
2. 优化 HTTP 状态码与统一返回体的一致性。
3. 将数据库密码等敏感配置迁移到环境变量。
4. 完善用户查询、会话标题和会话管理。
5. 学习前端接入、跨域和接口联调。
6. 学习 RAG、向量数据库和知识库问答。

## 学习习惯

- 代码默认由用户自己输入，助手负责展示、解释和检查。
- 新增代码先说明文件位置、用途、必要性和调用链。
- 关键业务判断添加必要注释，不逐行添加无意义注释。
- 每个阶段结束后更新本文件；必要时同步更新 `AGENTS.md`。
- 优先使用 APIFox 验证接口，使用 `mvn test` 验证构建和测试。

## 2026-10-09 进度补充

- 用户确认已完成上一节全局异常 HTTP 状态码与统一返回体一致性验证。
- 已学习 `@ExceptionHandler` 负责异常分派，`@ResponseStatus` 设置 HTTP 响应状态；二者与 `ReturnVO.code` 的职责不同。
- 当前继续处理创建用户接口：成功创建返回 HTTP 201，重复用户名返回 HTTP 409，使用 `ResponseEntity` 按分支设置状态。
- 此次终端工具启动失败，未重新读取源码或执行 Maven 测试；创建用户接口的最新实现和验证结果待后续核对。
- 本小节待验证：创建新用户得到 HTTP 201 / JSON code 201，重复创建得到 HTTP 409 / JSON code 409。
