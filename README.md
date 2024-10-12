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
