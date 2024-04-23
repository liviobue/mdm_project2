package ch.zhaw.buergli1.project2;

import java.time.LocalDate;

public class WaterQualityData {
    private String siteId;
    private String unitId;
    private LocalDate readDate;
    private double salinity;
    private double dissolvedOxygen;
    private double pH;
    private double secchiDepth;
    private double waterDepth;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public LocalDate getReadDate() {
        return readDate;
    }

    public void setReadDate(LocalDate readDate) {
        this.readDate = readDate;
    }

    public double getSalinity() {
        return salinity;
    }

    public void setSalinity(double salinity) {
        this.salinity = salinity;
    }

    public double getDissolvedOxygen() {
        return dissolvedOxygen;
    }

    public void setDissolvedOxygen(double dissolvedOxygen) {
        this.dissolvedOxygen = dissolvedOxygen;
    }

    public double getpH() {
        return pH;
    }

    public void setpH(double pH) {
        this.pH = pH;
    }

    public double getSecchiDepth() {
        return secchiDepth;
    }

    public void setSecchiDepth(double secchiDepth) {
        this.secchiDepth = secchiDepth;
    }

    public double getWaterDepth() {
        return waterDepth;
    }

    public void setWaterDepth(double waterDepth) {
        this.waterDepth = waterDepth;
    }

    public double getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(double waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    private double waterTemperature;
    private double airTemperature;

    public WaterQualityData(String siteId, String unitId, LocalDate readDate, double salinity, double dissolvedOxygen,
            double pH, double secchiDepth, double waterDepth, double waterTemperature,
            double airTemperature) {
        this.siteId = siteId;
        this.unitId = unitId;
        this.readDate = readDate;
        this.salinity = salinity;
        this.dissolvedOxygen = dissolvedOxygen;
        this.pH = pH;
        this.secchiDepth = secchiDepth;
        this.waterDepth = waterDepth;
        this.waterTemperature = waterTemperature;
        this.airTemperature = airTemperature;
    }

}
