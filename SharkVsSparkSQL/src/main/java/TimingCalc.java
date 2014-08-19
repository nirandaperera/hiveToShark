/**
 * Created by niranda on 8/19/14.
 */
public class TimingCalc {
    float temp;
     public float getTimeInMilli(long start, long end){
         temp = (end - start);
         return temp/1000000;
    }

    public float getTimeInMicro(long start, long end){
        temp = (end - start);
        return temp/1000;
    }

    public float getTimeInNano(long start, long end){
//        temp = (end - start);
        return (end - start);
    }
}
