title: STL Colour Preview Tool
date: 2025-04-13
excerpt: Previewing the colours of a 3d image with custom layer colours.
section: tools
scan-time: 20s
---

Link to the tool [here](/blog/tools/stl_colour_preview_tool.html).

The tool assumes you will print with a first layer height of 0.16mm and a general layer height of 0.08mm.  
You can adjust the colours applied by layer and how many are used.  
The approach works well with an image with a good range of tones or applying a mosaic effect
to improve the blending between layers.

The script runs within your browser. If you wish to edit it, you can download the page source code
and serve it locally with `python -m http.server`.

I have attached some printed examples below.

<div class="grid-container">
<img src="/blog/image/week_notes_6/quiet.jpg" alt="Quiet"  style="width: 250px;"/>
<img src="/blog/image/week_notes_7/knight_devil_death.jpg" alt="The Knight, Death and the Devil"  style="width: 250px;"/>
<img src="/blog/image/week_notes_6/deus_ex_red.jpg" alt="Deux Ex Red"  style="height: 220px;"/>
<img src="/blog/image/week_notes_6/deus_ex_blue.jpg" alt="Deus Ex Blue"  style="height: 220px;"/>
</div>
