package com.zzf.Flowable.test;

import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test001 {

    /**
     * 获取流引擎对象
     */
    @Test
    public void testProcessEngine() {
        //配置信息
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        //配置数据库连接信息
        processEngineConfiguration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("zzf5201314");
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable-demo?serverTimezone=UTC&nullCatalogMeansCurrent=true");
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        //构建processEngine对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        System.out.println("processEngine: " + processEngine);
    }

    ProcessEngineConfiguration processEngineConfiguration = null;

    @Before
    public void before() {
        processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        //配置数据库连接信息
        processEngineConfiguration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("zzf5201314");
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable-demo?serverTimezone=UTC&nullCatalogMeansCurrent=true");
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);


    }

    /**
     * 部署流程
     */
    @Test
    public void testDeploy() {
        //构建processEngine对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("holiday-request.bpmn20.xml")
                .name("请求流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * 查询流程
     */
    @Test
    public void testDeployQuery() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
//
//        Deployment deployment = deploymentQuery.deploymentId("1").singleResult();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("holidayRequest").singleResult();

        System.out.println(processDefinition.getName());
        System.out.println(processDefinition.getId());
        System.out.println(processDefinition.getDescription());
        System.out.println(processDefinition.getDeploymentId());
    }

    /**
     * 删除流程
     */
    @Test
    public void testDeleteDeploy() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //删除部署的流程 参数是id 若流程启动了则不允许删除，除非用下面的删除
//        repositoryService.deleteDeployment("1");
        //第二个参数是关联删除相关 流程相关的任务
        repositoryService.deleteDeployment("5001", true);
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testRunProcess() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "张三");
        variables.put("holiday", 5);
        variables.put("desciption", "工作累了，出去玩玩！");

        ProcessInstance holidayRequest = runtimeService.startProcessInstanceByKey("holidayRequest", variables);

        System.out.println(holidayRequest.getProcessDefinitionId());
        System.out.println(holidayRequest.getActivityId());
        System.out.println(holidayRequest.getId());

    }

    /**
     * 测试任务查询
     */
    @Test
    public void testQueryTask() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().processDefinitionKey("holidayRequest")
                .taskAssignee("zhangsan")
                .list();

        for (Task test : list) {
            System.out.println("test.getId(): " + test.getId());
            System.out.println("test.getDescription(): " + test.getDescription());
            System.out.println("test.getName(): " + test.getName());
            System.out.println("test.getAssignee(): " + test.getAssignee());
            System.out.println("test.getProcessDefinitionId(): " + test.getProcessDefinitionId());
        }
    }


    /**
     * 完成任务
     */
    @Test
    public void testCompleteTask() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("holidayRequest")
                .taskAssignee("zhangsan")
                .singleResult();

        //创建xml里的流程变量
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("approved", false);

        //完成任务
        taskService.complete(task.getId(), map);
    }

    /**
     * 测试
     */
    @Test
    public void testHistory() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        HistoryService historyService = processEngine.getHistoryService();

        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processDefinitionId("holidayRequest:1:12503")
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for (HistoricActivityInstance history : list) {
            System.out.println(history.getActivityName() + " : " + history.getAssignee()
                    + "---" + history.getActivityId() + ": " + history.getDurationInMillis()+ "毫秒");
        }

    }
}
