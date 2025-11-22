# Crypto Trading SDK

<div align="center">



[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue?style=flat-square)](LICENSE)
[![Version](https://img.shields.io/badge/version-0.0.1-orange?style=flat-square)](https://github.com/meai-vision/crypto-trading-sdk/releases)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

**A powerful, unified, and type-safe SDK for crypto trading.**

[Documentation] • [Examples] • [Contributing] • [Discord/Community]

</div>

---

> **⚠️ IMPORTANT: Work in Progress / Pre-Alpha**
>
> This SDK is currently under active development and **has not yet been released** to Maven Central.
> APIs are subject to change without notice. Use for testing or development purposes only.

---

## 🚀 Overview

**Crypto Trading SDK** allows developers to easily connect to multiple cryptocurrency exchanges through a single, unified interface. Designed with performance and reliability in mind, it simplifies the complexity of managing API keys, order execution.

Whether you are building an AI-driven trading bot, a portfolio tracker, or an arbitrage tool, this SDK provides the building blocks you need.

## ✨ Key Features

* **Unified API:** Connect to Binance, WhiteBIT, Coinbase, and Bybit using the same methods.
* **Error Handling:** Robust normalization of errors across different exchanges.
* **AI-Ready:** Optimized data structures for feeding into ML models (perfect for `meai-vision` projects).

## 📋 Prerequisites

Before you begin, ensure you have met the following requirements:

* **Java 21** or higher: This SDK utilizes modern Java features (Records, Pattern Matching, Virtual Threads).
* **Stable Internet Connection**: Critical for maintaining WebSocket connections and executing low-latency trades.
* **No Framework Required**: This is a standalone Java library. It does **not** require Spring Boot or any heavy containers to run.

## 📦 Installation

You can include the SDK in any standard Java application using Maven or Gradle.

### Maven

1. Run `mvn clean install` before going to the next steps.
2. Add the following dependency to your `pom.xml`:
    ```xml
    <dependency>
        <groupId>com.meaivision.crypto-trading-sdk</groupId>
        <artifactId>core</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    ```
3. Reload/resync maven in your project.

## 📄 License

Copyright 2025 MEAI VISION

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.