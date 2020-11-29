package com.nowcoder.work.community.dao.elasticsearch;

import com.nowcoder.work.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostReposity extends ElasticsearchRepository<DiscussPost, Integer> {
}
