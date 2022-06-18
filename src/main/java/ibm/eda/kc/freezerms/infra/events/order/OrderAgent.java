package ibm.eda.kc.freezerms.infra.events.order;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import ibm.eda.kc.freezerms.domain.Reefer;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferAllocated;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferEvent;
import ibm.eda.kc.freezerms.infra.events.reefer.ReeferEventProducer;
import ibm.eda.kc.freezerms.infra.repo.ReeferRepository;

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
    ReeferRepository repo;

    @Inject
    ReeferEventProducer reeferEventProducer;

    @Incoming("orders")
    public CompletionStage<Void> processOrder(Message<OrderEvent> messageWithOrderEvent){
        logger.info("Received order : " + messageWithOrderEvent.getPayload().orderID);
        OrderEvent oe = messageWithOrderEvent.getPayload();
        switch( oe.getType()){
            case OrderEvent.ORDER_CREATED_TYPE:
                processOrderCreatedEvent(oe);
                break;
            case OrderEvent.ORDER_UPDATED_TYPE:
                logger.info("Receive order update " + oe.status);
                compensateOrder(oe.orderID);
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
        List<Reefer> reefers = repo.getReefersForOrder(oe.orderID, 
                                oce.pickupCity, 
                                oe.quantity);
        if (reefers.size() > 0) {
            ReeferAllocated reeferAllocatedEvent = new ReeferAllocated(reefers,oe.orderID);
            ReeferEvent re = new ReeferEvent(ReeferEvent.REEFER_ALLOCATED_TYPE,reeferAllocatedEvent);
            re.reeferID = reeferAllocatedEvent.reeferIDs;     
            reeferEventProducer.sendEvent(re.reeferID,re);  
            return re;
        }
        return null;
    }
 
    public void compensateOrder(String oid) {
        repo.cleanTransaction(oid);
    }
}
