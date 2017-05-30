package come.texi.driver.utils;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by techintegrity on 13/07/16.
 */
public class AllTripFeed implements Serializable {


    String bookig_id;
    public String getBookingId(){
        return bookig_id;
    }
    public void setBookingId(String bookid){
        this.bookig_id = bookid;
    }

    String pickup_area;
    public String getPickupArea(){
        return pickup_area;
    }
    public void setPickupArea(String pickAre){
        this.pickup_area = pickAre;
    }

    String drop_area;
    public String getDropArea(){
        return drop_area;
    }
    public void setDropArea(String drpAre){
        this.drop_area = drpAre;
    }

    String pickup_date_time;
    public String getPickupDateTime(){
        return pickup_date_time;
    }
    public void setPickupDateTime(String pickupdatetime){
        this.pickup_date_time = pickupdatetime;
    }

    String taxi_type;
    public String getTaxiType(){
        return taxi_type;
    }
    public void setTaxiType(String taxitype){
        this.taxi_type = taxitype;
    }

    String km;
    public String getKm(){
        return km;
    }
    public void setKm(String km){
        this.km = km;
    }

    String amount;
    public String getAmount(){
        return amount;
    }
    public void setAmount(String amount){
        this.amount = amount;
    }

    String car_icon;
    public String getCarIcon(){
        return car_icon;
    }
    public void setCarIcon(String car_icon){
        this.car_icon = car_icon;
    }

    String driver_detail;
    public String getDriverDetail(){
        return driver_detail;
    }
    public void setDriverDetail(String driver_detail){
        this.driver_detail = driver_detail;
    }

    String status;
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    String approx_time;
    public String getApproxTime(){
        return approx_time;
    }
    public void setApproxTime(String approx_time){
        this.approx_time = approx_time;
    }

    ArrayList<LatLng> OldLocationList;
    public ArrayList<LatLng> getOldLocationList(){
        return OldLocationList;
    }
    public void setOldLocationList(ArrayList<LatLng> OldLocationList){
        this.OldLocationList = OldLocationList;
    }

    LatLng PickupLarLng;
    public LatLng getPickupLarLng(){
        return PickupLarLng;
    }
    public void setPickupLarLng(LatLng PickupLarLng){
        this.PickupLarLng = PickupLarLng;
    }

    LatLng DropLarLng;
    public LatLng getDropLarLng(){
        return DropLarLng;
    }
    public void setDropLarLng(LatLng DropLarLng){
        this.DropLarLng = DropLarLng;
    }

    LatLng DriverLarLng;
    public LatLng getDriverLarLng(){
        return DriverLarLng;
    }
    public void setDriverLarLng(LatLng DriverLarLng){
        this.DriverLarLng = DriverLarLng;
    }

    String StartPickLatLng;
    public String getStartPickLatLng(){
        return StartPickLatLng;
    }
    public void setStartPickLatLng(String StartPickLatLng){
        this.StartPickLatLng = StartPickLatLng;
    }

    String EndPickLatLng;
    public String getEndPickLatLng(){
        return EndPickLatLng;
    }
    public void setEndPickLatLng(String EndPickLatLng){
        this.EndPickLatLng = EndPickLatLng;
    }

    String StartDropLatLng;
    public String getStartDropLatLng(){
        return StartDropLatLng;
    }
    public void setStartDropLatLng(String StartDropLatLng){
        this.StartDropLatLng = StartDropLatLng;
    }

    String EndDropLatLng;
    public String getEndDropLatLng(){
        return EndDropLatLng;
    }
    public void setEndDropLatLng(String EndDropLatLng){
        this.EndDropLatLng = EndDropLatLng;
    }
}
