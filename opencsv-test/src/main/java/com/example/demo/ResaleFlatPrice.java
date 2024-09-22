package com.example.demo;

import com.opencsv.bean.CsvBindByName;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class ResaleFlatPrice {
    @CsvBindByName(column = "month")
    private String yearMonth;
    @CsvBindByName(column = "town")
    private String town;
    @CsvBindByName(column = "flat_type")
    private String flatType;
    @CsvBindByName(column = "block")
    private String block;
    @CsvBindByName(column = "street_name")
    private String streetName;
    @CsvBindByName(column = "storey_range")
    private String storeyRange;
    @CsvBindByName(column = "floor_area_sqm")
    private BigDecimal floorArea;
    @CsvBindByName(column = "flat_model")
    private String flatMode;
    @CsvBindByName(column = "lease_commence_date")
    private int leaseCommenceDate;
    @CsvBindByName(column = "remaining_lease")
    private String remainingLease;
    @CsvBindByName(column = "resale_price")
    private BigDecimal resalePrice;

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getFlatType() {
        return flatType;
    }

    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStoreyRange() {
        return storeyRange;
    }

    public void setStoreyRange(String storeyRange) {
        this.storeyRange = storeyRange;
    }

    public BigDecimal getFloorArea() {
        return floorArea;
    }

    public void setFloorArea(BigDecimal floorArea) {
        this.floorArea = floorArea;
    }

    public String getFlatMode() {
        return flatMode;
    }

    public void setFlatMode(String flatMode) {
        this.flatMode = flatMode;
    }

    public int getLeaseCommenceDate() {
        return leaseCommenceDate;
    }

    public void setLeaseCommenceDate(int leaseCommenceDate) {
        this.leaseCommenceDate = leaseCommenceDate;
    }

    public String getRemainingLease() {
        return remainingLease;
    }

    public void setRemainingLease(String remainingLease) {
        this.remainingLease = remainingLease;
    }

    public BigDecimal getResalePrice() {
        return resalePrice;
    }

    public void setResalePrice(BigDecimal resalePrice) {
        this.resalePrice = resalePrice;
    }

    public boolean isValid() {
        return List.of(yearMonth,
                        town,
                        flatType,
                        block,
                        streetName,
                        storeyRange,
                        floorArea,
                        flatMode,
                        leaseCommenceDate,
                        remainingLease,
                        resalePrice)
                .stream()
                .map(it -> StringUtils.hasText(it.toString()))
                .filter(it -> !it)
                .findFirst()
                .orElse(true);
    }

    @Override
    public String toString() {
        return "ResaleFlatPrice{" +
                "yearMonth=" + yearMonth +
                ", town='" + town + '\'' +
                ", flatType='" + flatType + '\'' +
                ", block='" + block + '\'' +
                ", streetName='" + streetName + '\'' +
                ", storeyRange='" + storeyRange + '\'' +
                ", floorArea=" + floorArea +
                ", flatMode='" + flatMode + '\'' +
                ", leaseCommenceDate=" + leaseCommenceDate +
                ", remainingLease='" + remainingLease + '\'' +
                ", resalePrice=" + resalePrice +
                '}';
    }
}
