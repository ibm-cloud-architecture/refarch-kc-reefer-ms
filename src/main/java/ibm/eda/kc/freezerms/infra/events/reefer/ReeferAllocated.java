package ibm.eda.kc.freezerms.infra.events.reefer;

import java.util.List;

import ibm.eda.kc.freezerms.domain.Reefer;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Generate an event that could have aggregated IDs
 */
@RegisterForReflection
public class ReeferAllocated extends ReeferVariablePayload {
	public String orderID;
	public boolean aggregate = false;
	public String reeferIDs = "";

	public ReeferAllocated() {}

	public ReeferAllocated(List<Reefer> freezers , String oid) {
		this.orderID = oid;
		if (freezers.size() == 1) {
			this.reeferIDs = freezers.get(0).reeferID;
		} else {
			this.aggregate = true;
			for (Reefer f : freezers) {
				reeferIDs = reeferIDs + f.reeferID + ",";
			}
			if (reeferIDs.length() > 0) {
				reeferIDs=reeferIDs.substring(0, reeferIDs.lastIndexOf(","));
			}
		}

	}

	public ReeferAllocated(String cid,String oid) {
		this.orderID = oid;
		this.reeferIDs = cid;
	} 
}
