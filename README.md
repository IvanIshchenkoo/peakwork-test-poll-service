Poll service
=========================

This service polls [IEX Developer Platform](https://iextrading.com/developer/) API to retrieve current price for the selected companies.

Poll interval and companies can either be configured in applictaion.yaml file or via program arguments 
e.g `--pollingInterval=10000` (time is specified in milliseconds) and `--symbols=AAPL,FB` (by default `pollingInterval=60000` and `symbols=AAPL,FB`).

This app uses Google datastore for persistence. It takes credentials from environment values. You can specified used kind in configuration file or via program arguments `--datastore.kind=your-kind` (by default `datastore.kind=stock`).

The app polls for company name, it's logo, and current market price.
When the app launched for the first time it will get historical daily data for 5 years about specified companies and persist it. 
That data have the same format as the data polled at intervals.

Please enable annotation processing in your IDE to run the project from IDE.
