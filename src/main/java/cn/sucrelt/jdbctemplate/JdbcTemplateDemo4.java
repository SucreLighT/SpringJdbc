package cn.sucrelt.jdbctemplate;

import cn.sucrelt.dao.AccountDao;
import cn.sucrelt.domain.Account;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author sucre
 * spring中使用jdbc
 */
public class JdbcTemplateDemo4 {
    public static void main(String[] args) {
        //1.获取容器
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        //2.获取对象
        AccountDao accountDao = ac.getBean("accountDao", AccountDao.class);

        Account account = accountDao.findAccountById(1);

        System.out.println(account);
    }
}
