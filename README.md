# Student-Information-System
## 用于记录学生信息

## 用法：

在`Student Information System\src`目录下输入命令:

\# 编译（使用分号分隔类路径）

```
javac -cp .;mysql-connector-j-8.1.0.jar *.java
```

\# 运行（同样使用分号）

```
java -cp .;mysql-connector-j-8.1.0.jar MainFrame
```

## 前置条件：

1.需要在`DatabaseUtil.java`中修改对应的端口号，账户和密码



2.本地mysql中需要有`student`的数据库和`xuesheng`的表

> 如果没有请创建
>
> 1.创建`student`数据库：
>
> ```sql
> CREATE DATABASE student CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
> ```
>
> 2.创建`xuesheng`表
>
> ```sql
> USE student;
> 
> CREATE TABLE xuesheng (
>     xuehao VARCHAR(20) PRIMARY KEY,
>     xingming VARCHAR(50) NOT NULL,
>     xingbie VARCHAR(10),
>     chushengriqi VARCHAR(20),
>     xueyuan VARCHAR(50)
> );
> ```
>
> 
