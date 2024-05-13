# Server-Client communication

## Client To Server

```java
# data is sent in form of hashmap converted to string
# because server-client communication allows only string data exchange
# data is parsed on the other side and converted back to hashmap

# just send data in payload key
{
  id:   "userId",
  payload: "anymessage"
}

# if want to start game then " run game game_name "
# Example
{
  id:   "name_123",
  payload: "run game typeracer"
}

# Sending game data
# Example
{
  id:   "userId",
  type: "game_data",
  data: "anydata"
}
```

# Server To Client

```java
# Starting game in form of hashmaps
# Example
{
  id:   "userId",
  type: "game",
  name: "typeracer"
}

# Sending general message
# Example
{
  id:   "userId",
  payload: "helloworld"
}

```
