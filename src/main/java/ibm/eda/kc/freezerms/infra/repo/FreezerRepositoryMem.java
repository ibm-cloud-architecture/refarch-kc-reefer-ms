package ibm.eda.kc.freezerms.infra.repo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;

import ibm.eda.kc.freezerms.domain.Freezer;

@ApplicationScoped
public class FreezerRepositoryMem implements FreezerRepository {
    public  static ConcurrentHashMap<String,List<Freezer>> byLocation = new ConcurrentHashMap<String,List<Freezer>>();
    private ConcurrentHashMap<String, List<Freezer>> currentOrderBacklog = new ConcurrentHashMap<String,List<Freezer>>();
    private static ConcurrentHashMap<String,Freezer> freezers = new ConcurrentHashMap<String,Freezer>();

    private static ObjectMapper mapper = new ObjectMapper();
    

    public FreezerRepositoryMem() {
        super();
        InputStream is = getClass().getClassLoader().getResourceAsStream("reefers.json");
        if (is == null) 
            throw new IllegalAccessError("file not found for order json");
        try {
            List<Freezer> currentDefinitions = mapper.readValue(is, mapper.getTypeFactory().constructCollectionType(List.class, Freezer.class));
            currentDefinitions.stream().forEach( (t) -> { 
                freezers.put(t.reeferID,t);
                if (t.location != null) {
                    if (byLocation.get(t.location) == null) 
                        byLocation.put(t.location, new ArrayList<Freezer>());
                    byLocation.get(t.location).add(t);
                }
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        freezers.values().stream().forEach(v -> System.out.println(v.reeferID));
    }

    public List<Freezer> getAll(){
        return new ArrayList<Freezer>(freezers.values());
    }

    public Freezer addFreezer(Freezer entity) {
        freezers.put(entity.reeferID, entity);
        return entity;
    }

    public Freezer updateFreezer(Freezer entity) {
        freezers.put(entity.reeferID, entity);
        return entity;
    }

    @Override
    public Freezer getById(String key) {
        return freezers.get(key);
    }

    /**
     * Search reefers free in the pickup location that has support capacity
     * @param puck up location
     * @param expectedCapacity
     * @return list of freezers support this expected catacity and at the expected location
     */
    public  List<Freezer>  getFreezersForOrder(String transactionID,String pickupLocation,long expectedCapacity) {
        List<Freezer> potentials = new ArrayList<Freezer>();
        if (pickupLocation == null) return potentials;
        if (byLocation.get(pickupLocation) != null) {
            currentOrderBacklog.put(transactionID, potentials);
            for (Freezer reefer : byLocation.get(pickupLocation)) {
                if (reefer.status.equals(Freezer.FREE)) {
                   
                    reefer.status = Freezer.ALLOCATED;
                    potentials.add(reefer);
                    if (expectedCapacity > reefer.capacity) {
                        reefer.currentFreeCapacity = 0;
                        expectedCapacity -= reefer.capacity;
                    } else {
                        reefer.currentFreeCapacity = reefer.capacity - expectedCapacity;
                        return potentials;
                    }
                }
            } 
        } 
        // this is possible to still have capacity to address... but makes it simple
        return potentials;
    }

    public List<Freezer> getFreezersForTransaction(String transactionID) {
        return currentOrderBacklog.get(transactionID);
    }

    public void cleanTransaction(String transactionID) {
        this.currentOrderBacklog.remove(transactionID);
    }
}
