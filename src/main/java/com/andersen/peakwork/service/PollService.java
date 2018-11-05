package com.andersen.peakwork.service;

import com.andersen.peakwork.entity.Stock;
import com.andersen.peakwork.repository.StockRepository;
import com.google.cloud.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.zankowski.iextrading4j.api.stocks.BatchStocks;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.BatchMarketStocksRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.BatchStocksType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class PollService {
    private final StockRepository stockRepository;
    private final IEXTradingClient client;

    @Value("${symbols}")
    private Set<String> symbols;

    @Autowired
    public PollService(StockRepository stockRepository, IEXTradingClient client) {
        this.stockRepository = stockRepository;
        this.client = client;
    }

    /**
     * Polls in set intervals and saves new prices with company info, price and timestamp
     * for specified companies
     */
    @Scheduled(fixedRateString = "${pollingInterval}")
    public void run() {

        BatchMarketStocksRequestBuilder batchRequest = new BatchMarketStocksRequestBuilder()
                .addType(BatchStocksType.COMPANY)
                .addType(BatchStocksType.PRICE)
                .addType(BatchStocksType.LOGO);
        symbols.forEach(batchRequest::withSymbol);

        final Map<String, BatchStocks> batchStocksMap = client.executeRequest(batchRequest.build());

        List<Stock> stocks = new ArrayList<>();
        for (Map.Entry<String, BatchStocks> entry : batchStocksMap.entrySet()) {
            Stock stock = new Stock();
            stock.setSymbol(entry.getKey());
            stock.setCompanyName(entry.getValue().getCompany().getCompanyName());
            stock.setPrice(entry.getValue().getPrice().doubleValue());
            stock.setLogoUrl(entry.getValue().getLogo().getUrl());
            stock.setTimestamp(Timestamp.now());

            stocks.add(stock);
        }
        stockRepository.save(stocks);
    }
}
