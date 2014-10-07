rain-notifications
==================

Rain notifications before it happens for Google Wear

Architecture
------------

Here's our module structure (as of Oct 2014): ![Module structure](/readme-files/weather-modules-v2.jpg?raw=true)

How to run the tests
--------------------

Execute `./gradlew clean test` from the terminal. And then find the results in rain-notifications/tests/build/test-report/debug/index.html.

Or in a one liner: `./gradlew clean test && open tests/build/test-report/debug/index.html`

In other words: ![Terminal screenshot with test commands](/readme-files/test-run-from-terminal.png?raw=true)

And then: ![Browser screenshot with test results](/readme-files/test-results.png?raw=true)

