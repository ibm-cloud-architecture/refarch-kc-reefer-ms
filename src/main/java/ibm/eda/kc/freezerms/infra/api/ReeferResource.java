package ibm.eda.kc.freezerms.infra.api;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ibm.eda.kc.freezerms.domain.Freezer;
import ibm.eda.kc.freezerms.domain.FreezerService;


@RequestScoped
@Path("/reefers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReeferResource {
    
    @Inject
    public FreezerService serv;
    
    @GET
    public List<Freezer> getAll() {
        return serv.getAllReefers();
    }

}
