package ut;



import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ibm.eda.kc.freezerms.domain.Freezer;
import ibm.eda.kc.freezerms.infra.events.order.OrderAgent;
import ibm.eda.kc.freezerms.infra.events.order.OrderCreatedEvent;
import ibm.eda.kc.freezerms.infra.events.order.OrderEvent;
import ibm.eda.kc.freezerms.infra.events.order.OrderEventDeserializer;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferAllocated;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferEvent;
import ibm.eda.kc.freezerms.infra.repo.FreezerRepository;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestProcessOrderCreatedEvent {
    
    @Inject
    OrderAgent agent;

    @Inject
    FreezerRepository repo;

    @Test
    public void shouldHaveTwoReefersForAcapacityOf120(){
        List<Freezer> f = repo.getFreezersForOrder("T01", "San Francisco", 120);
        Assertions.assertEquals(2,f.size());
    }

    @Test
    public void shouldBeAbleToCast(){
        OrderCreatedEvent oce = new OrderCreatedEvent("Sydney","San Francisco");

        OrderEvent oe = new OrderEvent(OrderEvent.ORDER_CREATED_TYPE,oce);
        oe.orderID = "Test01";
        oe.quantity = 80;
        ObjectMapperSerializer<OrderEvent> mapper = new ObjectMapperSerializer<OrderEvent>();
        byte[] inMessage = mapper.serialize("orders", oe);
        OrderEventDeserializer deserialize = new OrderEventDeserializer();
        OrderEvent oe2 = deserialize.deserialize("orders", inMessage);
        OrderCreatedEvent oce2 = (OrderCreatedEvent)oe2.payload;
        Assertions.assertEquals("San Francisco", oce2.pickupCity);
        mapper.close();
        deserialize.close();
    }

    @Test
    public void orderCreatedEvent(){

        OrderCreatedEvent oce = new OrderCreatedEvent("Sydney","San Francisco");

        OrderEvent oe = new OrderEvent(OrderEvent.ORDER_CREATED_TYPE,oce);
        oe.orderID = "Test01";
        oe.quantity = 80;
        ReeferEvent re = agent.processOrderCreatedEvent(oe);
        Assertions.assertEquals(ReeferEvent.REEFER_ALLOCATED_TYPE,re.getType());
        Assertions.assertEquals(oe.orderID,((ReeferAllocated)re.payload).orderID);
        Assertions.assertNotNull(((ReeferAllocated)re.payload).reeferIDs);
        System.out.println("Reefer -> " + ((ReeferAllocated)re.payload).reeferIDs);
    }
}
