v1.0.1

- Having issues with TOML bundling in NeoForge, rewrote mod to use GSON so logbegone.toml is now logbegone.json so: 
```toml
# The configuration file for Log Begone

[logbegone]
# If a log message has one of these phrases, it will be filtered out from logging
phrases = ["Disconnecting VANILLA connection attempt", "Channels "]

# If a log message matches one of these regex patterns, it will be filtered out from logging. EX: 
regex = ["Disconnecting VANILLA connection attempt", "Channels "]
```
is now
```json
{
  "logbegone": {
    "phrases": [
      "Disconnecting VANILLA connection attempt",
      "Channels "
    ],
    "regex": [
      "Disconnecting VANILLA connection attempt",
      "Channels "
    ]
  }
}
```