
# Asteroids

```
  A
 STE
ROIDS
```

## References

* Arcade Games 
  * Asteroids (Atari) 
  * Asteroids Deluxe (Atari)
  * Battlezone
  * Star Wars / The Empire Strikes Back
  * Gravitar (defender x lunar lander)
  * Defender
  * Lunar Lander
  * Speed Freak (road race)

## Considered stacks:

* Python / pygame / sdl
* Java / JRE
  * with Processing
  * without Processing
* P5.js
* JavaScript / TypeScript other framework
* C++
  * open frameworks (ofx)
  * other framework

## Implementation Plan

* ensure es8 or es9 DC coupling spike
* render arbitrary streaming geometry on laser
* laser driver
  * easing functions for polygon points (quintic?)
  * dwell time for bright points
  * colour
  * debug output - shows interconnected paths 
  * brightness control test
  * skew, keystone, scale, rotation, translation
  * debug / logging
    * geometry stats
    * path period / fps
  * config
    * max pps
    * max dwell time
    * max path duration
    * strategy for culling geometry
    * colours
    * detail levels
    * optional display items (score, spare ships, fps)
    * game options (asteroid sizes etc.)
* hershey vector font - text engine
* wav generation
* sound effects
  * synth
  * samples (backup plan)
* workflow
* dev setup