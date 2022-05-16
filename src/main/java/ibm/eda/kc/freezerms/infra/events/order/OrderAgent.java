package ibm.eda.kc.freezerms.infra.events.order;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import ibm.eda.kc.freezerms.domain.Freezer;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferAllocated;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferEvent;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferEventProducer;
import ibm.eda.kc.freezerms.infra.repo.FreezerRepository;

/**
 * Listen to the orders topic and processes event from order service:
 * - order created event
 * - order cancelled event
 * Normally it should also support order updated event and recompute capacity
 */
@ApplicationScoped
public class OrderAgent {
    Logger logger = Logger.getLogger(OrderAgent.class.getName());

    @Inject
    FreezerRepository repo;

    @Inject
    ReeferEventProducer reeferEventProducer;

    @Incoming("orders")
    public CompletionStage<Void> processOrder(Message<OrderEvent> messageWithOrderEvent){
        logger.info("Received order : " + messageWithOrderEvent.getPayload().orderID);
        OrderEvent oe = messageWithOrderEvent.getPayload();
        switch( oe.getType()){
            case OrderEvent.ORDER_CREATED_TYPE:
                ReeferEvent re=processOrderCreatedEvent(oe);
                reeferEventProducer.sendEvent(re.reeferID,re);
                break;
            default:
                break;
        }
        return messageWithOrderEvent.ack();
    }

    /**
     * When order created, search for reefers close to the pickup location,
     * add them in the container ids and send an event as ReeferAllocated
     */
    public ReeferEvent processOrderCreatedEvent( OrderEvent oe){
        OrderCreatedEvent oce = (OrderCreatedEvent)oe.payload;
        List<Freezer> freezers = repo.getFreezersForOrder(oe.orderID, 
                                oce.pickupCity, 
                                oe.quantity);
        ReeferAllocated reeferAllocatedEvent = new ReeferAllocated(freezers,oe.orderID);
        ReeferEvent re = new ReeferEvent();
        re.reeferID = reeferAllocatedEvent.reeferIDs;
        re.setType(ReeferEvent.REEFER_ALLOCATED_TYPE);
        re.payload = reeferAllocatedEvent;
       
        return re;
    }
 
}
