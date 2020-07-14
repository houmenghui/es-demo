package com.esdemo.frame;

import com.esdemo.modules.dao.AccessDao;
import com.esdemo.modules.service.EsDataMigrateService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-14 15:22
 */
@RunWith(SpringRunner.class)
//@SpringBootTest(classes = AgentApiApplication.class)
@SpringBootTest(properties = {"spring.profiles.active=local"})
public class TestEs {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private EsDataMigrateService esDataMigrate;
    @Resource
    private AccessDao accessDao;

    @Test
    public void test22() {
        int i = accessDao.canAccessTheAgent("0-1446-", "1446");
        System.out.println(i);
    }

    @Test
    public void test1() {
        BoolQueryBuilder must = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("type_name", "activity"))
                .must(QueryBuilders.matchPhrasePrefixQuery("agent_node", "0-1446-"))
                .must(QueryBuilders.termsQuery("team_id", "200010"));
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(must)
                .withPageable(PageRequest.of(0, 210))
                .build();
        List<String> merchant_no = elasticsearchTemplate.query(query, response ->
                Arrays.stream(response.getHits().getHits())
                        .map(hit -> Objects.toString(hit.getSourceAsMap().get("merchant_no")))
                        .collect(Collectors.toList())
        );
        merchant_no.forEach(System.out::println);
    }

    @Test
    public void testEsDataMigrate() throws InterruptedException {
        esDataMigrate.merchantMigrate("253311000001559", "1565");
        Thread.sleep(5000);
    }
}
