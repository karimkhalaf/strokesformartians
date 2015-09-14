# Bone structure information #

  * Bone transformations are defined in global space.
  * Bone animations are defined as the transformation matrix that transforms the rest pose coordinate system into the animation pose coordinate system.
  * The animation is defined as one transformation matrix for each bone for each frame.
  * Each vertex has one or more nonzero weights corresponding to nearby bones.
  * The weights must sum to one.
  * The bone structure has a rest pose.

## Equations ##
### Bone animation transformation ###
```
r_t: rest transformation
a_t: animation transformation (for a frame)
t: transformation from r_t to a_t
r_t * t = a_t
t = r_t^-1 * a_t
```

### Skinning transformation ###
Example transformation for skinning a vertex with two bones affecting it.
```
p_r: the resulting position of the vertex
t_x: transformation from restpose to animation pose for bone x
b_x: root position for bone x
p_x: position of vertex x
w_x: weight of bone x for the vertex

p_r = t_0 * (p_1 - b_0) * w_0 + t_1 * (p_1 - b_1) * w_1
```