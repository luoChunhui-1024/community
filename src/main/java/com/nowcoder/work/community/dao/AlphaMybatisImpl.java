package com.nowcoder.work.community.dao;

import org.springframework.stereotype.Repository;

@Repository("mybatis")
public class AlphaMybatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "myBatis";
    }
}
