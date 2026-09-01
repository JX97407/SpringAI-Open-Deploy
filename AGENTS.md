# Spring AI 学习项目协作规则

## 学习方式

- 使用中文进行讲解。
- 先说明编程思路和业务目的，再展示代码。
- 用户希望自己输入代码；默认只展示代码和解释，不直接修改项目文件。
- 新增类、接口、枚举或配置项时，先说明它的用途和为什么需要它。
- 对关键业务判断、复杂集合操作和容易误解的代码添加注释，不需要给每一行代码写注释。
- 解释代码中使用到的重要 Java 方法、Spring 注解和设计思路。
- 用户要求继续学习时，一次讲解适量的相关内容，并给出可执行的验证步骤。
- `AGENTS.md` 和 `LEARNING_PROGRESS.md` 由助手在每个学习阶段结束后自动更新；用户检查后再提交到 GitHub。

## 当前环境

- 操作系统：Windows 11
- IDE：IntelliJ IDEA
- Java：21
- Spring Boot：4.1.0
- Spring AI：2.0.0
- Maven：项目使用 Maven 构建
- 本地大模型：Ollama，模型为 `qwen2.5:1.5b`
- 接口测试工具：APIFox

## 项目约定

- 项目根包名为 `io.github.SpringAI`。
- 使用 Controller、Service、VO、DTO、Enum、Exception、Handler、Memory 等分层。
- API 返回统一使用 `ReturnVO<T>`。
- 请求对象使用 VO，业务返回对象使用 DTO。
- AI 角色通过 `ChatRole` 枚举管理。
- 聊天会话通过 `sessionId` 区分。
- 当前聊天记忆已经迁移到 MySQL，使用 Spring Data JPA 保存会话和消息。
- 数据库表结构和消息正文容量已经完成验证。
- 下一阶段学习事务边界、外键约束、会话清理和用户、会话、消息之间的数据库关系。
- 不要为了套用企业模板，机械地增加 `Service` 接口和 `ServiceImpl`；只有出现多个实现或明确抽象需求时再引入。
- 不要把密码、Token、代理账号等敏感信息提交到 GitHub。

## 验证方式

- 优先使用 APIFox 验证 HTTP 接口。
- 项目编译和测试使用 `mvn test`。
- PowerShell 终端可能出现中文编码显示问题，不能仅凭终端乱码判断接口实际返回错误。

## 继续学习时的要求

开始新内容前，先阅读 `LEARNING_PROGRESS.md`，确认已经完成的内容和当前学习节点。
每次课程结束时，总结本次新增知识、需要验证的内容和下一步目标。
