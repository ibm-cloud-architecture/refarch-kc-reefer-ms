package ibm.eda.kc.freezerms.infra.api.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Represents the oshipping rder entity over the wire
 */
@RegisterForReflection
public class OrderDTO {

    public static final String PENDING_STATUS = "pending";
    public static final String CANCELLED_STATUS = "cancelled";
    public static final String ONHOLD_STATUS = "onHold";
    public static final String ASSIGNED_STATUS = "assigned";
    public static final String REJECTED_STATUS = "rejected";
    public static final String SPOILT_STATUS = "spoilt";
    
    public String orderID;
    public int quantity;
    public String pickupCity;
    public String pickupDate;
    public String destinationCity;
    public String expectedDeliveryDate;
    public String status;
	public String voyageID;
	public String containerID;

    public OrderDTO() {
    }

    public OrderDTO(String orderID, String productID, String customerID, int quantity, String pickupCity,
            String pickupDate, String destinationCity, String expectedDeliveryDate, String status) {
        super();
        this.orderID = orderID;
        this.quantity = quantity;
        this.pickupCity = pickupCity;
        this.pickupDate = pickupDate;
        this.destinationCity = destinationCity;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = status;
    }

   

  

    public void setAssignStatus() {
    	if (this.voyageID != null && this.containerID != null) {
    		this.status = OrderDTO.ASSIGNED_STATUS;
    	}
    }
   

    // Implement what can be updated in an order from the customer update order command.
    // For now, we are updating an existing order with whatever comes from the update order command.
    public void update(OrderDTO oco) {
        this.containerID = oco.getContainerID();
        this.voyageID = oco.getVoyageID();
	}

    public void spoilOrder(){
        this.status = OrderDTO.SPOILT_STATUS;
    }

    public void rejectOrder(){
        this.status = OrderDTO.REJECTED_STATUS;
    }

    public void cancelOrder(){
        this.status = OrderDTO.CANCELLED_STATUS;
    }
    
    public String getOrderID() {
        return orderID;
    }

   

    public int getQuantity() {
        return quantity;
    }

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

   

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

   
    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return "Order " + orderID + " Pickup at: " + pickupCity;
    }

	public void setOrderID(String oid) {
		this.orderID = 	oid;
	}

	public void setQuantity(int value) {
		this.quantity = value;
	}

	public String getVoyageID() {
		return voyageID;
	}

	public String getContainerID() {
		return containerID;
	}
}
