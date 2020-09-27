package com.adidas.challenge;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@EmbeddedKafka(ports = 9092, controlledShutdown = true, brokerProperties = {"state.dir=tmp"})
public class EmailSrvAppTest {

    @Test
    public void contextLoads() { }

}
