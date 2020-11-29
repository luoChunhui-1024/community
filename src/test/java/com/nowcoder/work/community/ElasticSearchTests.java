package com.nowcoder.work.community;

import com.nowcoder.work.community.dao.DiscussPostMapper;
import com.nowcoder.work.community.dao.elasticsearch.DiscussPostReposity;
import com.nowcoder.work.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostReposity discussReposity;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;   // 注意不要注入ElasticsearchTemplate

//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInsert(){
        discussReposity.save(discussPostMapper.selectDiscussPostById(288));
        discussReposity.save(discussPostMapper.selectDiscussPostById(289));
        discussReposity.save(discussPostMapper.selectDiscussPostById(290));
    }

    @Test
    public void testInsertList(){
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(138, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(145, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(157, 0, 100, 0));
        discussReposity.saveAll(discussPostMapper.selectDiscussPosts(156, 0, 100, 0));

    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水");
        discussReposity.save(post);
    }

    @Test
    public void testDelete(){
        discussReposity.deleteAll();
    }

    @Test
    public void testSearchByRepository(){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(20, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        Page<DiscussPost> page = discussReposity.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for(DiscussPost post : page){
            System.out.println(post);
        }
    }

    @Test
    public void testSearchByTemplate1(){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬要来", "title", "content"))
//                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
//                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                 .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        //elasticsearchTemplate.queryForPage()
        //elasticsearchRestTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchRe)

        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        // 得到查询结果返回的内容
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        // 设置一个需要返回的实体类集合
        List<DiscussPost> discussPosts = new ArrayList<>();
        // 遍历返回的内容进行处理
        for(SearchHit<DiscussPost> searchHit : searchHits){
            // 高亮的内容
            Map<String, List<String>> highLightFields = searchHit.getHighlightFields();
            // 将高亮的内容填充到content中
            searchHit.getContent().setTitle(highLightFields.get("title") == null ? searchHit.getContent().getTitle() : highLightFields.get("title").get(0));
            searchHit.getContent().setTitle(highLightFields.get("content") == null ? searchHit.getContent().getContent() : highLightFields.get("content").get(0));
            // 放到实体类中
            discussPosts.add(searchHit.getContent());
        }
        System.out.println(search.getTotalHits());
        System.out.println(discussPosts.size());
        for(DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }
    }

//    @Test
//    public void testSearchByTemplate(){
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
//                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
//                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
//                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
//                .withPageable(PageRequest.of(0, 10))
//                .withHighlightFields(
//                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
//                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
//                ).build();
//
//        elasticsearchRestTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper())
//
//    }

}
