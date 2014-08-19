/**
 * Created by niranda on 8/19/14.
 */
public class TimingCalc {
     public float getTimeInMilli(long start, long end){
         return (end - start)/1000000;
    }

    public float getTimeInMicro(long start, long end){
        return (end - start)/1000;
    }

    public float getTimeInNano(long start, long end){
        return (end - start);
    }
}
