package com.meaivision.trading.base.stream;

/**
 * Marker for all events that can flow over a User Data Stream. Concrete subtypes today: {@link
 * OrderExecutionEvent}. Extend as needed (balance update, account update, listenKey expiry).
 */
public sealed interface UserDataEvent permits OrderExecutionEvent {}
