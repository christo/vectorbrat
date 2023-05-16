# vectorbrat

Experimental software for live interactive installations using ILDA lasers and xy-mode cathode ray tube oscilloscopes.

> "more of a brat than a punk"

## Lasers and Vectors and Brats Oh My!

If you don't know why you want to play with lasers and vector graphics and 
what's the big deal anyway, start with [background info](background.md).

## How To

* make sure default system audio device is set to ES-9

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

## Stretch goals for Asteroids

* fisheye distortion etc.
* screen shake & compression blasts
* asteroid collisions
* conservation of rotational momentum
* rotation around centre of mass
* laser weapon (goes through everything, instantaneous)
* show starfield video backdrop
* modular synth sound effects
* beat detection input to effects
* custom play controls - continuous encoder and a couple of buttons
* use daisy patch for advanced modulation (beat detect? etc.)

## modular fx

* warps / lens distortion (nonlinear proportional adder)
* screen shake (linear precision adder)

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
* laserbox - pattern generation, load/store of files: wav, ilda, patterns
* es-9 - computer integration, headphone monitoring, line in, balanced line out
* dasiypatch - beat detection, orchestration, 
* muxlicer - arbitrary sequencing, switching
* vca matrix - mixing two geometry sources
* rampage - slew, laser functions
* distingEX - modulation  
* Pams + pexp2 - clocks, lfo
* data - signal analysis, waveform generator
* zoia - everything
* mult - sharing clock
* (possibly?) poly hector (general duties)

### Dedicated Control surfaces

* Faderbank 16n
* Beat Step Pro
* USB Gamepads



