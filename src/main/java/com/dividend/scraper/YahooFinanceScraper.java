package com.dividend.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.dividend.model.Company;
import com.dividend.model.Dividend;
import com.dividend.model.ScrapedResult;
import com.dividend.model.constants.Month;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s/";
    private static final long START_TIME = 60 * 60 * 24;

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {

            long now = System.currentTimeMillis() / 1000;
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);

            // 페이지 연결
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            // 각 트랜잭션(날짜와 배당금이 있는 tr 태그)을 가져옴
            Elements parsingTrs = document.select("tr");

            List<Dividend> dividends = new ArrayList<>();

            // 데이터를 하나씩 출력
            for (Element tr : parsingTrs) {
                Elements tds = tr.select("td");

                if (tds.size() >= 2) { // 날짜와 배당금이 모두 있는지 확인
                    String dateText = tds.get(0).text();  // 첫 번째 td에서 날짜 추출
                    String dividendText = tds.get(1).select("span.yf-ewueuo").text();  // 두 번째 td에서 배당금 추출

                    if (!dividendText.isEmpty()) {
                        // 날짜를 "month day, year" 형식으로 분리
                        String[] dateParts = dateText.split(" ");  // 공백 기준으로 분리
                        int month = Month.strToNumber(dateParts[0]);  // 예: "Jul"
                        int day = Integer.valueOf(dateParts[1].replace(",", ""));  // 쉼표 제거 후 day 추출
                        int year = Integer.valueOf(dateParts[2]);  // 예: "2024"

                        if (month < 0) {
                            throw new RuntimeException("Unexpected Month enum value -> " + dateParts[0]);
                        }

                        dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividendText));

                    }
                }
            }
            scrapResult.setDividendEntities(dividends);

        } catch (IOException e) {
            e.printStackTrace();

        }
        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByClass("yf-xxbei9").get(0);
            String[] titleParts = titleEle.text().split("\\(");
            String companyName = titleParts[0].trim();
            String companyTicker = titleParts[1].replace(")", "").trim();


            return new Company(companyTicker, companyName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


