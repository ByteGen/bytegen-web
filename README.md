# Web

spring web 服务通用的配置, filter 和 annotation 等.

## Package

1. adapter

- Bean parameter 的支持, 示例如下

```java
@Controller
@RequestMapping("/api")
public class BeanParamSample {

    @MockApi
    @PostMapping(value = "/sample", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity execute(HttpServletRequest context,
                                  @BeanParam(required = false) BeanModel beanModel) {
        // do something
        return ResponseUtil.toSuccessResponse();
    }
}
```

2. basic

提供一些基本的数据结构

3. config

- application config: 全局配置
```
//////// web interceptors ////////

//////// argument resolvers ////////

//////// use Gson as serialization/deserialization tool instead of Jackson ////////

//////// international messages config ////////
```

- exception handler: 常见的异常对应的 Restful response 处理

- message service: 国际化信息的处理

4. filter

- authentication filter: 依赖 `Constant.GATEWAY_ACCOUNT_ID` 的简单认证处理

- request wrapper filter: 转换 request, 缓存输入流使之可重复读; 生成 trace_id 用于追踪; 记录请求/响应日志

- mock api filter: 验证 @MockApi 标记的接口是否允许访问

- @NoSignature: 不启用签名

```java
@Controller
public class Sample {
 
    @MockApi
    @NoSignature
    @Authentication
    @PostMapping(value = "/sample", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity execute(HttpServletRequest context,
                                  @BeanParam(required = false) BeanModel beanModel) {
        // do something
        return ResponseUtil.toSuccessResponse();
    }
}
```

5. formatter

定义了 Gson 序列化/反序列化中对 @Expose 字段的处理

6. util

- GsonUtl: Gson 工具
- NetworkUtil: ip/hostname 获取工具
- NWebUtil: 一些特殊标记字段的获取
- ResponseUtil: 简易的 response entity 获取

