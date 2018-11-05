package com.andersen.peakwork.entity;

import com.google.cloud.Timestamp;
import lombok.Data;

@Data
public class Stock {
    public static final String SYMBOL_PROPERTY = "symbol";
    public static final String COMPANY_NAME_PROPERTY = "company";
    public static final String PRICE_PROPERTY = "price";
    public static final String LOGO_URL_PROPERTY = "logo";
    public static final String TIMESTAMP_PROPERTY = "date";

    private String symbol;
    private String companyName;
    private Double price;
    private String logoUrl;
    private Timestamp timestamp;
}
