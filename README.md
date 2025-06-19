# Student-Information-System
## 用于记录学生信息

## 用法：

在`Student Information System\src`目录下输入命令:

\# 编译（使用分号分隔类路径）

```
javac -cp ".;../lib/*" *.java
```

\# 运行（同样使用分号）

```
java -cp ".;../lib/*" LoginFrame
```

## 前置条件：

1.需要在`DatabaseUtil.java`中修改对应的端口号，账户和密码

![修改位置](1/image-20250618085310995.png)



2.本地mysql中需要有`student`的数据库和`xuesheng`的表 

> 如果没有请创建
>
> 1.创建`student`数据库：
>
> ```sql
> CREATE DATABASE IF NOT EXISTS student 
>     CHARACTER SET utf8mb4 
>     COLLATE utf8mb4_unicode_ci;
> ```
>
> 2.创建`xuesheng`表：
>
> ```sql
> CREATE TABLE IF NOT EXISTS xuesheng (
>     xuehao VARCHAR(20) NOT NULL COMMENT '学号',
>     xingming VARCHAR(50) NOT NULL COMMENT '姓名',
>     xingbie ENUM('男','女') NOT NULL COMMENT '性别',
>     chushengriqi DATE NOT NULL COMMENT '出生日期',
>     xueyuan VARCHAR(50) NOT NULL COMMENT '学院',
>     PRIMARY KEY (xuehao, xueyuan)  -- 复合主键
> ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
> ```
>
> 3.创建`user`表：
>
> ```sql
> CREATE TABLE IF NOT EXISTS `user` (
>     username VARCHAR(50) NOT NULL PRIMARY KEY COMMENT '用户名',
>     password VARCHAR(100) NOT NULL COMMENT '密码(建议存储哈希值)',
>     is_admin BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否是管理员',
>     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
>     last_login TIMESTAMP NULL COMMENT '最后登录时间'
> ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
> ```
>
> 4.创建`scores`表：
>
> ```sql
> CREATE TABLE IF NOT EXISTS scores (
>     student_id VARCHAR(20) NOT NULL COMMENT '学生学号',
>     xueyuan VARCHAR(50) NOT NULL COMMENT '学院(冗余存储但提高查询性能)',
>     `语文` DECIMAL(5,2) DEFAULT NULL COMMENT '语文成绩(0-100)',
>     `高数` DECIMAL(5,2) DEFAULT NULL COMMENT '高等数学成绩',
>     `英语` DECIMAL(5,2) DEFAULT NULL COMMENT '英语成绩',
>     `Java` DECIMAL(5,2) DEFAULT NULL COMMENT 'Java成绩',
>     `Go` DECIMAL(5,2) DEFAULT NULL COMMENT 'Go成绩',
>     `Linux` DECIMAL(5,2) DEFAULT NULL COMMENT 'Linux成绩',
>     `双创` DECIMAL(5,2) DEFAULT NULL COMMENT '双创成绩',
>     `思政` DECIMAL(5,2) DEFAULT NULL COMMENT '思政成绩',
>     `实训` DECIMAL(5,2) DEFAULT NULL COMMENT '实训成绩',
>     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
>     PRIMARY KEY (student_id),
>     INDEX idx_xueyuan (xueyuan),
>     CONSTRAINT fk_student_id 
>         FOREIGN KEY (student_id, xueyuan) 
>         REFERENCES xuesheng(xuehao, xueyuan)
>         ON DELETE CASCADE  -- 启用级联删除
>         ON UPDATE CASCADE  -- 启用级联更新
> ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
> ```
>
> 
