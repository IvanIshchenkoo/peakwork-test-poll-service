package com.andersen.peakwork.repository.impl;

import com.andersen.peakwork.entity.Stock;
import com.andersen.peakwork.repository.StockRepository;
import com.google.cloud.datastore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StockRepositoryImpl implements StockRepository {
    // 500 is maximum batch size for Google Datastore
    private static final int BATCH_SIZE = 500;

    private final Datastore datastore;
    private final KeyFactory keyFactory;

    @Autowired
    public StockRepositoryImpl(Datastore datastore, KeyFactory keyFactory) {
        this.datastore = datastore;
        this.keyFactory = keyFactory;
    }

    @Override
    public void save(List<Stock> stocks) {
        List<FullEntity> entities = new ArrayList<>();
        for (Stock stock : stocks) {
            IncompleteKey key = keyFactory.newKey();
            FullEntity<IncompleteKey> entity = Entity.newBuilder(key)
                    .set(Stock.SYMBOL_PROPERTY, stock.getSymbol())
                    .set(Stock.COMPANY_NAME_PROPERTY, stock.getCompanyName())
                    .set(Stock.PRICE_PROPERTY, stock.getPrice())
                    .set(Stock.LOGO_URL_PROPERTY, stock.getLogoUrl())
                    .set(Stock.TIMESTAMP_PROPERTY, stock.getTimestamp())
                    .build();
            entities.add(entity);
        }
        List<FullEntity> toBePersisted = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {

            // for batch write operation
            if (i > 0 && i % BATCH_SIZE == 0) {
                datastore.add(toBePersisted.toArray(new FullEntity[0]));
                toBePersisted.clear();
            }
            toBePersisted.add(entities.get(i));
        }
        datastore.add(toBePersisted.toArray(new FullEntity[0]));
    }
}
