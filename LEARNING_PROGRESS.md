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
- 本地模型：Ollama `qwen3.5:2b`（关闭思考模式），保留 `qwen2.5:1.5b` 供回退。
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

当前阶段：会话标题与会话管理；旧阶段的事务、外键和测试知识作为回顾内容保留。

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

## 2026-10-09 数据库配置阶段补充

- 用户已将数据库用户名和密码改为环境变量占位符，并在 IDEA 的 SpringAIApplication 运行配置中设置对应环境变量。
- 用户确认启动报错已解决，数据库环境变量配置可以正常使用。
- 新知识：`${DB_USERNAME:root}` 提供默认用户名，`${DB_PASSWORD}` 从启动进程环境读取密码；IDEA 运行配置与独立终端的环境变量互不自动同步。
- 下一小节：增加会话标题字段并通过 ChatSessionResponse 返回，兼容已有无标题会话；随后接入标题生成和修改接口。
- 当前终端读取工具启动失败，后续会话标题代码基于最近一次已核对的实体和 DTO 展示，尚未录入或验证。

## 2026-10-12 会话标题阶段

- 已扫描确认：ChatSession 已添加 title 字段，新会话默认标题为“新对话”。
- 已扫描确认：ChatSessionResponse 返回 title，并兼容旧记录的 null 或空白标题。
- 本次讲解修改标题接口：ChatSessionTitleVO 接收和校验输入，ChatSession.rename() 限制实体修改，ChatMemoryService.renameSession() 检查归属并通过事务更新，Controller 使用 PATCH 接收局部修改。
- 新知识：JPA 脏检查会在事务提交时同步受管理实体的变更；当前修改会触发已有 @PreUpdate，从而更新 updatedAt。
- 业务代码由用户录入。本次未执行构建或数据库接口验证，修改标题功能仍待录入和验证。
- 验证重点：本人可修改、其他用户不可修改、空白或超长标题返回 400、应用重启后标题仍保留。
- 下一步：核对修改标题接口，再考虑从首条问题生成默认标题和会话列表分页。

## 2026-10-12 本地模型切换

- 用户授权直接切换模型。已下载 `qwen3.5:2b` 并通过 Ollama 文件校验，保留旧模型 `qwen2.5:1.5b`。
- `application.yml` 的 `spring.ai.ollama.chat.model` 已改为 `qwen3.5:2b`，新增 `think: "false"` 以关闭思考模式、减少本地生成开销。
- 环境核对：HP ProBook 450 G9、i5-1240P、约 16 GB 内存、Intel Iris Xe 集成显卡；Ollama 服务版本为 0.33.2，实测模型运行使用 CPU。
- 已通过项目实际 Spring AI 2.0.0 依赖的 `ChatClient` 调用验证，并从最终 YAML 绑定模型与思考模式配置；中文回答非空、历史信息“小明”识别通过。
- 首次短回答调用约 18.3 秒，加载后另一次短回答约 10.3 秒；仅为少量样例，不代表所有请求速度或回答准确率。
- 已运行 `mvn test`：7 个测试全部通过；未新增业务源码或测试。
- 已读取 UTF-8 结果文件确认中文正常，终端输出乱码属于显示编码问题。
- 待验证：在 IDEA 重启应用，使用 APIFox 调用聊天接口，确认返回 `model` 为 `qwen3.5:2b`，回答和历史消息正常；本次未启动完整应用或进行数据库接口验证。
- 下一步仍为会话标题修改接口与会话列表分页，模型切换不改变当前学习节点。

## 2026-10-12 默认标题生成小节

- 已扫描确认 ChatSessionTitleVO、ChatSession.rename()、ChatMemoryService.renameSession() 和 PATCH 修改标题接口均已录入，接口运行验证仍待用户确认。
- 继续讲解：仅创建新会话时根据首条问题生成默认标题；已有会话直接复用，避免覆盖用户手动修改的标题。
- 标题生成采用本地字符串处理，不增加模型调用：合并空白、取前 30 个 Unicode 码点，超出时添加省略号。
- 本小节代码由用户录入，尚未实施或验证；不修改已有历史会话的标题。
- 待验证：首条问题生成标题、后续问题不覆盖标题、手动改名后继续聊天保持原标题。
- 下一步：会话列表分页。
