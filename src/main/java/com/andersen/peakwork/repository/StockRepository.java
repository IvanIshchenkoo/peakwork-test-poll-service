package com.andersen.peakwork.repository;

import com.andersen.peakwork.entity.Stock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository {
    /**
     * Saves passed list of stocks to datastore
     *
     * @param stock list of stocks to be persisted
     */
    void save(List<Stock> stock);
}
