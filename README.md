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

See [GitHub issues](https://github.com/christo/vectorbrat/issues) for outstanding items.

### DONE:

* [ ] laser driver (WIP, see
[laser issues](https://github.com/christo/vectorbrat/issues?q=is%3Aissue+is%3Aopen+label%3Alaser))
  * [x] text rendering
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

## Performance Measurement and Calibration

The Interpolator needs to be configurable with time delta functions. The coefficients of these functions must be 
experimentally approximated. This creates an opportunity for a much-needed test method for vector display 
performance characteristics.

We need a simulator and integrated a test harness for calibration of the physical hardware limitations as a search for
coefficients by empirical approximation. A simulator view should attempt to render the expected visual appearance
and tweaking the configuration parameters for the hardware driver should enable the user to make the simulator match the
real visual. Once the simulator shows the same distortions as the real device, at least for that point in the signal 
space, the hardware limitations can be known. By taking successive measurements of the distortions, even nonlinear 
performance characteristics can be compensated for.  

Scanning lasers deflect the beam with galvanometers, cathode ray oscilloscopes use electrostatic deflection and Vecrex,
like cathode ray TVs, use electromagnetic deflection. Each of these methods constrains the acceleration of these
deflection angles differently.

Additionally, DPSS lasers have a beam intensity change profile which is slower than "pure diode" lasers and the rate is
possibly different for increase and decrease.

In order to create signals that optimally produce a desired geometry, each of these (probably polynomial) time functions
must be approximated. Lasers are the slowest and have rotational momentum in the optics.

Each hardware model may need its own profile. 

The process can be semi-automated with a camera trained on the output, subject to camera performance and so long as the 
characteristics of the camera are known or can be reasonably approximated. By doing camera calibration at very low 
speeds, the chicken-and-egg problem may be avoided. The potential for on-board optimisers and intervening analog 
circuits for scaling and offsetting voltages must be accommodated, perhaps by adding notes to an experimental run 
and allowing for additional parameters to the measurement space.

The results of such tests could be saved to a known format and shared in an online database between users and 
manufacturers.

Finally, while the quality of the output signal is of primary interest, if the hardware is pushed too hard, it can 
be damaged, regardless of the output quality. In the case of lasers, the advised method to avoid this kind of 
trouble is to listen for excess audible strain in the scanners. Attempting to autocalibrate even this by adding a 
contact microphone is alluring but this whole thing feels like a bridge too far and such monumenal automation 
efforts must stop somewhere. 

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



