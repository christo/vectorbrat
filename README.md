# vectorbrat

Experimental software for live interactive installations using ILDA lasers and xy-mode cathode ray tube oscilloscopes.

![vectorbrat logo](src/main/resources/vectorbrat.png)

> "more of a brat than a punk"

## Lasers and Vectors and Brats Oh My!

If you don't know why you want to play with lasers and vector graphics and 
what's the big deal anyway, start with [background info](background.md).

[Information I've collected about lasers](lasers.md).

## How To

During this alpha stage, the audio device is configured in `jackstart` and the 
operating system with its audio driver specifics are assumed to be `macos`. The 
goal is to make this cross-platform. If you get it working on Linux or Windows,
please let me know and/or make a pull-request with the necessary changes.

## TODO

SO much. See [TODO](TODO.md).


## Game Ideas

* [asteroids](asteroids.md)
* pong
* gravity wars
* star wars ?
* lunar lander
* pinball
* future racing
* gyruss
* night driving
* missile command
* Zork (text engine)

## App Ideas

* console (Q: maximum rows/cols? optimal font?)
* boids
* chaos stuff
  * lorenz attractor
  * hilbert curve
  * fractal trees
* vector synth
* demoscene stuff
  * cubes
  * metablobs
  * tunnels
  * scrolltext
* star wars text


## Effects Ideas

* virtual raster: render horizontal stripes like old IBM logo
* joy division mountains - made from sound input
* star wars text
* 3d rotations with hidden line treatment (e.g. blue making object translucent)
* soft-body physics demos, box 2d demos
* stripes with perturbations
* starfield

## Content Ideas

* image input from artist's instagrams
* welcome to country
* laser overlay on video

## modular fx

Visual effects suitable for mixing in on the modular

* warps / lens distortion (nonlinear proportional adder)
* screen shake (linear precision adder)


## Modular Synthesiser Integration

Modular synthesisers can also be used to control ILDA lasers
using such modules as the 
[LZX Industries Cyclops](https://lzxindustries.net/products/cyclops) 
(discontinued) which has an ILDA DB25 and optionally add a 
[1010 Toolbox](https://1010music.com/product/toolbox-sequencer-function-generator-eurorack-module) (discontinued)
running the alternative 1010
[LaserBox Firmware](https://1010music.com/product/laserbox-pattern-generator-for-lasers).

### Modules:

* cyclops - ILDA integration, interlock, attenuation of outbound analog laser control signals.
  * has lowpass filter on the output at various kpps values
* laserbox - pattern generation, load/store of files: wav, ilda, patterns
* es-9 - computer integration, headphone monitoring, line in, balanced line out
  * alternative: es-8 (optional es-6 expansion)
* dasiypatch - beat detection, orchestration, 
* muxlicer - arbitrary sequencing, switching
* vca matrix - mixing two geometry sources, routing to data
* rampage - slew, laser functions
* distingEX - modulation, sample playback  
* Pams + pexp2 - clocks, lfo
* data - signal analysis, waveform generator
* zoia - everything
* mult - sharing clock
* poly hector (general duties)

### Dedicated Control surfaces

* Faderbank 16n
* Beat Step Pro
* USB Gamepads



