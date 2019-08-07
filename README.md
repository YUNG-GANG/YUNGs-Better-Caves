# Better Caves
Minecraft mod to make caves more interesting. Uses [Cubic Chunks](https://github.com/OpenCubicChunks/CubicChunks) and [Cubic World Gen](https://github.com/OpenCubicChunks/CubicWorldGen) (TODO) to generate cave systems in worlds of infinite vertical size.


### Notable settings for future implementation
- Large caverns. Expose a lot of lava down low. Definitely good for its own biome. Something like this (but more extreme) could be good for giant lava sea/river biomes.
  - 2 Ridged Multi-fractal Perlin noise w/ threshold 0.7.
    - 1 octave, .035 frequency, .3 gain
  - fBM (PerlinFractal) turbulence
    - 3 octaves, .03 frequency, .9 gain. Also tried 30 gain and it seemed like it MIGHT yield better caves but less space in big rooms? Not sure, didn't seem to matter too much.
