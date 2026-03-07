package com.meaivision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.BybitConstants;
import com.meaivision.client.BybitRestClient;
import com.meaivision.exception.BybitException;
import com.meaivision.trading.base.model.ExchangeInfo;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.ExchangeInformationServiceFutures;
import com.meaivision.trading.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
public class BybitExchangeInformationServiceFutures implements ExchangeInformationServiceFutures {

  private final ClientProvider<Optional<Object>, BybitRestClient> clientProvider;

  public BybitExchangeInformationServiceFutures(
      ClientProvider<Optional<Object>, BybitRestClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public List<ExchangeInfo> getSymbolsInformation() {
    BybitRestClient client = clientProvider.get(Optional.empty());
    return fetchAllSymbols(client, null, new ArrayList<>());
  }

  private List<ExchangeInfo> fetchAllSymbols(
      BybitRestClient client, String cursor, List<ExchangeInfo> accumulator) {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);
    params.put("limit", "500");
    if (cursor != null) {
      params.put("cursor", cursor);
    }

    String response = sendRequest(client, params);
    JsonNode resultNode = JsonUtils.convertToJsonTree(response).path(BybitConstants.FIELD_RESULT);
    JsonNode symbolsNode = resultNode.path(BybitConstants.FIELD_LIST);

    if (symbolsNode == null || symbolsNode.isMissingNode()) {
      throw new BybitException("Can't find exchange information for symbols!");
    }

    List<ExchangeInfo> page =
        StreamSupport.stream(symbolsNode.spliterator(), false)
            .map(node -> JsonUtils.convertToObject(node, ExchangeInfo.class))
            .filter(Objects::nonNull)
            .toList();

    accumulator.addAll(page);

    JsonNode nextCursorNode = resultNode.path("nextPageCursor");
    String nextCursor =
        nextCursorNode.isMissingNode() || nextCursorNode.asText().isEmpty()
            ? null
            : nextCursorNode.asText();

    if (nextCursor != null) {
      return fetchAllSymbols(client, nextCursor, accumulator);
    }

    log.info("Bybit total symbols fetched: {}", accumulator.size());
    return accumulator;
  }

  private String sendRequest(BybitRestClient client, LinkedHashMap<String, String> params) {
    try {
      String result = client.sendGet("/v5/market/instruments-info", params);
      log.debug("Bybit exchange information response: {}", result);
      return result;
    } catch (IOException e) {
      throw new BybitException("Error occurred during getting Bybit exchange information", e);
    }
  }
}
