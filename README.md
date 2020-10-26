JavaWeb学习中关于Spring-AOP的相关知识 💯💤



## Spring中的JdbcTemplate

spring框架中提供的对关系型数据库的操作模板类，在使用时需要导入`spring-jdbc-5.0.2.RELEASE.jar`以及与事务相关的`spring-tx-5.0.2.RELEASE.jar`。

### 基本使用

1. 在spring容器中配置数据源

   ```xml
   <!-- 配置一个数据库的操作模板：JdbcTemplate -->
   <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
       <property name="dataSource" ref="dataSource"/>
   </bean>
   
   <!-- 配置数据源 -->
   <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
       <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
       <property name="url"
                 value="jdbc:mysql://localhost:3306/spring?useUnicode=true&amp;characterEncoding=utf8"/>
       <property name="username" value="root"/>
       <property name="password" value="123456"/>
   </bean>
   ```

2. 从spring容器中获取对象

   ```java
   //1.获取容器
   ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
   //2.获取对象
   JdbcTemplate jt = ac.getBean("jdbcTemplate", JdbcTemplate.class);
   ```

3. 执行基本的增删改查操作

   ```java
   //3.执行操作
   //保存
   jt.update("insert into account(name,money)values(?,?)", "eee", 3333f);
   //删除
   jt.update("delete from account where id=?", 2);
   //更新
   jt.update("update account set name=?,money=? where id=?", "test", 4567, 4);
   
   // 查询所有
   List<Account> accounts = jt.query("select * from account where money > ?", new BeanPropertyRowMapper<Account>(Account.class), 1000f);
   for (Account account : accounts) {
       System.out.println(account);
   }
   // 查询一个
   List<Account> account = jt.query("select * from account where id = ?", new BeanPropertyRowMapper<Account>(Account.class), 1);
   System.out.println(account.isEmpty() ? "没有内容" : account.get(0));
   
   // 查询返回一行一列（使用聚合函数，但不加group by子句）
   Long count = jt.queryForObject("select count(*) from account where money > ?", Long.class, 1000f);
   System.out.println(count);
   ```

4. query()方法，在spring的JdbcTemplate中，查询一个和多个均可使用该方法

   参数：

   - String sql：sql语句。
   - RowMapper rowMapper：用于封装查询结果集的`BeanPropertyRowMapper<T>(java.lang.Class<T> mappedClass)`，泛型为对应的实体类，参数为实体类的字节码。
   - Object… args：sql语句中的参数。

5. queryForObject(String sql, Class requiredType, Object… args)方法

   参数：

   - String sql：sql语句。
   - Class requiredType：用于指定需要返回的结果类型。
   - Object… args：sql语句中的参数。

### 整合到Dao中

#### 1.在dao中定义JdbcTemplate

dao的实现类中定义成员变量JdbcTemplate，提供相应的setter方法，并需要给该类注入JdbcTemplate的值。

```
public class AccountDaoImpl implements AccountDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Account findAccountById(Integer accountId) {
        List<Account> accounts = jdbcTemplate.query("select * from account where id = ?",
                new BeanPropertyRowMapper<Account>(Account.class), accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }
}
```

bean.xml中在spring容器中定义该类

```xml
<bean id="accountDao" class="cn.sucrelt.dao.impl.AccountDaoImpl">
    <property name="jdbcTemplate" ref="jdbcTemplate"/>
</bean>
```

此方式的问题在于当有多个dao时，每个dao中都需要定义JdbcTemplate并进行注入，存在大量的重复代码。

#### 2.dao继承JdbcDaoSupport

定义dao类继承spring中的JdbcDaoSupport类，该类对数据源自动进行注入并创建JdbcTemplate，创建该类对象时只需要为其提供一个数据源即可。

当dao类继承该类时，在注入dao时需要提供数据源变量的值，并且直接通过从JdbcDaoSupport中继承来的getJdbcTemplate方法直接获取JdbcTemplate并使用即可。

```java
public class AccountDaoImpl2 extends JdbcDaoSupport implements AccountDao {
    public Account findAccountById(Integer accountId) {
        List<Account> accounts = getJdbcTemplate().query("select * from account where id = ?",
                new BeanPropertyRowMapper<Account>(Account.class), accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }
}
```

bean.xml文件中提供数据源DataSource用于注入，虽然AccountDaoImpl2类本身中没有该变量，但是继承父类时，父类JdbcDaoSupport需要数据源进行初始化。

```xml
<bean id="accountDao2" class="cn.sucrelt.dao.impl.AccountDaoImpl2">
    <property name="dataSource" ref="dataSource"/>
</bean>
```

#### 3.对比

第一种方式在Dao类中定义JdbcTemplate的方式，适用于所有配置方式（xml和注解都可以）。

第二种让Dao继承JdbcDaoSupport的方式，只能用于基于XML的方式，注解用不了，因为注解配置时，无法在AccountDaoImpl2类中自动注入dataSource。



## Spring中的事务控制

### 基于xml的事务控制配置

#### 1.配置事务管理器

```xml
<!-- 配置事务管理器 -->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>	
```

#### 2.配置事务通知

1. 需要在bean文件开始导入事务的相关约束以及aop的约束

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd">
```

2. 使用`tx:advice`标签配置事务通知

   该标签的属性：

   + id：事务的唯一标识
   + transaction-manager：给事务通知中传入一个事务管理器

```xml
<!-- 配置事务的通知-->
<tx:advice id="transactionAdvice" transaction-manager="transactionManager">
    <!-- 配置事务的属性-->
    <tx:attributes>
        <tx:method name="*" propagation="REQUIRED" read-only="false"/>
        <tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
    </tx:attributes>
</tx:advice>
```

3. 在`tx:advice`标签内部使用`tx:attributes`配置事务属性
   + isolation：用于指定事务的隔离级别。默认值是DEFAULT，表示使用数据库的默认隔离级别。
   + propagation：用于指定事务的传播行为。默认值是REQUIRED，**用于增删改**，表示一定会有事务。**查询方法**可以选择SUPPORTS。
   + read-only：用于指定事务是否只读。**只有查询方法才能设置为true**。默认值是false，表示读写。
   + timeout：用于指定事务的超时时间，默认值是-1，表示永不超时。如果指定了数值，以秒为单位。
   + rollback-for：用于指定一个异常，当产生该异常时，事务回滚，产生其他异常时，事务不回滚。没有默认值。表示任何异常都回滚。
   + no-rollback-for：用于指定一个异常，当产生该异常时，事务不回滚，产生其他异常时事务回滚。没有默认值。表示任何异常都回滚。

#### 3.配置aop

```xml
<!-- 配置aop-->
<aop:config>
    <!-- 配置切入点表达式-->
    <aop:pointcut id="pt1" expression="execution(* cn.sucrelt.service.impl.*.*(..))"/>
    <!--建立切入点表达式和事务通知的对应关系
        表示在满足切入点表达式pointcut-ref时，执行通知advice-ref
        -->
    <aop:advisor advice-ref="transactionAdvice" pointcut-ref="pt1"/>
</aop:config>
```

