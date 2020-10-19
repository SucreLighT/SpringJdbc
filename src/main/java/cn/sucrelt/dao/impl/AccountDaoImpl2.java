package cn.sucrelt.dao.impl;

import cn.sucrelt.dao.AccountDao;
import cn.sucrelt.domain.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;


/**
 * @author sucre
 * 继承JdbcDaoSupport类的Dao实现类，使用注解配置时此方式不适用
 * 因为其中的JdbcTemplate在其父类JdbcDaoSupport中设置，无法在该类中使用注解配置
 */
public class AccountDaoImpl2 extends JdbcDaoSupport implements AccountDao {
    public Account findAccountById(Integer accountId) {
        List<Account> accounts = getJdbcTemplate().query("select * from account where id = ?",
                new BeanPropertyRowMapper<Account>(Account.class), accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public Account findAccountByName(String accountName) {
        List<Account> accounts = getJdbcTemplate().query("select * from account where name = ?",
                new BeanPropertyRowMapper<Account>(Account.class), accountName);
        if (accounts.isEmpty()) {
            return null;
        }
        if (accounts.size() > 1) {
            throw new RuntimeException("结果不唯一");
        }
        return accounts.get(0);
    }

    public void updateAccount(Account account) {
        getJdbcTemplate().update("update account set name = ? ,money = ? where id=?", account.getName(), account.getMoney()
                , account.getId());
    }


}
