title: week_notes_4
date: 2025-03-31
excerpt: Site Reliability, Impro, and future projects.
section: week_notes
scan-time: 20s
performance:
    mental: 41
    physical: 32
    productivity: 48
---

## Work

I read an interesting case in the book Google's Site Reliability Engineering some years ago. 
They had reduced the availability of a storage/caching mechanism with planned outages so developers would not 
over-rely on it. The logic was that if they were to provide a too-high availability, developers would treat 
it as if it were always available. At the time, I didn't fully appreciate why you would purposefully 
degrade your system performance. I have now learnt this lesson.  
We have been addressing a caching system that has become too expensive. We had the foresight to 
design the library that accesses the cache to require a network fallback, so we were confident 
we could drop the cache for some users to better maximise the benefit/cost ratio. However, 
in practice, a client team extracted the mechanism to only read from the cache. They have 
been building features without a network fallback for several years now. To make matters worse, 
they modified the cache response to contain fields not present in their matching network request. 
Given this high coupling with the cache, the project to reduce cache reliance is now too expensive 
for us to pick up (due to opportunity costs).  
While explaining this problem to the responsible team, they kept telling me: 
"You don't need to do this; the cache is always available."

## Media

#### Impro by Keith Johnstone

I picked this up for a change of pace. It had been described to me as explaining social 
exchanges to the autistic (no comment). The most interesting part of the book was 
exploring how to present oneself as low or high status, from body posture to speaking 
mannerisms to "taking up space." One interesting observation was keeping one's head 
more still relative to exaggerated head movements when speaking.  
I was not the target audience for the rest of the book. However, I still found the 
observations useful, from how actors can build a scene without a fixed script to how
masks influence the actors' performance. Should I one day explore writing fiction 
more seriously, I can see the notes I took being useful.

#### Atomfall

The game was pitched to me as being more Metro than Fallout, which caught my attention. 
The game has lived up to the pitch. It's not the most challenging game, but the environment 
and plot are worth exploring. It seems relatively short. I'm about 6 hours in and have almost 
finished it. I recommend the game, but you may want to wait for a sale.

## Personal

I have been struggling with tired eyes lately. I attribute this to spending too much time staring at a screen. 
The books I've been reading lately are textbooks, which are more easily read on a tablet than an e-reader, 
which hasn't helped. I've started looking into some podcasts that I can listen to instead.

I've been looking into the Stan data analysis library. It has multiple bridges into other languages, 
mainly Python and R. Given its implementation in C++, I've been wondering if Kotlin would be a better 
language to use for a bridge. Kotlin's native compilation target should allow better interop. 
For example, it should be possible to implement custom samplers for Stan in Kotlin. Perhaps a project to pick up?

I enjoy the music selection in Caribbean Rhythms. However, it would be tedious to find every single 
track used, so I wrote a quick script to identify them for me. This was done in two steps: use the 
Essentia audio models to find and extract music segments from each episode, then use ShazamIO to find 
the artist and album for each segment. The results are not perfect, but I am not too fussed.  
I'll publish the resulting list of music next week.
