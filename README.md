# vectorbrat

Experimental software for live interactive installations using ILDA lasers and xy-mode cathode ray tube oscilloscopes.

> "more of a brat than a punk"

## Lasers and Vectors and Brats Oh My!

If you don't know why you want to play with lasers and vector graphics and 
what's the big deal anyway, start with [background info](background.md).

## TODO

See [TODO](TODO.md).


## Modular Synthesiser Integeration

Modular synthesisers can also be used to control ILDA lasers
using such modules as the 
[LZX Industries Cyclops](https://lzxindustries.net/products/cyclops) 
(discontinued) which has an ILDA DB25 and optionally add a 
[1010 Toolbox](https://1010music.com/product/toolbox-sequencer-function-generator-eurorack-module) (discontinued)
running the alternative 1010
[LaserBox Firmware](https://1010music.com/product/laserbox-pattern-generator-for-lasers).

### Modules:

* cyclops - ILDA integration, interlock, attenuation of outbound analog laser control signals.
* laserbox - pattern generation, load/store of files: wav, ilda, patterns
* es-9 - computer integration, headphone monitoring, line in, balanced line out
* dasiypatch - beat detection, orchestration, 
* muxlicer - arbitrary sequencing, switching
* vca matrix - mixing two geometry sources
* rampage - slew, laser functions
* distingEX - modulation  
* Pams + pexp2 - clocks, lfo
* data - signal analysis, waveform generator
* zoia - everthing
* mult - sharing clock
* (possibly?) poly hector (general duties)

### Dedicated Control surfaces

* Faderbank 16n
* Beat Step Pro
* USB Gamepads



