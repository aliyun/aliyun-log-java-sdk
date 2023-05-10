package com.aliyun.openservices.log;

import static org.junit.Assert.assertEquals;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Topostore;
import com.aliyun.openservices.log.common.TopostoreNode;
import com.aliyun.openservices.log.common.TopostoreRelation;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateTopostoreNodeRequest;
import com.aliyun.openservices.log.request.CreateTopostoreRelationRequest;
import com.aliyun.openservices.log.request.CreateTopostoreRequest;
import com.aliyun.openservices.log.request.DeleteTopostoreNodeRequest;
import com.aliyun.openservices.log.request.DeleteTopostoreRelationRequest;
import com.aliyun.openservices.log.request.DeleteTopostoreRequest;
import com.aliyun.openservices.log.request.ListTopostoreNodeRelationRequest;
import com.aliyun.openservices.log.request.ListTopostoreNodeRelationResponse;
import com.aliyun.openservices.log.request.GetTopostoreNodeRequest;
import com.aliyun.openservices.log.request.GetTopostoreRelationRequest;
import com.aliyun.openservices.log.request.GetTopostoreRequest;
import com.aliyun.openservices.log.request.ListTopostoreNodeRequest;
import com.aliyun.openservices.log.request.ListTopostoreRelationRequest;
import com.aliyun.openservices.log.request.ListTopostoreRequest;
import com.aliyun.openservices.log.request.UpdateTopostoreNodeRequest;
import com.aliyun.openservices.log.request.UpdateTopostoreRelationRequest;
import com.aliyun.openservices.log.request.UpdateTopostoreRequest;
import com.aliyun.openservices.log.request.UpsertTopostoreNodeRequest;
import com.aliyun.openservices.log.request.UpsertTopostoreRelationRequest;
import com.aliyun.openservices.log.response.CreateTopostoreNodeResponse;
import com.aliyun.openservices.log.response.CreateTopostoreRelationResponse;
import com.aliyun.openservices.log.response.CreateTopostoreResponse;
import com.aliyun.openservices.log.response.DeleteTopostoreNodeResponse;
import com.aliyun.openservices.log.response.DeleteTopostoreRelationResponse;
import com.aliyun.openservices.log.response.DeleteTopostoreResponse;
import com.aliyun.openservices.log.response.GetTopostoreNodeResponse;
import com.aliyun.openservices.log.response.GetTopostoreRelationResponse;
import com.aliyun.openservices.log.response.GetTopostoreResponse;
import com.aliyun.openservices.log.response.ListTopostoreNodeResponse;
import com.aliyun.openservices.log.response.ListTopostoreRelationResponse;
import com.aliyun.openservices.log.response.ListTopostoreResponse;
import com.aliyun.openservices.log.response.UpdateTopostoreNodeResponse;
import com.aliyun.openservices.log.response.UpdateTopostoreRelationResponse;
import com.aliyun.openservices.log.response.UpdateTopostoreResponse;
import com.aliyun.openservices.log.response.UpsertTopostoreNodeResponse;
import com.aliyun.openservices.log.response.UpsertTopostoreRelationResponse;

import org.junit.Test;

