package ibm.eda.kc.freezerms.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Freezer {
   public static String ALLOCATED = "allocated";
   public static String FREE = "free";
   public static String MAINTENANCE = "maintenance";

   public String reeferID;
   public long capacity;
   public String type; 
   public String brand;
   public String status;
   public String location;
   public long currentFreeCapacity;
   public String creationDate;

   public Freezer(){}

   public String toString(){
      return "Freezer: " + reeferID + " capacity: " + capacity + " status: " + status;
   }
}
