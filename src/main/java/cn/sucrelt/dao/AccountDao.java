package cn.sucrelt.dao;

import cn.sucrelt.domain.Account;

public interface AccountDao {
    Account findAccountById(Integer accountId);

    Account findAccountByName(String accountName);

    void updateAccount(Account account);
}
