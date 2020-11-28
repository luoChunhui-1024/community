package com.nowcoder.work.community.actuator;

import com.nowcoder.work.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")
public class DataBaseEndPoint {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseEndPoint.class);

    @Autowired
    private DataSource dataSource;

    public String checkConnection(){
        try (Connection conn = dataSource.getConnection()) {
            return CommunityUtil.getJSONString(0, "获取连接成功！");
        }catch (SQLException e){
            logger.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJSONString(0, "获取连接失败！");
        }
    }
}
