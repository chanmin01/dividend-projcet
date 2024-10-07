package org.search.wifi.dividend.scraper;

import org.search.wifi.dividend.model.Company;
import org.search.wifi.dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);
}
