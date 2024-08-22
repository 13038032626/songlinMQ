艹，功能差好多

1. 用户自己创建 | 点对点queue /push or poll | 发布订阅;
2. 实现发布订阅 结合netty就不太容易理解
3. 消息丢失啊！！！
4. 各种类型的消息能顺利编解码


核心在于设计message协议，使得即使多路复用channel，也能够准确识别消息是什么意思


server首先判断消息的channel是生产者的还是消费者的，因为在channelActive时，生产者/消费者就向server发起了身份验证，并且server在内存中维护了两种channel的set
如果是**生产者**，消息分为两类，server分别处理
- ManageMessage - 负责执行创建queue / 查看已有哪些queue
- DataMessage - 负责向指定queue中添加数据

ManageMessage 只需传递0/1/2，表示创建poll / push / 查看所有queue
DataMessage 核心要传递两类信息 - data(接口)，目标队列
设计的主要思想是：通过在生产者直接明确要把数据发往哪个队列，实现点对点，发布订阅
- 所以要解决的第一个问题是：怎么让生产者知道server中有哪些队列：
  - 在channelActive时将server中维护的队列信息（队列的description和size）按确定模式的字符串返回，生产者解析后即可得知哪些queue是干嘛的
  - 也可以通过发起ManageMessage来实时查看有哪些queue

如果是**消费者**，消息也是两类，server也需要分别处理 -- (消费者也需要偶尔向server发起消息，包括注册的时候、更改绑定queue的时候)
- ManageMessage - 负责查看有哪些queue
- DataMessage - 传递多个queue的编号，表示要从这些queue中获取信息
  - 亮点：在这个message中，既有要poll的queue的编号，也有要push的queue的编号（即在这个DataMessage中，将server中每个pushQueue完成绑定）

守护线程：自动发送（push模式），单线程while true

push的queue需要最开始就和channel绑定，

消息丢失解决： 
  > 顺序必要吗？
 
netty的channelRead其实天生支持写回调函数，因为已经将message和发来的channel绑定起来面向程序员了
方案一：在producer端维护一个锁（boolean），只有收到ACK才能停止阻塞，否则1s后重发
方案二：HashSet<对象：UUID，isACKed，times> 用又一个线程定时扫描后重发，完成消息补偿 -- 顺序可能有问题

在server也要有HashSet<UUID,isACKed，times> 记录发给消费者的消息是否有ACK，又一线程定时扫描重发，isACKed或者times超过三次则在set中去除/或者保留始终未发送的
  - 问题1：server怎么解决重复消息 -- UUID
netty的构造也天生支持这些 