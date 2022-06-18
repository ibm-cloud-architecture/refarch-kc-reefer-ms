package ibm.eda.kc.freezerms.infra.repo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;

import ibm.eda.kc.freezerms.domain.Reefer;

@ApplicationScoped
public class ReeferRepositoryMem implements ReeferRepository {
    public  static ConcurrentHashMap<String,List<Reefer>> byLocation = new ConcurrentHashMap<String,List<Reefer>>();
    private ConcurrentHashMap<String, List<Reefer>> currentOrderBacklog = new ConcurrentHashMap<String,List<Reefer>>();
    private static ConcurrentHashMap<String,Reefer> reefers = new ConcurrentHashMap<String,Reefer>();

    private static ObjectMapper mapper = new ObjectMapper();
    

    public ReeferRepositoryMem() {
        super();
        InputStream is = getClass().getClassLoader().getResourceAsStream("reefers.json");
        if (is == null) 
            throw new IllegalAccessError("file not found for reefer json");
        try {
            List<Reefer> currentDefinitions = mapper.readValue(is, mapper.getTypeFactory().constructCollectionType(List.class, Reefer.class));
            currentDefinitions.stream().forEach( (t) -> { 
                reefers.put(t.reeferID,t);
                if (t.location != null) {
                    if (byLocation.get(t.location) == null) 
                        byLocation.put(t.location, new ArrayList<Reefer>());
                    byLocation.get(t.location).add(t);
                }
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        reefers.values().stream().forEach(v -> System.out.println(v.reeferID));
    }

    public List<Reefer> getAll(){
        return new ArrayList<Reefer>(reefers.values());
    }

    public Reefer addReefer(Reefer entity) {
        if (entity.reeferID == null) {
            entity.reeferID = UUID.randomUUID().toString();
        }
        reefers.put(entity.reeferID, entity);
        return entity;
    }

    public Reefer updateReefer(Reefer entity) {
        reefers.put(entity.reeferID, entity);
        return entity;
    }

    @Override
    public Reefer getById(String key) {
        return reefers.get(key);
    }

    /**
     * Search reefers free in the pickup location that has support capacity
     * @param puck up location
     * @param expectedCapacity
     * @return list of freezers support this expected catacity and at the expected location
     */
    public  List<Reefer>  getReefersForOrder(String transactionID,String pickupLocation,long expectedCapacity) {
        List<Reefer> potentials = new ArrayList<Reefer>();
        if (pickupLocation == null) return potentials;
        if (byLocation.get(pickupLocation) != null) {
            currentOrderBacklog.put(transactionID, potentials);
            for (Reefer reefer : byLocation.get(pickupLocation)) {
                if (reefer.status.equals(Reefer.FREE)) {
                   
                    reefer.status = Reefer.ALLOCATED;
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

    public List<Reefer> getReefersForTransaction(String transactionID) {
        return currentOrderBacklog.get(transactionID);
    }

    public void cleanTransaction(String transactionID) {
        List<Reefer> allocatedFreezers = this.currentOrderBacklog.get(transactionID);
        if (allocatedFreezers != null) {
            for (Reefer f: allocatedFreezers) {
                f.status= Reefer.FREE;
                f.currentFreeCapacity = f.capacity;
            }
        }
        this.currentOrderBacklog.remove(transactionID);     
    }
}
