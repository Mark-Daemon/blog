title: week_notes_9
date: 2025-05-04
excerpt: Oblivion and Personal Metrics in Clojure.
section: week_notes
scan-time: 20s
performance:
    version: 1
    sleep: 52
    physical: 20
    productivity: 60
---
## Work
Work is splashing out on Google Cloud qualifications. I signed up for the architect and machine-learning courses.
I've seen these courses mentioned on other people's CVs, but I've never done one myself. With a bit of luck, they'll be
decent.

## Media

#### Oblivion Remaster
Too much of my childhood was spent playing Oblivion, and now too much of my adulthood has also been spent playing it.
Likely the nostalgia speaking, but I thought it was one of the best games released in recent times.
I don't generally play many games or watch many movies anymore; the industries have moved in a direction that doesn't appeal to me.

## Personal

#### Personal Metrics
I wrote a page for tracking and "scoring" my week [here](/blog/tools/week_scoring.html). It implements the scoring I
described [last week](/blog/week_notes/week_notes_8.html).

This was implemented in Clojure-Script, which I integrated into the site.

#### Clojure-Script Integration
As I have continued looking for excuses to write Clojure, I integrated Clojure-Script into the site. The process
was simple - I can now write Clojure-Script code in dedicated files and have them compiled alongside the
overall static-site generation into Javascript. However, it is slower than I had hoped.

I have some thoughts on implementing a caching solution to speed it up. Along the lines of storing the compiled JS
alongside the last-modified date of the .cljs file and only re-compiling as needed. Perhaps next week.