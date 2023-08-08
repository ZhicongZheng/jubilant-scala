# jubilant_scala
博客系统后端，基于 Java 17、 Scala 2.13.8，采用 DDD 分层架构

博客展示端： https://github.com/zhengruicong/jubilant-blog

博客管理端： https://github.com/zhengruicong/jubilant-adventure

此仓库是 http4s 支持 AOT 编译的版本，还有一个 Play framework 版本： https://github.com/zhengruicong/lingxi_scala

# 项目特点
- 无运行时反射
- 全部接口都是异步、响应式非阻塞的
- 支持 Graal Native Image 编译为二进制可执行文件，占用内存仅仅 50M，并且可以瞬间启动
- 支持 swagger 导出 openapi 文档，自动生成前端 TpyeScript 代码
- 支持 Markdown 文章编辑展示
- 评论、回复评论
- 邮件通知
- 动态权限
- 站点设置等功能

# 相关技术
- GraalVM Native Image             ->    AOT 编译
- Http4s    -> 函数式 http 框架
- circe     -> Json 转换
- chimney   -> 类转换
- slick     -> FRM 数据库操作
- macwire   -> 编译器依赖注入
- tapir     -> openapi 定义和生成
- ip2region -> Ip 归属地转换
- Docker
- PostgreSQL 15   
