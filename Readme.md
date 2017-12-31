## Running
```bash
./gradlew run
```

### Architecture
```
                        +------------+
  Link Channel -------> | Url Filter | ------> Url Channel
       ▲                +------------+          |   |
       |                                        |   |
       |     +----------------------------------+   |
       |     ▼                                      ▼
+----------------+                           +-------------+
| Link Extractor | <--- Document Channel <-- | Url Fetcher |
+----------------+            |              +-------------+
                              ▼
                         +---------+
                         | Counter |
                         +---------+
```