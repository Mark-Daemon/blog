title: week_notes_6
date: 2025-04-13
excerpt: Algorithmic Learning in a Random World, Nick Land's Exit v Voice, 3D printed art.
section: week_notes
scan-time: 20s
performance:
    mental: 72
    physical: 70
    productivity: 62
---

## Work
Gears have shifted a bit with my volunteer work at [REDACTED]. With the K8S work done, I am now 
looking into how to integrate the Medusa store-front with Ory for authentication and user management.
Both are headless solutions, and Medusa looks comfortably flexible with plugin support (as opposed 
to Ghost CMS, which is a pain on that front). I did an initial bit of digging to get a feel for where 
to start, but now I have a plan sorted (registration plugin to integrate with Ory).

## Media

#### Algorithmic Learning in a Random World
The general gist is to provide a confidence interval for machine learning algorithms dubbed conformal 
prediction. This is similar to what is used with Bayesian modelling, but the interval is determined
by the similarity to already seen data points rather than the model set distributions. 
This comes with some nice properties - namely, the data determines the confidence interval whilst 
considering how "familiar" a particular data point is.

The first several chapters introduce the concept of conformity - how similar data points are to one another. 
It then builds towards how we can apply this to determine confidence intervals for an underlining predictor - 
in short, how well did we predict the similar data points in our training set? The authors dive deep into the 
topic, exploring the presented formulas in detail, looking into asymptotic performance and backing their claims 
with mathematical proofs. The downside is that this drags on a bit, and you may start to miss the forest from 
the trees. However, from chapter 8 onwards, the book shifts into high gear again.

From Chapter 8, tests for exchangeability using martingale tests are introduced - in short, detecting when 
your algorithm needs to be retrained for whatever reason (concept shift, label shift, etc.). Note: martingale 
here as in the stochastic process, not the better-known betting strategy. The idea is to "place bets" against 
the underlining model predictions - should the martingale property be violated (E[M[n + 1]] = M[n]), then 
exchangeability has been violated. Once again, they go into great depth exploring this (I particularly liked 
the properties super-martingales provide regarding bounding - i.e. Ville's inequality). However, it doesn't 
end there.

Given we can detect when the algorithm is suffering from some imbalance, we can use this mechanism to 
course-correct with "protected prediction algorithms". Leveraging the martingale exchangeability tests, 
we can also derive the direction of the inaccuracy, allowing us to nudge the prediction in the right direction. 
This, of course, comes at a price. However, it is presented as a technique to be relied upon while the 
underlying predictor is undergoing retraining.

The authors provide an ending theory chapter covering the base probability-related topics they build 
upon to develop their conformal predictors. In hindsight, I should have given this a flick earlier 
to set the context for the book. I recommend starting here and reviewing any concepts you are unfamiliar with.

Overall, I enjoyed this book, particularly from chapter 8 onwards. The ideas are well-founded and have 
shaped how I would approach building prediction systems. There is enough value here that I am surprised 
my more data-science / data-engineering friends didn't already have this on their radar. If you are someone 
who builds prediction systems, then I highly recommend this book.

## Personal
I have been pondering Nick Land's thoughts on Exit being a more influential move than Voice (voting with 
your feet carrying more weight than reforming a system from within / democratically).

Looking at the situation in the UK, it becomes apparent that there are filters in place to avoid reform. 
Anyone who steps up will be vilified by the government, smeared by the media, and will need to walk on 
eggshells to avoid a knock on the door from the police. This will happen before there is even a chance to 
enact reform. To be rather Hitchens about it, the effort is not worth it.

Even if the self-destructive elements are removed from power, they will continue to agitate for what they
genuinely think is for the best. I am not keen to tie my personal fate to such people. Perhaps it is 
time to look for the door.

#### 3D Printing
After seeing a 
[picture](https://makerworld.com/en/models/565019-quiet-from-metal-gear-solid?from=search#profileId-484615) 
of Quiet from MGSV, I took an interest in printing similar 3d art pieces. I wrote a small script for rendering
an estimation of what a height map image will look like with layers printed in different colours (Bambu 
Studio displays the colours but doesn't display the blending between layers satisfactorily). You can find 
my script and a description of my approach [here](/blog/tools/stl_colour_preview.html).

I took a well-known image from the latest Deus Ex game and applied a mosaic effect to improve blending. 
The results are below (alongside the initial Quiet print):

<img src="/blog/image/week_notes_6/quiet.jpg" alt="Quiet"  style="width: 250px;"/>
<div class="grid-container">
<img src="/blog/image/week_notes_6/deus_ex_red.jpg" alt="Deux Ex Red"  style="height: 220px;"/>
<img src="/blog/image/week_notes_6/deus_ex_blue.jpg" alt="Deus Ex Blue"  style="height: 220px;"/>
</div>

#### Kotlin Bridge for Stan
BridgeStan provides a good foundation that is already used by RStan and CmdStan - getting started was very easy. 
The Kotlin support for C is surprisingly good - almost works out of the box. You point the build system at 
the headers, and it will generate the C calls for you. I'm not far enough to have encountered 
memory-management-related issues, but I expect them. Too early to share, but progress will continue.