title: week_notes_1
date: 2025-03-09
excerpt: Started learning Clojure, Gradle woes, and the latest on books.
section: week_notes
scan-time: 20s
performance:
    mental: 62
    physical: 54
    productivity: 68
---

## Work

Learnt an interesting Gradle lesson this week. While configuring an Android
project to generate a SAST integration lock-file, I got OOM errors.  
The gradle build is broken into three processes: the gradle wrapper,
the gradle daemon and the kotlin daemon. The wrapper typically has a
small amount of memory assigned, and tasks involving large logging outputs
can crash it.  
In those cases, pass the `-q` flag to disable the wrappers
logging to resolve your OOM woes.

I've continued to work on volunteer projects with [REDACTED], but have struggled a bit with the
latest environment. We moved to k8s, with the infrastructure supported predominantly by a single person. 
It's been nicely put together, but some final details, such as auth access to ArgoCD have slowed us down. 
Hoping to see that improve over the next few weeks.

## Media

#### Beyond Good and Evil by Frederick Nietzsche

>*Almost everywhere in Europe today we find a pathological sensitivity and receptivity to pain; (...)*
>*that would use religion and philosophical bric-a-brac to deck itself out as something higher -*
>*there is a veritable cult of suffering.*

>*Gods enjoy mockery: it seems they cannot suppress laughter even during holy rites.*

A much more openly daring book than *On The Genealogy of Morality* or
*Thus Spoke Zarathustra*. He is more direct in his criticism of Christianity and disdain
for the "inverted" morality that took root in Europe. His writing has aged well and
is remarkably prescient regarding Europe's trajectory.  
It's taken me a month to complete as I would frequently stop to think about various passages.
LLMs make for a good reading companion for this, like having someone on-demand to review arguments 
or interpretations.  
I read Kaufmann's translation, which is very legible, but his footnotes haven't always aged
well. At one point, he compares Nietzsche's observation on inherited behaviour to "Lamarckism",
seemingly discrediting Nietzsche's argument. However, we now know personality traits are
heritable today (just not Lamarckianly).

#### Pokemon Soul-Silver
Feeling nostalgic, I picked up an emulator and downloaded a more modern, yet still
familiar, Pokemon game. It would be unplayable if it were not for the fast-forward emulation setting. Unlikely to finish it, but it was a decent distraction for an
evening.

## Personal

I've started learning Clojure recently and used it to stitch this blog together.
It's a concise language with a build system that gets out of your way.
It reminds me of (and makes me want to revisit) Haskell, but much simpler.

In terms of languages I've been looking for something with a light build system,
good multi-threading, and practical whilst leaning functional. Kotlin would fit the bill
if it were not for Gradle. Clojure is nice, but I dislike the lack of static typing. Python
lacks the strong multi-threading. My repertoire will predominately remain Kotlin and Python, but Clojure
could be a nice addition.
