package com.andersen.peakwork.service;

import com.andersen.peakwork.entity.Stock;
import com.andersen.peakwork.repository.StockRepository;
import com.google.cloud.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import pl.zankowski.iextrading4j.api.stocks.*;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.BatchMarketStocksRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.BatchStocksType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class HistoryPopulationService implements CommandLineRunner {
    private final IEXTradingClient client;
    private final StockRepository stockRepository;

    @Value("${symbols}")
    private Set<String> symbols;

    @Autowired
    public HistoryPopulationService(IEXTradingClient client, StockRepository stockRepository) {
        this.client = client;
        this.stockRepository = stockRepository;
    }

    /**
     * Retrieves history of prices for specified companies for the last 5 years and persists this data for further use.
     */
    @Override
    public void run(String... args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        BatchMarketStocksRequestBuilder batchRequest = new BatchMarketStocksRequestBuilder()
                .addType(BatchStocksType.CHART)
                .addType(BatchStocksType.LOGO)
                .addType(BatchStocksType.COMPANY)
                .withChartRange(ChartRange.FIVE_YEARS);

        symbols.forEach(batchRequest::withSymbol);

        final Map<String, BatchStocks> charts = client.executeRequest(batchRequest.build());

        for (Map.Entry<String, BatchStocks> entry : charts.entrySet()) {
            Logo logo = entry.getValue().getLogo();
            Company company = entry.getValue().getCompany();

            List<Stock> stocks = new ArrayList<>();
            for (Chart chart : entry.getValue().getChart()) {
                Stock stock = new Stock();
                stock.setPrice(chart.getClose().doubleValue());
                stock.setSymbol(entry.getKey());
                stock.setLogoUrl(logo.getUrl());
                stock.setCompanyName(company.getCompanyName());
                long seconds = LocalDate.parse(chart.getDate(), formatter).atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
                stock.setTimestamp(Timestamp.ofTimeSecondsAndNanos(seconds, 0));

                stocks.add(stock);
            }
            stockRepository.save(stocks);
        }
        log.info("History for the last 5 years was saved");
    }
}
