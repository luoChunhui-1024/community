package com.nowcoder.work.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "HebernateImpl";
    }
}
