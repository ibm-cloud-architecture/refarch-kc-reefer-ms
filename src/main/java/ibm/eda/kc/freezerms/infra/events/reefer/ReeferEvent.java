package ibm.eda.kc.freezerms.infra.events.reefer;

import ibm.eda.kc.freezerms.infra.events.EventBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ReeferEvent extends EventBase {
    public static final String REEFER_ALLOCATED_TYPE = "ReeferAllocated";
	public String reeferID;
    public ReeferVariablePayload payload;


}
