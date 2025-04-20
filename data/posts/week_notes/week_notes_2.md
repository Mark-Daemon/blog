title: week_notes_2
date: 2025-03-16
excerpt: Clojure, irrationality and the latest on books.
section: week_notes
scan-time: 20s
performance:
    mental: 68
    physical: 52
    productivity: 77
---

## Work

Work has requested I pick up a project to improve my visibility in the company. So, as asked, I've
de-prioritised projects that are more aligned with business interests to make room for this. I've found a small project
generating a service-map navigation equivalent for our Android team using [KSP](https://kotlinlang.org/docs/ksp-overview.html).
I'll pull together a small team of people, chosen because I haven't worked with them before.  
What bemuses me is I am already well-known in the company. At least the project looks fun.

Continuing to volunteer with [REDACTED]. I've been asked to take over the k8s deployments for the latest project.
Given my limited system access, this will be interesting. At least it will be a good opportunity
to resolve those issues. As it stands, I roll out new builds by overwriting the docker image in the registry, then
kill the currently running pod. Not quite where I want it to be.

## Media

#### Notes from the Underground by Dostoevsky

> *"You see, if it were not a palace but a hen-house, I might creep into it to avoid getting wet,*
> *and yet I would not call the hen-house a palace out of gratitude to it for keeping me dry.*  
> *(...)*  
> *I will not accept as the crown of my desires a block of buildings with tenements for the poor*
> *on a lease of a thousand years, and perhaps with a sign-board of a dentist hanging out."*

> *"No one could have gone out of his way to degrade himself more shamelessly, and I fully realised it,*
> *fully, and yet I went on pacing up and down from the table to the stove.*
> *'Oh, if you only knew what thoughts and feelings I am capable of, how cultured I am!' I thought*
> *at moments, mentally addressing the sofa on which my enemies were sitting."*

> *"I never have been a coward at heart, though I have always been a coward in action."*

A very amusing book.
A case study in self-destruction and resentment, with a social critique that anticipates the
coming Soviet era.  
The narrator makes for a pitiful character, soothed and tormented by his
fantasies and self-grandeur that inevitably collide with reality. He parodies the timeless
"intellectual" and "morally righteous" archetypical personality. Makes for a good laugh.
There were moments I came to relate with his antics, a throwback to my younger years, both to my horror
and amusement.

The book starts with an argument against the idea that what is in man's best interest can be
determined rationally. The narrator appeals to man's inherent irrationality and desire for suffering in
a manner that parallels or contrasts with Mises' introduction to *Human Action*. Mises argues that
human action is always rational or to his interest, as it is in pursuit of desires of his own choosing
(whether it achieves those ends is another matter). Dostoevsky adds that these desires
may be irrational or self-destructive, but that is all too human.

#### Shape Up by Basecamp

I Picked this up as a quick read while reviewing some agile ideas I may want to apply at work.  
The general framework is "small-scale" agile, targeted to small team structures at a startup-sized company. It
does so without much of the bureaucracy typical of more structured agile methods by handing more autonomy to the
engineering team. There are clear trade-offs with this - it strikes me as more appropriate for a mature, high-initiative
team.

The key idea in the book, which seems obvious in hindsight, is in "shaping" the work appropriately. This entails
first deciding how much time and manpower a problem warrants, and from there tailoring the scope of the work to fit
within the desired constraints. This question is always worth asking: how much time and money are we willing to dedicate to
solving this?  
Basecamp takes this to a certain extreme; if a project is not delivered within the time frame, it is not automatically
extended. First, whether it is still worthwhile or needs further scoping must be re-evaluated. This may
result in abandoned projects, but it is one hell of an incentive for your team to deliver on time.

#### Caribbean Rhythms Ep. 184 Variety Actor

BAP comments on the ongoing Ukraine talks regarding NATO and the potential peace talks. He regards the US
potentially pulling out of NATO as a major opportunity for the European Right. However, I find this unlikely,
I don't see Trump reducing US power projection. He ends on a final note: Russia may not be an ally,
but the EU is a far greater threat to the European Right.

Musk was also involved recently in promoting the German AFD party, which BAP regards as a poor manoeuvre. He
argues that such a provincial party will not be able to win a national-level election, nor should the
European Right is angling for such a goal.  
Instead, they should focus on influencing the mainstream parties and pressuring them to
adopt their policies. BAP cites the Danish left wing adopting anti-immigration policies as a successful
example. Musk drawing unnecessary international attention to the AFD in turn applies pressure on the CDU to distance
themselves from their policies.

Again, we hear a perspective from Hilaire du Berrier concerning the activities of the CIA and their backing
of left-wing regimes across the world. This time, BAP draws attention to [Tom Braden](https://en.wikipedia.org/wiki/Tom_Braden),
who championed promoting "anti-soviet" left-wing movements as a counter to the USSR. He further highlights that these
initiatives "backfired", such as in Morocco, and instead introduced a left-wing communist presence where previously there
was none.  
This brings into question whether the CIA genuinely intended these left-wing movements to combat the USSR and
whether they live up to their reputation for promoting right-wing movements.

The show had a nice choice in music, as always. I find I perhaps go too far in resting my eyes and listening.

## Personal

I have recently learnt that Amazon limits the number of book highlights stored on a Kindle.
You can still access all the highlights [here](https://read.amazon.com/notebook). However, this is
much less convenient than simply extracting the text file from the device to convert it into notes.  
I don't usually buy books from Amazon except for the rare occasion I cannot pirate them, but
now I will be less inclined to do so.

I have made a few more adjustments to the site generation. I now use Clojure's eval mechanisms to
move UI logic into the template files. Very nice that Clojure can do this so easily.  
My remaining issue with the language is the type support. I've made heavy use of pre-conditions with
spec to enforce type, yet it still feels clunky to refactor. I'll need to dive deeper into how to best
use spec.  
I have also yet to dive into testing.
