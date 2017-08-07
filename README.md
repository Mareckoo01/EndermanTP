# EndermanTP
##### Players need **endermantp.teleport** permission to be teleported
To use commands you need **endermantp.command** permission, commands:
- **/endermantp** - main command
- - **/endermantp delay** - tells you current delay
- - - **/endermantp delay 80** - changes delay to 80(in config too)
- - **/endermantp threshold** - tells you current threshold
- - - **/endermantp threshold 5** - changes threshold to 5(in config too)
- - **/endermantp multiply** - tells you current multiply
- - - **/endermantp multiply 3** - changes multiply to 3(in config too)
- - **/endermantp maxY** - tells you current max Y distance
- - - **/endermantp maxY 5** - changes max Y distance to 5(in config too)
- - **/endermantp debug** - switches debug on or off(in config too)
###### *note multiply and max Y are doubles that means they can be set to 3 or 3.3 or 3.000003
###### [Compiled plugin .jar](https://github.com/Mareckoo01/EndermanTP/raw/master/compiled/EndermanTP.jar)
### Config options
- threshold: how many times does player need to hit enderman to get teleported or teleport enderman to him
- delay: how long should hit be stored (in ticks, 20 ticks = 1 second)
- debug: send debug messages to player
- multiply: player's direction will be multiplied and changed to block location and player will be teleported to it
- maxYDistance: player will be allways teleported to highest Y block in ^location, but this limits it(it will continue searching and if it doesn't find position it will just teleport player to enderman)
### Default config
- threshold: 5
- delay: 80 (4 seconds)
- debug: false
- multiply: 3.0
- maxYDistance: 5.0
