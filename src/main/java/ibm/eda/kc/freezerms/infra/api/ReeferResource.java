package ibm.eda.kc.freezerms.infra.api;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ibm.eda.kc.freezerms.domain.Reefer;
import ibm.eda.kc.freezerms.domain.ReeferService;
import ibm.eda.kc.freezerms.infra.api.dto.OrderDTO;


@RequestScoped
@Path("/reefers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReeferResource {
    private static Logger logger = Logger.getLogger("FreezerResource");

    @Inject
    public ReeferService service;
    
    @GET
    public List<Reefer> getAll() {
        return service.getAllReefers();
    }

    @GET
    @Path("/transaction/{txid}")
    public List<Reefer> getAllFreezerForAtransaction(@PathParam("txid") String txid) {
        return service.getAllReefersForTransaction(txid);
    }

    @GET
    @Path("/{reeferId}")
    public Reefer getReeferById(@PathParam("reeferId") String reeferId) {
        return service.getReeferById(reeferId);
    }

    @POST
    public Reefer saveNewFreezer( Reefer newFreezer) {
        logger.info("Save new freezer " + newFreezer.toString());
        return service.saveReefer(newFreezer);
    }

    @PUT
    public Reefer updateFreezer( Reefer newFreezer) {
        logger.info("Update freezer " + newFreezer.toString());
        return service.updateFreezer(newFreezer);
    }

    @POST
    @Path("/assignOrder")
    public Response processOrder( OrderDTO order) {
        logger.info("processOrder " + order.toString());
        // for demo purpose!
        if (order.destinationCity.equals("ABadDestination")) {
            return Response.serverError().build();
        }
        OrderDTO updatedOrder = service.computeBestFreezerToShip(order);
        return Response.ok().entity(updatedOrder).build();
    }

    
    @Path("/compensateOrder")
    @PUT
    public Response compensateOrder(OrderDTO order) {
        OrderDTO updatedOrder = service.compensate(order);
        return Response.ok().entity(updatedOrder).build();
    }

}
