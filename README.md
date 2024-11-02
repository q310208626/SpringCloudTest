## Spring Cloud 测试工程

### Spring Cloud + Nacos Bootstrrap
启动时通过bootstrap.yaml文件连接nacos，获取nacos上的application.yaml配置

CloudConfigService

--- 
### Spring Cloud + Nacos Origin
启动时通过application.yaml连接Nacos获取配置

ConfigService


--- 
### Spring Cloud + Ribbon + Seata + Nacos + OpenFeign + Gateway
- 两个服务件通过Spring Cloud的调用
- Nacos注册服务信息
- Ribbon在RestTemplate添加拦截器去nacos上根据服务名获取服务信息
- seata实现分布式事务
- Gateway API网关，服务解析，负载均衡

GatewayServer  
OrderService  
StockService

ribbon: com.order.controller.OrderController.addProduct  
seata: com.order.controller.OrderController.addProductWithException  
OpenFeign: com.order.controller.OrderController.addProductWithFeign  

---
### Spring Boot + Spring Security
- 自定义MyUsernamePasswordAuthenticationFilter 获取请求Json中的用户名密码，跟数据库中的用户名密码比对
- 自定义RequestBodyCopyServletRequestWrapper，封装request，解决MyUsernamePasswordAuthenticationFilter中inputStream只能读取一次的问题
- 自定义MyUserDetailsService，修改适配为自己的用户数据库查询逻辑
- DelegatingPasswordEncoder比对请求密码跟数据库密码，目前暂时使用的是明文比对
- SecurityContext使用HttpSessionSecurityContextRepository存储到当前容器的HttpSession中
- 开启csrfToken，/token获取token跟SessionId
- 自定义CsrfSaveAuthenticationStrategy，认证成功后，把新生成的csrfToken存放到Response Header中

请求流程大致为：
1. 请求/token，获取token跟sessionId
2. 使用第1步获取到的csrfToken跟seeionId，分别设置到Header跟Cookie中，发起/user/login请求
3. 发起/user/login请求成功时，会更新SessionId跟csrfToken，同时会把用户信息包装到SecurityContext存储到HttpSession中
4. 使用第3步中新的SessionId跟csrfToken，发起其他请求，csrfToken通过CsrfFilter校验，SessionId通过AuthorizationFilter校验当前HttpSession中是否有SecurityContext.authentication对象

UserService

