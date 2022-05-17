package ibm.eda.kc.freezerms.infra.repo;

import java.util.List;

import ibm.eda.kc.freezerms.domain.Freezer;

public interface FreezerRepository {
    public List<Freezer> getAll();
    public Freezer addFreezer(Freezer entity);
    public Freezer updateFreezer(Freezer entity);
    public Freezer getById(String key);
    public List<Freezer>  getFreezersForOrder(String transactionID,String pickupLocation,long expectedCapacity);
    public List<Freezer> getFreezersForTransaction(String transactionID);
    public void cleanTransaction(String transactionID);
}