public class ClientTopostoreTest {
    @Test
    public void testCreateTopostore() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        Topostore topostore = new Topostore("hello_topostore");
        Map<String, String> tag = new HashMap<String,String>();
        tag.put("hello", "world");
        topostore.setTag(tag);
        CreateTopostoreRequest request = new CreateTopostoreRequest(topostore);
        CreateTopostoreResponse response = client.createTopostore(request);
        System.out.println(response.toString());
    }

    @Test
    public void testUpdateTopostore() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        Topostore topostore = new Topostore("hello_topostore");
        Map<String, String> tag = new HashMap<String,String>();
        tag.put("hello", "world22");
        topostore.setTag(tag);
        topostore.setDescription("this is a desc");
        UpdateTopostoreRequest request = new UpdateTopostoreRequest(topostore);
        UpdateTopostoreResponse response = client.updateTopostore(request);
        System.out.println(response.toString());
    }

    @Test
    public void testGetTopostore() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        GetTopostoreRequest request = new GetTopostoreRequest("hello_topostore");
        GetTopostoreResponse response = client.getTopostore(request);
        System.out.println(response.getTopostore().ToJsonString());
    }

    @Test
    public void testListTopostore() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        ListTopostoreRequest request = new ListTopostoreRequest();
        ListTopostoreResponse response = client.listTopostore(request);

        for(int i=0; i<response.getCount(); i++){
            System.out.println(response.getTopostores().get(i).ToJsonObject());
        }

        System.out.println("filter with tag");

        ListTopostoreRequest request2 = new ListTopostoreRequest();
        Map<String, String> tags = new HashMap<String,String>();
        tags.put("hello", "world22");
        request2.setTags(tags);

        ListTopostoreResponse response2 = client.listTopostore(request2);

        for(int i=0; i<response2.getCount(); i++){
            System.out.println(response2.getTopostores().get(i).ToJsonObject());
        }

    }

    @Test
    public void testDeleteTopostore() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        DeleteTopostoreRequest request = new DeleteTopostoreRequest("hello_topostore");
        DeleteTopostoreResponse response = client.deleteTopostore(request);
        System.out.println(response.toString());
    }

    @Test
    public void testCreateTopostoreNode() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        TopostoreNode topostoreNode = new TopostoreNode("machine1", "server");
        CreateTopostoreNodeRequest request = new CreateTopostoreNodeRequest("hello_topostore",topostoreNode);
        CreateTopostoreNodeResponse response = client.createTopostoreNode(request);
        System.out.println(response.toString());
    }

    @Test
    public void testUpsertTopostoreNode() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        TopostoreNode topostoreNode = new TopostoreNode("machine1", "server");
        topostoreNode.setDisplayName("login_server");
        topostoreNode.setDescription("aliyun ecs server");

        List<TopostoreNode> nodes = new ArrayList<TopostoreNode>();
        nodes.add(topostoreNode);

        UpsertTopostoreNodeRequest request = new UpsertTopostoreNodeRequest("hello_topostore", nodes);
        UpsertTopostoreNodeResponse response = client.upsertTopostoreNode(request);
        System.out.println(response.toString());
    }


    @Test
    public void testUpdateTopostoreNode() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        TopostoreNode topostoreNode = new TopostoreNode("machine1", "server");
        topostoreNode.setDisplayName("login_server");
        UpdateTopostoreNodeRequest request = new UpdateTopostoreNodeRequest("hello_topostore",topostoreNode);
        UpdateTopostoreNodeResponse response = client.updateTopostoreNode(request);
        System.out.println(response.toString());
    }

    @Test
    public void testGetTopostoreNode() throws LogException{
        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        GetTopostoreNodeRequest request = new GetTopostoreNodeRequest("hello_topostore","machine1");
        GetTopostoreNodeResponse response = client.getTopostoreNode(request);
        System.out.println(response.getTopostoreNode().ToJsonString());
    }

    @Test
    public void testDeleteTopostoreNode() throws LogException{
        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        List<String> nodeIds = new ArrayList<String>();
        nodeIds.add("machine1");
        DeleteTopostoreNodeRequest request = new DeleteTopostoreNodeRequest("hello_topostore",nodeIds);
        DeleteTopostoreNodeResponse response = client.deleteTopostoreNode(request);
        System.out.println(response.toString());
    }

    @Test
    public void testCreateTopostoreRelation() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        String topostoreName = "hello_topostore";
        // prepare nodes
        List<TopostoreNode> nodes = new ArrayList<TopostoreNode>();
        TopostoreNode topostoreNode1 = new TopostoreNode("machine1", "server");
        topostoreNode1.setDisplayName("front server1");
        topostoreNode1.setDescription("aliyun ecs server");
        nodes.add(topostoreNode1);

        TopostoreNode topostoreNode2 = new TopostoreNode("machine2", "server");
        topostoreNode2.setDisplayName("front server2");
        topostoreNode2.setDescription("aliyun ecs server");
        nodes.add(topostoreNode2);


        TopostoreNode topostoreNode3 = new TopostoreNode("login", "server");
        topostoreNode3.setDisplayName("login service");
        nodes.add(topostoreNode3);

        UpsertTopostoreNodeRequest nodeReq = new UpsertTopostoreNodeRequest(topostoreName, nodes);
        UpsertTopostoreNodeResponse nodeResp = client.upsertTopostoreNode(nodeReq);
        System.out.println(nodeResp.toString());


        // relation op
        TopostoreRelation relation = new TopostoreRelation();
        relation.setRelationId("relation1");
        relation.setRelationType("depend_on");
        relation.setSrcNodeId("login");
        relation.setDstNodeId("machine1");

        CreateTopostoreRelationRequest request = new CreateTopostoreRelationRequest(topostoreName, relation);
        CreateTopostoreRelationResponse response = client.createTopostoreRelation(request);
        System.out.println(response.toString());

    }

    @Test
    public void testUpdateTopostoreRelation() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        String topostoreName = "hello_topostore";

        // relation op
        TopostoreRelation relation = new TopostoreRelation();
        relation.setRelationId("relation1");
        relation.setRelationType("depend_on");
        relation.setSrcNodeId("login");
        relation.setDstNodeId("machine1");
        relation.setDescription("login depend on machine1");

        UpdateTopostoreRelationRequest request = new UpdateTopostoreRelationRequest(topostoreName, relation);
        UpdateTopostoreRelationResponse response = client.updateTopostoreRelation(request);
        System.out.println(response.toString());

    }

    @Test
    public void testUpsertTopostoreRelation() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        String topostoreName = "hello_topostore";

        // relation op
        TopostoreRelation relation1 = new TopostoreRelation();
        relation1.setRelationId("relation1");
        relation1.setRelationType("depend_on");
        relation1.setSrcNodeId("login");
        relation1.setDstNodeId("machine1");
        relation1.setDescription("login depend on machine1");

        TopostoreRelation relation2 = new TopostoreRelation();
        relation2.setRelationId("relation2");
        relation2.setRelationType("depend_on");
        relation2.setSrcNodeId("login");
        relation2.setDstNodeId("machine2");
        relation2.setDescription("login depend on machine1");

        List<TopostoreRelation> relations = new ArrayList<TopostoreRelation>();
        relations.add(relation1);
        relations.add(relation2);

        UpsertTopostoreRelationRequest request = new UpsertTopostoreRelationRequest(topostoreName, relations);
        UpsertTopostoreRelationResponse response = client.upsertTopostoreRelation(request);
        System.out.println(response.toString());

    }

    @Test
    public void testGetTopostoreRelation() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        String topostoreName = "hello_topostore";

        // relation op
        GetTopostoreRelationRequest request = new GetTopostoreRelationRequest(topostoreName, "relation1");
        GetTopostoreRelationResponse response = client.getTopostoreRelation(request);
        System.out.println(response.getTopostoreRelation().ToJsonString());
    }


    @Test
    public void testListTopostoreRelation() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        String topostoreName = "hello_topostore";

        // relation op
        ListTopostoreRelationRequest request = new ListTopostoreRelationRequest(topostoreName);
        ListTopostoreRelationResponse response = client.listTopostoreRelation(request);
        for(TopostoreRelation r :response.getTopostoreRelations()){
            System.out.println(r.ToJsonString());
        }
    }


    @Test
    public void testDeleteTopostoreRelation() throws LogException{

        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        String topostoreName = "hello_topostore";

        // relation op
        List<String> relationIds = new ArrayList<String>();
        relationIds.add("relation1");
        relationIds.add("relation2");

        DeleteTopostoreRelationRequest request = new DeleteTopostoreRelationRequest(topostoreName, relationIds);
        DeleteTopostoreRelationResponse response = client.deleteTopostoreRelation(request);
        System.out.println(response.toString());
    }

    @Test
    public void testGetTopostoreProperities() throws LogException{
        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        ListTopostoreRequest tReq = new ListTopostoreRequest();

        tReq.SetParam("key", "value");

        Map<String, String> tags = new HashMap<String, String>();
        tags.put("a-1", "b-1");
        tags.put("a-2", "b-2");

        tReq.setTags(tags);
        client.listTopostore(tReq);

        // test relation list
        ListTopostoreRelationRequest req = new ListTopostoreRelationRequest();

        req.setTopostoreName("sls");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("env", "prod");
        req.setProperties(properties);

        ListTopostoreRelationResponse resp = client.listTopostoreRelation(req);

        for(TopostoreRelation relation:resp.getTopostoreRelations()        ){
            System.out.println(relation.ToJsonString());
        }

        // test node list
        ListTopostoreNodeRequest req2 = new ListTopostoreNodeRequest();
        req2.setTopostoreName("sls");

        Map<String, String> properties2 = new HashMap<String, String>();
        properties2.put("env", "prod");
        properties2.put("name", "host1");
        req2.setProperties(properties2);

        ListTopostoreNodeResponse resp2 = client.listTopostoreNode(req2);

        for(TopostoreNode node:resp2.getTopostoreNodes()){
            System.out.println(node.ToJsonString());
        }

    }

    private void prepareTopoForquery(String topoName) throws LogException{
        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        // delete topostore if exists
        DeleteTopostoreRequest delReq = new DeleteTopostoreRequest(topoName);
        try{
            client.deleteTopostore(delReq);
        } catch(Exception ex){

        }

        // create topostore
        Topostore webservice = new Topostore(topoName);
        CreateTopostoreRequest createReq = new CreateTopostoreRequest(webservice);
        client.createTopostore(createReq);

        // Service
        List<String> services = new ArrayList<String>();
        services.add("Web");
        services.add("Search");
        services.add("Order");

        List<TopostoreNode> nodes = new ArrayList<TopostoreNode>();
        for(String e: services){
            TopostoreNode x = new TopostoreNode();
            x.setNodeId(e);
            x.setDisplayName(e);
            x.setNodeType("service");
            Map<String, String> properties = new HashMap<String,String>();
            properties.put("name", e);
            x.setProperty(properties);
            nodes.add(x);
        }

        // Database
        List<String> dbs = new ArrayList<String>();
        dbs.add("ElasticSearch");
        dbs.add("MySQL");
        for(String e: dbs){
            TopostoreNode x = new TopostoreNode();
            x.setNodeId(e);
            x.setDisplayName(e);
            x.setNodeType("database");
            Map<String, String> properties = new HashMap<String,String>();
            properties.put("name", e);
            x.setProperty(properties);
            nodes.add(x);
        }

        // Host
        List<String> hosts = new ArrayList<String>();
        hosts.add("host1");
        hosts.add("host2");
        hosts.add("host3");
        hosts.add("host4");
        hosts.add("host5");
        for(String e: hosts){
            TopostoreNode x = new TopostoreNode();
            x.setNodeId(e);
            x.setDisplayName(e);
            x.setNodeType("machine");
            Map<String, String> properties = new HashMap<String,String>();
            properties.put("name", e);
            x.setProperty(properties);
            nodes.add(x);
        }

        UpsertTopostoreNodeRequest upsertNodes = new UpsertTopostoreNodeRequest(topoName, nodes);
        client.upsertTopostoreNode(upsertNodes);


        // for relations
        List<List<String>> relationLists = new ArrayList<List<String>>();
        relationLists.add(Arrays.asList("Web","host1", "run_on"));
        relationLists.add(Arrays.asList("Search","host2", "run_on"));
        relationLists.add(Arrays.asList("Order","host3", "run_on"));
        relationLists.add(Arrays.asList("ElasticSearch","host4", "run_on"));
        relationLists.add(Arrays.asList("MySQL","host5", "run_on"));
        relationLists.add(Arrays.asList("Web","Search", "api_call"));
        relationLists.add(Arrays.asList("Web","Order", "rpc_call"));
        relationLists.add(Arrays.asList("Order","MySQL", "db_query"));
        relationLists.add(Arrays.asList("Search","ElasticSearch", "api_call"));

        List<TopostoreRelation> relations = new ArrayList<TopostoreRelation>();
        for(List<String> relation: relationLists){
            TopostoreRelation r = new TopostoreRelation();
            r.setRelationId(relation.get(0)+"-"+relation.get(1));
            r.setSrcNodeId(relation.get(0));
            r.setDstNodeId(relation.get(1));
            r.setRelationType(relation.get(2));
            relations.add(r);
        }

        UpsertTopostoreRelationRequest upertRelations = new UpsertTopostoreRelationRequest(topoName, relations);
        client.upsertTopostoreRelation(upertRelations);
    }


    @Test
    public void testListTopostoreNodeRelationsWithCycle() throws LogException{
        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        String topoName = "topoCycle";

        // delete topostore if exists
        DeleteTopostoreRequest delReq = new DeleteTopostoreRequest(topoName);
        try{
            client.deleteTopostore(delReq);
        } catch(Exception ex){

        }

        // create topostore
        Topostore webservice = new Topostore(topoName);
        CreateTopostoreRequest createReq = new CreateTopostoreRequest(webservice);
        client.createTopostore(createReq);

        List<TopostoreNode> nodes = new ArrayList<TopostoreNode>();

        TopostoreNode n1 = new TopostoreNode();
        n1.setNodeId("AAA");
        n1.setDisplayName("AAA");
        n1.setNodeType("service");
        nodes.add(n1);

        TopostoreNode n2 = new TopostoreNode();
        n2.setNodeId("BBB");
        n2.setDisplayName("BBB");
        n2.setNodeType("service");
        nodes.add(n2);

        TopostoreNode n3 = new TopostoreNode();
        n3.setNodeId("CCC");
        n3.setDisplayName("CCC");
        n3.setNodeType("service");
        nodes.add(n3);

        TopostoreNode n4 = new TopostoreNode();
        n4.setNodeId("DDD");
        n4.setDisplayName("DDD");
        n4.setNodeType("service");
        nodes.add(n4);

        UpsertTopostoreNodeRequest upsertNodes = new UpsertTopostoreNodeRequest(topoName, nodes);
        client.upsertTopostoreNode(upsertNodes);

        List<TopostoreRelation> relations = new ArrayList<TopostoreRelation>();

        TopostoreRelation r1 = new TopostoreRelation();
        r1.setRelationId("AAA-BBB");
        r1.setSrcNodeId("AAA");
        r1.setDstNodeId("BBB");
        r1.setRelationType("depend");
        relations.add(r1);

        TopostoreRelation r2 = new TopostoreRelation();
        r2.setRelationId("BBB-AAA");
        r2.setSrcNodeId("BBB");
        r2.setDstNodeId("AAA");
        r2.setRelationType("depend");
        relations.add(r2);

        TopostoreRelation r3 = new TopostoreRelation();
        r3.setRelationId("BBB-AAA");
        r3.setSrcNodeId("BBB");
        r3.setDstNodeId("AAA");
        r3.setRelationType("depend");
        relations.add(r3);

        TopostoreRelation r4 = new TopostoreRelation();
        r4.setRelationId("BBB-CCC");
        r4.setSrcNodeId("BBB");
        r4.setDstNodeId("CCC");
        r4.setRelationType("depend");
        relations.add(r4);

        TopostoreRelation r5 = new TopostoreRelation();
        r5.setRelationId("CCC-BBB");
        r5.setSrcNodeId("CCC");
        r5.setDstNodeId("BBB");
        r5.setRelationType("depend");
        relations.add(r5);


        TopostoreRelation r6 = new TopostoreRelation();
        r6.setRelationId("CCC-AAA");
        r6.setSrcNodeId("CCC");
        r6.setDstNodeId("AAA");
        r6.setRelationType("depend");
        relations.add(r6);

        TopostoreRelation r7 = new TopostoreRelation();
        r7.setRelationId("AAA-DDD");
        r7.setSrcNodeId("AAA");
        r7.setDstNodeId("DDD");
        r7.setRelationType("depend");
        relations.add(r7);

        UpsertTopostoreRelationRequest upertRelations = new UpsertTopostoreRelationRequest(topoName, relations);
        client.upsertTopostoreRelation(upertRelations);

        ListTopostoreNodeRelationRequest request = new ListTopostoreNodeRelationRequest();
        request.setTopostoreName(topoName);
        request.setDirection(Consts.TOPOSTORE_RELATION_DIRECTION_OUT);
        request.setDepth(-1);
        List<String> nodeIds = new ArrayList<String>();
        nodeIds.add("CCC");
        request.setNodeIds(nodeIds);

        ListTopostoreNodeRelationResponse resp = client.listTopostoreNodeRelations(request);
        // System.out.println(JSONObject.toJSONString(resp));
        listNodeRelationResultCheck(resp, Arrays.asList("AAA", "BBB", "CCC", "DDD"), 
                Arrays.asList("AAA-BBB", "BBB-AAA", "BBB-CCC", "CCC-BBB", "CCC-AAA","AAA-DDD"));
        // System.out.println("---------------------------");
        // System.out.println("nodes:");
        // for(TopostoreNode node: resp.getNodes()){
        //     System.out.println("\t" + node.getNodeId());
        // }
        // System.out.println("\nrelations:");
        // for(TopostoreRelation relation: resp.getRelations()){
        //     System.out.println("\t" + relation.getRelationId());
        // }
    }


    @Test
    public void testListTopostoreNodeRelations() throws LogException{
        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        String topoName = "webservice";
        prepareTopoForquery(topoName);

        // case 1
        ListTopostoreNodeRelationRequest request1 = new ListTopostoreNodeRelationRequest();
        request1.setTopostoreName(topoName);

        ListTopostoreNodeRelationResponse resp = client.listTopostoreNodeRelations(request1);
        listNodeRelationResultCheck(resp, Arrays.asList("ElasticSearch", "host1", "host2", "host3", "host4", "host5", "MySQL", "Order", "Search", "Web"), 
                new ArrayList<String>());

        // case 2
        ListTopostoreNodeRelationRequest request2 = new ListTopostoreNodeRelationRequest();
        request2.setTopostoreName(topoName);
        request2.setDirection(Consts.TOPOSTORE_RELATION_DIRECTION_IN);
        request2.setDepth(2);
        List<String> nodeIds = new ArrayList<String>();
        nodeIds.add("host3");
        request2.setNodeIds(nodeIds);

        resp = client.listTopostoreNodeRelations(request2);
        listNodeRelationResultCheck(resp, Arrays.asList("host3", "Order", "Web"), Arrays.asList("Order-host3", "Web-Order"));
        
        // case 3
        ListTopostoreNodeRelationRequest request3 = new ListTopostoreNodeRelationRequest();
        request3.setTopostoreName(topoName);
        request3.setDirection(Consts.TOPOSTORE_RELATION_DIRECTION_OUT);
        request3.setDepth(2);
        List<String> nodeIds2 = new ArrayList<String>();
        nodeIds2.add("Search");
        request3.setNodeIds(nodeIds2);

        resp = client.listTopostoreNodeRelations(request3);
        listNodeRelationResultCheck(resp, Arrays.asList("Search", "host2", "ElasticSearch", "host4"), 
            Arrays.asList("Search-host2", "Search-ElasticSearch", "ElasticSearch-host4"));


        // case 4
        ListTopostoreNodeRelationRequest request4 = new ListTopostoreNodeRelationRequest();
        request4.setTopostoreName(topoName);
        request4.setDirection(Consts.TOPOSTORE_RELATION_DIRECTION_OUT);
        request4.setDepth(2);
        request4.addNodeProperty("name", "Search");

        resp = client.listTopostoreNodeRelations(request4);
        listNodeRelationResultCheck(resp, Arrays.asList("Search", "host2", "ElasticSearch", "host4"), 
            Arrays.asList("Search-host2", "Search-ElasticSearch", "ElasticSearch-host4"));

        // case 5
        ListTopostoreNodeRelationRequest request5 = new ListTopostoreNodeRelationRequest();
        request5.setTopostoreName(topoName);
        request5.setDirection(Consts.TOPOSTORE_RELATION_DIRECTION_OUT);
        request5.setDepth(1);
        request5.addNodeProperty("name", "Search");

        resp = client.listTopostoreNodeRelations(request5);
        listNodeRelationResultCheck(resp, Arrays.asList("Search", "host2", "ElasticSearch"), 
            Arrays.asList("Search-host2", "Search-ElasticSearch"));

        // case 6
        ListTopostoreNodeRelationRequest request6 = new ListTopostoreNodeRelationRequest();
        request6.setTopostoreName(topoName);
        request6.setDirection(Consts.TOPOSTORE_RELATION_DIRECTION_BOTH);
        request6.setDepth(1);
        request6.addNodeProperty("name", "Search");

        resp = client.listTopostoreNodeRelations(request6);
        listNodeRelationResultCheck(resp, Arrays.asList("Search", "host2", "ElasticSearch", "Web"), 
            Arrays.asList("Search-host2", "Search-ElasticSearch","Web-Search"));

        // case 7
        ListTopostoreNodeRelationRequest request7 = new ListTopostoreNodeRelationRequest();
        request7.setTopostoreName(topoName);
        request7.setDirection(Consts.TOPOSTORE_RELATION_DIRECTION_BOTH);
        request7.setDepth(1);
        request7.addNodeProperty("name", "");

        resp = client.listTopostoreNodeRelations(request7);
        listNodeRelationResultCheck(resp, new ArrayList<String>(), new ArrayList<String>());
    }

    @Test
    public void testListTopostoreNodeRelations222() throws LogException{
        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");

        Client client = new Client(endpoint, accessKeyId, accessKeySecret);
        String  s = "        {\"depth\":1,\"direction\":\"both\",\"from\":0,\"nodeIds\":[\"sls-mall_trace-type_sls-mall\"],\"nodeProperities\":{},\"nodeTypes\":[],\"to\":0,\"topostoreName\":\"SLS_DEFAULT_DATA_EXPRESSION\"}\n";

        ListTopostoreNodeRelationRequest req = JSONObject.parseObject(s, ListTopostoreNodeRelationRequest.class);
        ListTopostoreNodeRelationResponse res = client.listTopostoreNodeRelations(req);

        System.out.println(JSON.toJSONString(res.getRelations()));
    }

    private void listNodeRelationResultCheck(ListTopostoreNodeRelationResponse resp, List<String> expectNodes, List<String> expectRelations){
        System.out.println("---------------------------");
        System.out.println("nodes:");
        if(expectNodes!=null){
            assertEquals(expectNodes.size(), resp.getNodes().size());
        }
        for(TopostoreNode node: resp.getNodes()){
            System.out.println("\t" + node.getNodeId());
            if(expectNodes!=null){
                assertEquals(true, expectNodes.contains(node.getNodeId()));
            }
        }
        System.out.println("\nrelations:");
        if(expectRelations!=null){
            assertEquals(expectRelations.size(), resp.getRelations().size());
        }
        for(TopostoreRelation relation: resp.getRelations()){
            System.out.println("\t" + relation.getRelationId());
            if(expectRelations!=null){
                assertEquals(true, expectRelations.contains(relation.getRelationId()));
            }
        }
    }
  
}
