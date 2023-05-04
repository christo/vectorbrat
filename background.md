# What is a Vector Display?

Vector graphics displays are much less common than pixel grids. Most TVs and
computer screens cut the display into a rectangular matrix, even the old-school
glass-tube TVs. The picture shown is constructed by refreshing the entire
screen many times each second. Vector displays trace out points, lines and
curves over and over, resulting in a very high-contrast, smooth path without
the stair-step or aliasing particularly noticeable on lower-resolution
displays. You have to see it in person.

The two most popular forms of vector display are firstly, Cathod Ray Tube (CRT)
Oscilloscope, which are usually monochromatic, showing pristine lines on the
same glass vacuum tube as pre-1990s TVs, and secondly, lasers, which project an
often multicolour beam of light that traces arbitrary shapes in space. The
oscilloscope should have an XY mode, which allows it to trace a path of any
shape and the laser ought to support the same kind of control, typically
through the ILDA standard. The tradeoffs between each display are:

| CRT Oscilloscope | RGB ILDA Laser  |
| ---------------- | --------------- |
| monochrome       | full colour     |  
| capable of high complexity geometry | capable of moderate complexity geometry |
| all lines are interconnected | line segments can be separate |
| very small screen     | vast projection surface |
| relatively safe to use  | potential serious eye damage / fire hazard |
| available as ex lab equipment | available as products for night clubs etc. |
| designed as a professional tool | designed for public display |
| crocodile clips or similar | ILDA connector | 
| simple audio signal | specialist ILDA interface signal |
| DIY integration difficulty: low | DIY integration difficulty: medium | 


The vector display signal sent to a scope or laser is changing analog voltages
over time. In order to control this with a computer, a DAC (Digital to Analog
Converter) is used. In some cases a sound card will do the job but it must be
"DC Coupled" and most are not.

Scopes are designed to take any kind of signal though the quality of the scope
and signal both affect the fidelity of the output. Left and right audio
straight out of a headphone jack will produce an image of some kind though if
it is not true stereo, that image will be collapsed onto a single diagonal
line.

The ILDA laser standard specifies a DB25 connector that looks like a boomer
printer cable. Sound cards with DC coupling (like the MOTU or Expert Sleepers
devices) can do the job, as can dedicated products like Etherdream which have
the DB25 socket on one side and an ethernet port on the other.

# Laser Safety

Don't use lasers unless you understand the dangers and have the skills to keep
yourself and others safe. You will only find sufficient information about this
elsewhere.

The significant dangers to using lasers include permanent damage to eyesight,
fire hazards, and potential damage to the laser equipment itself. Safety
measures of many kinds are typically employed to ameliorate each of these.
Laser output power is typically measured in milliwatts. A common laser pointer
may be 0.5mW and yet can still cause damage to eyesight. A club laser will
usually output more than 1000mW and sometimes more than 10,000mW (10 Watts).
