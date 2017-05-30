package come.texi.driver.utils;

import java.io.Serializable;

/**
 * Created by techintegrity on 11/07/16.
 */
public class CabDetails implements Serializable {

    String id;
    public String getId(){
        return id;
    }
    public void setId(String cabId){
        this.id = cabId;
    }

    boolean is_selected;
    public boolean getiIsSelected(){
        return is_selected;
    }
    public void setIsSelected(boolean is_selected){
        this.is_selected = is_selected;
    }

    String cartype;
    public String getCartype(){
        return cartype;
    }
    public void setCartype(String ctype){
        this.cartype = ctype;
    }

    String transfertype;
    public String getTransfertype(){
        return transfertype;
    }
    public void setTransfertype(String trntype){
        this.transfertype = trntype;
    }

    String intialkm;
    public String getIntialkm(){
        return intialkm;
    }
    public void setIntialkm(String intikm){
        this.intialkm = intikm;
    }


    String CarRate;
    public String getCarRate(){
        return CarRate;
    }
    public void setCarRate(String CarRate){
        this.CarRate = CarRate;
    }

//    String intailrate;
//    public String getIntailrate(){
//        return intailrate;
//    }
//    public void setIntailrate(String intrate){
//        this.intailrate = intrate;
//    }

    String standardrate;
    public String getStandardrate(){
        return standardrate;
    }
    public void setStandardrate(String stanrate){
        this.standardrate = stanrate;
    }

    String fromintialkm;
    public String getFromintialkm(){
        return fromintialkm;
    }
    public void setFromintialkm(String fromintialkm){
        this.fromintialkm = fromintialkm;
    }

    String fromintailrate;
    public String getFromintailrate(){
        return fromintailrate;
    }
    public void setFromintailrate(String fromintailrate){
        this.fromintailrate = fromintailrate;
    }

    String fromstandardrate;
    public String getFromstandardrate(){
        return fromstandardrate;
    }
    public void setFromstandardrate(String fromstandardrate){
        this.fromstandardrate = fromstandardrate;
    }

    String night_fromintialkm;
    public String getNightFromintialkm(){
        return night_fromintialkm;
    }
    public void setNightFromintialkm(String night_fromintialkm){
        this.night_fromintialkm = night_fromintialkm;
    }

    String night_fromintailrate;
    public String getNightFromintailrate(){
        return night_fromintailrate;
    }
    public void setNightFromintailrate(String night_fromintailrate){
        this.night_fromintailrate = night_fromintailrate;
    }

    String icon;
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon = icon;
    }

    String night_intailrate;
    public String getNightIntailrate(){
        return night_intailrate;
    }
    public void setNightIntailrate(String night_intailrate){
        this.night_intailrate = night_intailrate;
    }

    String night_standardrate;
    public String getNightStandardrate(){
        return night_standardrate;
    }
    public void setNightStandardrate(String night_standardrate){
        this.night_standardrate = night_standardrate;
    }

    String ride_time_rate;
    public String getRideTimeRate(){
        return ride_time_rate;
    }
    public void setRideTimeRate(String ride_time_rate){
        this.ride_time_rate = ride_time_rate;
    }

    String night_ride_time_rate;
    public String getNightRideTimeRate(){
        return night_ride_time_rate;
    }
    public void setNightRideTimeRate(String night_ride_time_rate){
        this.night_ride_time_rate = night_ride_time_rate;
    }

    String description;
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    String seatCapacity;
    public String getSeatCapacity(){
        return seatCapacity;
    }
    public void setSeatCapacity(String seatCapacity){
        this.seatCapacity = seatCapacity;
    }

    String area_id;
    public String getAreaId(){
        return area_id;
    }
    public void setAreaId(String area_id){
        this.area_id = area_id;
    }

    String fix_price;
    public String getFixPrice(){
        return fix_price;
    }
    public void setFixPrice(String fix_price){
        this.fix_price = fix_price;
    }

}
