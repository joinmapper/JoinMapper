## 目录
1. *Mybatis,Hibernate,tk.mybatis对比*
2. <u >*Mybatis插件编写方法及原理(本次分享的目的)*</u>
3. *Mybatis插件编写示例：mybatis-join-mapper*
## 1. Mybatis,Hibernate,tk.mybatis对比
- 共同点
    - 都是ORM框架（Object Relational Mapping）
- Mybatis优势
    - MyBatis可以进行更为细致的SQL优化，可以减少查询字段。
    - MyBatis容易掌握，而Hibernate门槛较高。
- Hibernate优势
    - Hibernate的DAO层开发比MyBatis简单，Mybatis需要维护SQL和结果映射。
    - Hibernate对对象的维护和缓存要比MyBatis好，对增删改查的对象的维护要方便。
    - Hibernate数据库移植性很好，MyBatis的数据库移植性不好，不同的数据库需要写不同SQL。
    - Hibernate有更好的二级缓存机制，可以使用第三方缓存。MyBatis本身提供的缓存机制不佳。
- tk.mybatis优势
    - 使用注解(@Table,@Id,@Column等)和Mapper接口，不需要在xml中编写sql，快速开发。
## 2. Mybatis插件编写方法及原理（pagehelper和tk.mybatis都是基于Mybatis的插件）
- pagehelper插件分析

- tk.mybatis插件分析

- Mybatis插件编写方法总结
## 3. Mybatis插件编写示例：mybatis-join-mapper
- 背景
- 需求分析
- 实现步骤
    - 定义Mapper接口
    - 动态实现Mapper接口
    - 在Mybatis中配置插件
    - 测试
        - 创建多个表，通过JoinMapper接口关联查询，并结合pagehelper进行分页
        - 对tk.mybatis的版本兼容测试
- 这个插件的优点和缺点
    - 优点：数据库字段较多或者关联查询很多时，在xml中编写resultMap很繁琐，使用这个插件可以提高编程效率，在什么场景下使用能充分体现它的价值。
    - 缺点：
    
