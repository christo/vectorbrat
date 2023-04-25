# vectorbrat

Experimental software for live interactive installations using ILDA lasers and xy-mode cathode ray tube oscilloscopes.

> "more of a brat than a punk"

## What is a Vector Display?

Vector graphics displays are much less common than pixel grids. Most TVs and computer screens cut the display into a rectangular matrix, even the old-school glass-tube TVs. The picture shown is 
constructed by refreshing the entire screen many times each second. Vector displays trace out points, lines and curves over and over, resulting in a   So what's the difference?

Whenever shapes with diagonal edges are displayed with pixels, there are often-visible stair-step patterns as if the whole picture was stitched on a tapestry or made of Lego bricks. Instead of this, 
vector displays have completely smooth diagonal lines and edges, which can be more effective for certain kinds of imagery. However, people can't appreciate this incredible, visceral difference by
watching a video on a phone, computer or TV because, the whole scene is being shown with a grid of pixels!

You have to see a vector display in person.

The two most popular forms of vector display are firstly, Cathod Ray Tube (CRT) Oscilloscope, which are usually monochromatic, showing pristine lines on the same glass vacuum tube as pre-1990s TVs, 
and secondly, lasers, which project an often multicolour beam of light that traces arbitrary shapes in space. In order to be most useful for displaying graphics, the oscilloscope should have an XY
mode, which allows it to trace a path of any shape and the laser ought to support the same kind of control, typically through the ILDA standard. The tradeoffs between each display are:

| CRO Oscilloscope | RGB ILDA Laser  |
| ---------------- | --------------- |
| monochrome       | full colour     |  
| high complexity geometry | moderate complexity geometry |
| interconnected lines    | separate line shapes |
| very small screen     | vast projection surface |
| relatively safe       | potential serious eye damage / fire hazard |
| available as ex lab equipment | available as products for night clubs etc. |

CRT oscilloscopes tend to only be available as second-hand lab equipment as their professional use has been replaced by modern flat panel displays. Lasers of all quality and features are available.

The display signal sent to a scope or laser is changing analog voltages over time. In order to control this with a computer, a DAC (Digital to Analog Converter) is used. Scopes are easier since they
are designed to accommodate any kind of signal. Left and right audio straight out of a headphone jack will produce an image of some kind. The ILDA laser standard specifies a DB25 connector that looks
like a boomer printer cable. Sound cards with DC coupling (like the MOTU or Expert Sleepers devices) can do the job, as can dedicated products like Etherdream which have the DB25 socket on one side 
and an ethernet port on the other. Modular synthesisers can also be used to control ILDA lasers using such modules as the 
[LZX Industries Cyclops](https://lzxindustries.net/products/cyclops) (discontinued) which has an ILDA DB25 and optionally add a 
[1010 Toolbox](https://1010music.com/product/toolbox-sequencer-function-generator-eurorack-module) running the alternative 
[LaserBox Firmware](https://1010music.com/product/laserbox-pattern-generator-for-lasers).

## Existing Software

There's a lot, and then there's also a lot of potential.

TODO


