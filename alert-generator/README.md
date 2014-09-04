Alert Generator
===============

Generator of weather alerts. Given a weather transition (e.g., "it's raining now, and will be snowing in 30 minutes from now"), we generate alerts. An alert has a level (e.g., "show to the user as an info notification", or "never mind, don't show it"), and a message (e.g., "Get ready to build a snowman in a half hour").

This is the core business logic for the application. We keep it in a separate project to make it easy to test and to make sure we don't introduce unwanted dependencies.

 