<h3>Archer接口验证系统</h3>

<h5>使用SpringBoot + HttpClient实现.</h5>
<h5>两种执行方式 -- 用例/单接口.</h5>
<h5>实体依赖关系: 项目 --> 用例 --> 接口</h5>
<h5>接口依赖关系: 接口 --> 响应 --> 抽取参数 --> 验证点</h5>
<h5>Hosts独立配置</h5>
<h5>三种正文抽取方式进行验证: 正则表达式 / JSONPath / CSS seletor</h5>

<h3>已修改问题</h3>
<p>进入功能改进阶段</p>
<p>已修复BUG: 验证点无法更新</p>
<p>已修复BUG: 允许发送没有请求体的POST请求(可以不设置Content-Type)</p>
<p>已修复BUG: 修复HTTPS URL的问题(待测试)</p>
<p>已修复BUG: 使用Hosts地址作为BaseURL</p>
<p>已修复BUG: 接口删除后,用例列表中依然存在已删除接口的ID</p>
<p>已修复BUG: 修复POST/PUT请求中ContentType必须是application/xxx的问题</p>
<p>已修复BUG: 修复添加服务之后服务列表不刷新的问题</p>
<p>已修复BUG: 修复接口列表级联下拉的JS问题</p>
<p>已修复BUG: 接口列表和用例接口列表分离(重要)</p>
<p>已修复BUG: 添加接口之后直接返回到接口列表(应返回到当前服务下的接口列表)</p>

<h3>功能优化</h3>
<p>新增需求: 项目-服务-接口分级</p>
<p>优化需求: 前端结构优化(列表)</p>
<p>优化需求: Hosts按IP排序</p>
<p>优化需求: 增加从响应中获取值对Header进行替换(重要)</p>
<p>优化需求: 多Hosts绑定(重要)</p>
<p>优化需求: 增加执行前的等待时间(毫秒)</p>
<p>优化需求: 增加删除接口的确认popbox</p>
<p>优化需求: 更改用例中接口顺序后,依然能保留验证点和关联参数(重要)</p>

<h3>部署配置</h3>
<p>文件名: application.yml 修改数据库密码</p>
<p>文件名: caseConfig.js   修改WebSocket指向为服务器地址</p>
<p>文件名: WebSocketConfig 注释掉serverEndpointExporter方法</p>
<p>文件名: build.gradle    打开providedRuntime('org.springframework.boot:spring-boot-starter-tomcat')的注释</p>
<p>文件名: build.gradle    开启war { enabled = true }</p>

<h3>待处理需求</h3>
<p>可重复测试相同接口</p>
