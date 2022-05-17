package ibm.eda.kc.freezerms.infra.events.reefer;

import java.util.Date;

import ibm.eda.kc.freezerms.infra.events.EventBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ReeferEvent extends EventBase {
    public static final String REEFER_ALLOCATED_TYPE = "ReeferAllocated";
	public String reeferID;
    public ReeferVariablePayload payload;

    public ReeferEvent(long timestampMillis, 
                String type, 
                String version, 
                ReeferVariablePayload payload) {
        super(timestampMillis, type, version);
        this.payload = payload;
    }

    public ReeferEvent( String aType, ReeferVariablePayload payload) {
        this.payload = payload;
        this.type = aType;
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
    }

    public ReeferEvent(){
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
    }
}
