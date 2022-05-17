package ibm.eda.kc.freezerms.domain;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ibm.eda.kc.freezerms.infra.repo.FreezerRepository;

@ApplicationScoped
public class FreezerService {
    private static Logger logger = Logger.getLogger("ReeferService");

    @Inject
    public FreezerRepository repository;

    public FreezerService(FreezerRepository repo) {
        this.repository = repo;
    }

    public Freezer getReeferById(String id) {
        return repository.getById(id);
    }

  
    public List<Freezer> getAllReefers() {
        return repository.getAll();
    }

    public Freezer saveReefer(Freezer r){
        repository.addFreezer(r);
        return r;
    }

    public Freezer updateFreezer(Freezer newFreezer) {
        return repository.updateFreezer(newFreezer);
    }

    public List<Freezer> getAllReefersForTransaction(String txid) {
        return repository.getFreezersForTransaction(txid);
    }
}
