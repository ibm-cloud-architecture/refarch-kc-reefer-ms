package ibm.eda.kc.freezerms.domain;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ibm.eda.kc.freezerms.infra.api.dto.OrderDTO;
import ibm.eda.kc.freezerms.infra.repo.ReeferRepository;

/**
 * Service layer should implement business rules and logic
 */
@ApplicationScoped
public class ReeferService {
    private static Logger logger = Logger.getLogger("ReeferService");

    @Inject
    public ReeferRepository repository;

    public ReeferService(ReeferRepository repo) {
        this.repository = repo;
    }

    public Reefer getReeferById(String id) {
        return repository.getById(id);
    }

  
    public List<Reefer> getAllReefers() {
        return repository.getAll();
    }

    public Reefer saveReefer(Reefer r){
        repository.addReefer(r);
        return r;
    }

    public Reefer updateFreezer(Reefer newFreezer) {
        return repository.updateReefer(newFreezer);
    }

    public List<Reefer> getAllReefersForTransaction(String txid) {
        return repository.getReefersForTransaction(txid);
    }

    public OrderDTO computeBestFreezerToShip(OrderDTO order) {

        logger.info("compute best freezers for " + order.orderID);
        List<Reefer> reefers = repository.getReefersForOrder(order.orderID, 
            order.pickupCity, 
            order.quantity);
            order.containerID = "";
        for (Reefer f : reefers) {
            order.containerID = order.containerID + f.reeferID + ",";
        }
        if (order.containerID.length() > 0) {
            order.containerID=order.containerID.substring(0, order.containerID.lastIndexOf(","));
        }
        logger.info("--> " + order.toString());
        return order;
    }

    public OrderDTO compensate(OrderDTO order) {
        logger.info("compensate freezer allocation for " + order.orderID);
        order.containerID = "";
        List<Reefer> freezers = repository.getReefersForTransaction(order.orderID);
        for (Reefer f : freezers) {
            f.status = Reefer.FREE;
            f.currentFreeCapacity = f.capacity;
            repository.updateReefer(f);
        }
        logger.info("--> " + order.toString());
        return order;
    }
}
