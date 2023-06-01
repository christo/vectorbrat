# vectorbrat TODO List

* [ ] move to github issues
* [ ] brightness
* [ ] text rendering, fonts
* [ ] configurable x and y flip for DisplayPanel
* [ ] scale markers in debug mode
* [ ] grid in debug mode
* [ ] dynamic parameter changing
* [ ] x and y positive arrows in debug mode
* [ ] configurable points per second
* [ ] collect credits to do some kind of credit screen
* [ ] vitalise branding - svg translation
* [ ] debug colour bleed (caused by path planner error or hardware?)
* [ ] gamepads / other control surface (midi? cv?)
* [ ] text rendering
* [ ] render circle primitives using sin/cos rather than polygon
* [ ] performance testing and instrumentation
* [ ] laser driver
  * [ ] brightness control test and calibration
  * [ ] skew, keystone, scale, rotation, translation
  * [ ] debug / logging
    * [ ] geometry stats
    * [ ] path period / fps
  * [ ] config
    * [ ] max pps
    * [ ] max dwell time
    * [ ] max path duration
    * [ ] strategy for culling geometry
    * [ ] detail levels
    * [ ] optional display items (score, spare ships, fps)
  * text engine
    * original asteroids font
    * hershey vector font
  * wav generation and export
* game options (asteroid sizes etc.)
* sound effects
  * sound fx test rig  
  * modular synth for sounds
    * two cv lines: sound index, trigger
    * send cv for sound fx
    * use muxlicer to decode up to 8 sounds
    * patch dfam, mother32 to make individual sounds
  * sample playback (backup plan)
    * use disting EX for playback
* use jack for nano clock in AppMap constructor

## Done

* [ ] laser driver
  * [x] linear, quintic easing functions for line interpolation
  * [x] dwell time for bright points
  * [x] colour
  * [x] debug output - shows interconnected paths
  * [x] render arbitrary streaming geometry on laser
* [x] spike: ensure es8 or es9 DC coupling 
* [x] define deployment setup
  * [x] laptop
  * [x] modular rig
* [x] audio device detection
* [x] colour
* [x] off/on
* [x] dwell points
* [x] review and collect prior art
* [x] attempt to build best existing
* [x] compare existing to rebuild option
* [x] audio visualisations via *ad hoc* modular patches
* [x] practice laser shape jamming
* [x] dev setup and workflow

