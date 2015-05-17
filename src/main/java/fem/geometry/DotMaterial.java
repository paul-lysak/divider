package fem.geometry;

public enum DotMaterial{
   AIR(0), FIGURE(1);
   private final int value;
   private DotMaterial(int value) {
      this.value = value;
   }

   public int getValue(){
      return value;
   }
}
