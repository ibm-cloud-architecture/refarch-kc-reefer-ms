package ibm.eda.kc.freezerms.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Reefer {
   // Status property
   public static String ALLOCATED = "allocated";
   public static String FREE = "free";
   public static String MAINTENANCE = "maintenance";
   // types
   public static String MODEL_40 = "model_40";
   public static String MODEL_60 = "model_60";
   
   public String reeferID;
   public long capacity;
   public String type; 
   public String brand;
   public String status;
   public String location;
   public long currentFreeCapacity;
   public String creationDate;

   public Reefer(){}

   public String toString(){
      return "Freezer: " + reeferID + " capacity: " + capacity + " status: " + status;
   }
}
