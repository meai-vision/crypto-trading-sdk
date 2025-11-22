package com.meaivision.trading.base.service;

/**
 * This is a trading client client. It is responsible for creating and providing a trading client
 * instance based on the provided configuration properties.
 *
 * <p>The provided client can be for various trading platforms, such as a Binance Spot client.
 *
 * @param <PROPS> The type of properties used to configure the trading client.
 * @param <CL> The type of the trading client to be provided.
 */
public interface ClientProvider<PROPS, CL> {

  /**
   * Provides a configured trading client instance.
   *
   * @param properties The properties configured for the user, used to create and initialize the
   *     trading client.
   * @return A ready-to-use trading client instance.
   */
  CL get(PROPS properties);
}
