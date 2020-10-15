package cn.sucrelt.jdbctemplate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


/**
 * @author sucre
 * 传统jdbc执行方式
 */
public class JdbcTemplateDemo1 {
    public static void main(String[] args) {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/spring?useUnicode=true&characterEncoding=utf8");
        driverManagerDataSource.setUsername("root");
        driverManagerDataSource.setPassword("123456");

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(driverManagerDataSource);
            jdbcTemplate.execute("insert into account(name,money)values('ccc',1000)");
    }
}
