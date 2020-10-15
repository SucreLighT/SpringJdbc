package cn.sucrelt.jdbctemplate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author sucre
 * spring中使用jdbc
 */
public class JdbcTemplateDemo2 {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean.xml");
        JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);

        jdbcTemplate.execute("insert into account(name,money)values('ddd',1000)");

    }
}
