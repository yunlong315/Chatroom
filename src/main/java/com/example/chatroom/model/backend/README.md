# README.md
这里是钟熙桐，简单写了一下后端的架构，目前实现了网络通信以及注册功能
### 使用方式
1. 运行CenterServer类
2. 运行Client类

### 解释说明
1. CenterServer类为中央服务器，当有Client类运行时自动建立一个新的ClientThread线程
2. Client类通过与对应的ClientThread线程通信来完成注册功能
3. 为了方便后端测试，我直接在Client类中的main函数调用了注册函数，实际上应该由前端来调用（即Client类的main方法可删除）
4. 后续后端方面可以新建一个ChatRoomThread类

