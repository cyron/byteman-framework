### What is Byteman Framework?
Byteman Framework is a distributed test helper library, just like dtest, and actually inspired by dtest.

### Why not dtest?
So why invent a new wheel?

* First, dtest only support test distributed systems locally, means it only starts jboss server on local machine.
(correct me if i'm wrong)<br>
Byteman Framework can start jboss server on remote machine via SSH.<br>
Just set the SSH user name and password on Linux, and cygwin or similar tool is requied on Windows.<br>

* Second, dtest uses ServerManager to start jboss server, it does not support EAP6 and the later version.<br>
Byteman Framework can execute shell script (run.sh or standalone.sh, etc).

So everything you do locally can also be done remotely throuth SSH.

### Main function

* start / stop multiple remote server<br>
  some complex scenario like two jboss server cluster, or one jboss server + one jms server, is easy to implment.
* privide distributed thread coordination operations<br>
  extending Byteman's builtin thread coordination operations such as waiters to be running under distribution environment
* collect and assert remote server log<br>
  can collect log file on remote server, and privide utility class to assert log
* callback support<br>
  privide registering and executing callback function implemented by rmi callback

### Example
* environment
  * controller: test controller node,  running test case
  * server1 & server2: running application which to be tested
  * jms: JMS server
* scenario<br>
  JMS server sends 1 message to each server, and each server will process the message.<br>
  Test case will verify the count of message each server received, and assert the message has been processed.
* byteman-framework.properties<br>
  this is the configration file required by Byteman Framework.<br> 

```
    // test controller declaration  
    controller.rmi.address=controller  
    controller.result.dir=/opt/framework-test/result/
    
    // test node declaration 
    node.address.server1=server1 ip address
    node.address.server2=server2 ip address
    node.address.jms=jms ip address
    
    // byteman & byteman framework configuration
    node.byteman.jar.*=/opt/framework-test/byteman.jar
    node.bytemanframework.jar.*=/opt/framework-test/byteman-framework.jar
    
    // rules applied on jboss startup
    // node.byteman.scripts.mytarget1=/opt/framework-test/mytarget1.btm
    // node.byteman.scripts.mytarget2=/opt/framework-test/mytarget2.btm
    
    // SSH user & password setting
    // use wildcast
    node.ssh.username.*=jboss
    node.ssh.password.*=password
    // or individual setting
    node.ssh.username.jms=jms
    node.ssh.password.jms=mypass
      
    // test node configuration  
    node.jboss.eap6.home.*=/opt/jboss-eap-6.2/
    node.jboss.eap6.base.dir.*=/opt/jboss-eap-6.0/standalone
    node.jboss.eap6.profile.config.*=standalone-full.xml
```

* testcase program

```java
    @Test  
    public void test() throws Exception {  
      // 1. clean the environment  
      CommandManager commandManager = new CommandManager();  
      commandManager.execute("ssh", "server1",  
            "rm -rf /opt/jboss-eap-6.2/jboss-as/server/it/log/*");  
      commandManager.execute("ssh", "server2",  
            "rm -rf /opt/jboss-eap-6.2/jboss-as/server/it/log/*");  
      commandManager.execute("ssh", "jms",  
            "rm -rf /opt/jboss-eap-6.2/jboss-as/server/it/log/*");  
      commandManager.execute("ssh", "jms",  
            "rm -rf /opt/jboss-eap-6.2/jboss-as/server/it/data/hornetq/*");  
      
      // 2. initialize test controller  
      DistributedInstrumentor instrumentor = new DistributedInstrumentorImpl();  
      instrumentor.init();  
      DistributedAdapter adapter = instrumentor.getAdapter();  
      
      // 3. start test node  
      JBossController sender1 = new JBossController("server1");  
      JBossController sender2 = new JBossController("server2");  
      JBossController jms = new JBossController("jms");  
      sender1.start();  
      sender2.start();  
      jms.start();  
      // wait for the starting finish  
      Thread.sleep(60 * 1000);  
      
      // 4. initialize result collector  
      ResultFilesRepository repository = new ResultFilesRepository("Test", "test");  
      repository.clear();  
      
      /*-------------------- test start --------------------*/  
      // 5. apply rule to server1  
      instrumentor.installScript("server1", "server1_counter",  
          "RULE Counter1\n" +  
          "CLASS jp.co.ntt.oss.jboss.jmstest.SenderImpl\n" +  
          "METHOD execute\n" +  
          "HELPER jp.co.ntt.oss.jboss.byteman.framework.helper.DistributedHelper\n" +  
          "AT ENTRY\n" +  
          "IF TRUE\n"+  
          "DO\n" +  
          "flag(\"MSG_FLAG_SERVER1\")\n" +  
          "ENDRULE\n");  
      // 6. apply rule to server2  
      instrumentor.installScript("server2", "server2_counter",  
          "RULE Counter1\n" +  
          "CLASS jp.co.ntt.oss.jboss.jmstest.SenderImpl\n" +  
          "METHOD execute\n" +  
          "HELPER jp.co.ntt.oss.jboss.byteman.framework.helper.DistributedHelper\n" +  
          "AT ENTRY\n" +  
          "IF TRUE\n"+  
          "DO\n" +  
          "flag(\"MSG_FLAG_SERVER2\")\n" +  
          "ENDRULE\n");  
      // 7. apply rule to jms  
      instrumentor.installScript("jms", "jms_counter",  
          "RULE Counter\n" +  
          "CLASS org.hornetq.core.postoffice.impl.PostOfficeImpl\n" +  
          "METHOD processRoute\n" +  
          "HELPER jp.co.ntt.oss.jboss.byteman.framework.helper.DistributedHelper\n" +  
          "AT EXIT\n" +  
          "IF TRUE\n"+  
          "DO\n" +  
          "incrementCounter(\"MSG_COUNT\")\n" +  
          "ENDRULE\n");  
      
      // 8. check counter and flag before test  
      assertFalse(adapter.flagged("MSG_FLAG_SERVER1"));  
      assertFalse(adapter.flagged("MSG_FLAG_SERVER2"));  
      assertEquals(0, adapter.readCounter("MSG_COUNT", false));  
      
      // 9. execute the app on server1  
      
      // 10. validate counter and flag of server1  
      assertTrue(adapter.flagged("MSG_FLAG_SERVER1"));  
      assertFalse(adapter.flagged("MSG_FLAG_SERVER2"));  
      assertEquals(1, adapter.readCounter("MSG_COUNT", false));  
      
      // 11. execute the app on server2  
      
      // 12. validate counter and flag of server2  
      assertTrue(adapter.flagged("MSG_FLAG_SERVER2"));  
      assertTrue(adapter.flagged("MSG_FLAG_SERVER3"));  
      assertEquals(2, adapter.readCounter("MSG_COUNT", false));  
      /*-------------------- test end --------------------*/  
      
      // 13. stop test node  
      sender1.stop();  
      sender2.stop();  
      jms.stop();  
      // wait for node stopping finish  
      Thread.sleep(30 * 1000);  
      
      // 14. stop test controller  
      instrumentor.destroy();  
      
      // 15. collect result  
      repository.collect("server1", "/opt/jboss-eap-6.2/jboss-as/server/it/log/*");  
      repository.collect("server2", "/opt/jboss-eap-6.2/jboss-as/server/it/log/*");  
      repository.collect("jms", "/opt/jboss-eap-6.2/jboss-as/server/it/log/*");  
      repository.collect("jms", "/opt/jboss-eap-6.2/jboss-as/server/it/data/hornetq/journal/*");  
      
      // 16. check error not exists in log files  
      List<File> collectedFiles = repository.getCollectedFiles(null);  
      for(File file : collectedFiles) {  
        Assertion.assertMatches(file, new NoKeywordMatcher("ERROR"));  
      }  
    } 
```
