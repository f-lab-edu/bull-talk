package com.lima.consoleservice.schedule.log.params;

import lombok.Getter;

@Getter
public enum Symbol {
  AAPL("Apple")
//  , MSFT("Microsoft")
//  , AMZN("Amazon")
//  , GOOGL("Alphabet Class A")
//  , GOOG("Alphabet Class C")
//  , META("Meta")
//  , NVDA("Nvidia")
//  , TSLA("Tesla")
//  , AVGO("Broadcom")
//  , NFLX("Netflix")
//  , AMD("Advanced Micro Devices")
//  , INTC("Intel")
//  , QCOM("Qualcomm")
//  , IBM("International Business Machines")
//  , ORCL("Oracle")
//  , SAP("SAP")
//  , CRM("Salesforce")
//  , NOW("ServiceNow")
//  , SNOW("Snowflake")
//  , PLTR("Palantir Technologies")
//  , ADBE("Adobe")
//  , DOCU("DocuSign")
//  , ZM("Zoom Video Communications")
//  , UBER("Uber Technologies")
//  , LYFT("Lyft")
//  , PYPL("PayPal")
//  , SQ("Square")
//  , SHOP("Shopify")
//  , SE("Sea Limited")
//  , KAKAOY("Kakao")
//  , NHNCF("Naver")
//  , SSNLF("Samsung")
//  , TSM("Taiwan Semiconductor Manufacturing Company")
//  , SONY("Sony")
//  , LGEIY("LG")
//  , BIDU("Baidu")
//  , BABA("Alibaba")
//  , JD("JD.com")
//  , TCEHY("Tencent")
//  , PDD("Pinduoduo")
//  , ASML("반도체 장비 제조사")
//  , TXN("Texas Instruments")
//  , LRCX("Lam Research")
//  , MU("Micron Technology")
//  , WDC("Western Digital")
//  , STX("Seagate Technology")
//  , NXPI("NXP Semiconductors")
//  , HPQ("Hewlett-Packard")
//  , DELL("Dell Technologies")
//  , COIN("Coinbase")
//  , INTU("Intuit")
  ;


  private final String company;

  Symbol(String company) {
    this.company = company;
  }
}
