PrefixMap: A Map type datastructure that uses prefixes instead of keys
========================================
[![Travis Build status](https://api.travis-ci.com/nielsbasjes/prefixmap.png?branch=master)](https://travis-ci.com/nielsbasjes/prefixmap)
[![Coverage Status](https://coveralls.io/repos/github/nielsbasjes/prefixmap/badge.svg?branch=master)](https://coveralls.io/github/nielsbasjes/prefixmap?branch=master)
[![License](https://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.collections/prefixmap.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.basjes.collections%22)
[![If this project has business value for you then don't hesitate to support me with a small donation.](https://img.shields.io/badge/Donations-via%20Paypal-blue.svg)](https://www.paypal.me/nielsbasjes)

This is a java library that implements a datastructure I consider to be missing in the java landscape.

The datastructure and its usages
================================
In several situations I have a set of values that are each associated with a different prefix of a String.

The example where I found the biggest need for this was when analyzing the device tags of mobile devices.

- A "GT-I8190N" is a "Samsung" device because it starts with "GT-".
- A "RM-1092" is a "Nokia" device because it starts with "RM-".

So I needed a lookup structure that maps these prefixes to the desired brands.

    "GT-" : "Samsung"
    "RM-" : "Nokia"

With this code you can now do this type of lookup in a very fast way:

    // Parameter caseSensitive=false --> so lookups are caseINsensitive
    PrefixMap<String> brandLookup = new StringPrefixMap<>(false);

    brandLookup.put("GT-", "Samsung");
    brandLookup.put("RM-", "Nokia");

    String brandGT = brandLookup.getLongestMatch("GT-I8190N");   // --> "Samsung"
    String brandRM = brandLookup.getLongestMatch("RM-1092");     // --> "Nokia"

All you need is to add this dependency to you project

    <dependency>
      <groupId>nl.basjes.collections</groupId>
      <artifactId>prefixmap</artifactId>
      <version>2.0</version>
    </dependency>

Version 2.0 If you are using Kryo 5.x you can use this to register all classes:

    StringPrefixMap.configureKryo(kryo);

Blog post
=========
A bit more background about this datastructure and how it works can be found in this blog which I wrote about it: [https://techlab.bol.com/finding-the-longest-matching-string-prefix-fast/](https://techlab.bol.com/finding-the-longest-matching-string-prefix-fast/) ([Local copy](Article.md))

Donations
===
If this project has business value for you then don't hesitate to support me with a small donation.

[![Donations via PayPal](https://img.shields.io/badge/Donations-via%20Paypal-blue.svg)](https://www.paypal.me/nielsbasjes)

License
=======

    Copyright (C) 2018-2020 Niels Basjes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
