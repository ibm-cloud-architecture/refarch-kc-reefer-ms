package ut;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import ibm.eda.kc.freezerms.infra.events.order.OrderCreatedEvent;
import ibm.eda.kc.freezerms.infra.events.order.OrderEvent;
import ibm.eda.kc.freezerms.infra.events.order.OrderEventDeserializer;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferEvent;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferEventDeserializer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;

@QuarkusTest
//@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
@QuarkusTestResource(KafkaCompanionResource.class)
public class TestOrderEventProcessing {
    @InjectKafkaCompanion 
    KafkaCompanion companion;

    @Test
    public void sendOrderCreated(){
        companion.registerSerde(OrderEvent.class, 
                    new io.quarkus.kafka.client.serialization.ObjectMapperSerializer<OrderEvent>(), 
                    new OrderEventDeserializer());
        companion.registerSerde(ReeferEvent.class, 
                    new io.quarkus.kafka.client.serialization.ObjectMapperSerializer<ReeferEvent>(), 
                    new ReeferEventDeserializer());
        OrderCreatedEvent oce = new OrderCreatedEvent("Sydney","San Francisco");

        OrderEvent oe = new OrderEvent(OrderEvent.ORDER_CREATED_TYPE,oce);
        oe.orderID = "Test01";
        oe.quantity = 80;
        List<ProducerRecord<String,OrderEvent>> records = new ArrayList<ProducerRecord<String,OrderEvent>>();
        records.add(new ProducerRecord<String,OrderEvent>("orders",oe.orderID,oe));
        companion.produce(String.class, OrderEvent.class).fromRecords(records);

        ConsumerTask<String,ReeferEvent> reefers = companion.consume(String.class, ReeferEvent.class).fromTopics("reefers", 1);
        reefers.awaitCompletion();
        ConsumerRecord<String,ReeferEvent> reeferEventRecord = reefers.getFirstRecord();
        System.out.println("Reefer allocated is --> " + reeferEventRecord.value().reeferID);
    }

    /*
    @Inject @Any
    InMemoryConnector connector;
    
    @BeforeAll
    public static void switchMyChannels() {
        InMemoryConnector.switchIncomingChannelsToInMemory("orders");
        InMemoryConnector.switchOutgoingChannelsToInMemory("reefers");
    }
    
    @AfterAll
    public static void revertMyChannels() {
        InMemoryConnector.clear();
    }
    
    @Test
    public void orderCreatedShouldGenerateReeferAllocated(){
        InMemorySource<OrderEvent> orders = connector.source("orders");
        InMemorySink<ReeferEvent> reefers = connector.sink("reefers");
         
        OrderCreatedEvent oce = new OrderCreatedEvent("Sydney","San Francisco");

        OrderEvent oe = new OrderEvent(OrderEvent.ORDER_CREATED_TYPE,oce);
        
        orders.send(oe);

        await().<List<? extends Message<ReeferEvent>>>until(reefers::received, t -> t.size() == 1);
        
    }
    */
}
