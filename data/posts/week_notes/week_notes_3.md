title: week_notes_3
date: 2025-03-23
excerpt: Russia, Bayes, and Economics. 
section: week_notes
scan-time: 20s
performance:
    mental: 65
    physical: 48
    productivity: 52
---

## Work

A former Russian colleague of mine had been living in Dubai to avoid the ongoing war. He has recently moved to London,
so we were able to grab a bite. It had been a fair few years since we last met.  
While reminiscing about old times he shared a story about his politically active days in Russia. There was a protest
regarding the Navalny arrest, and he had filmed it with his drone. The media then reported the crowd as being small
and insubstantial. So, he shared his footage with some alternative media outlets and ended up making headlines.  
I also learnt another Russian colleague of ours is a qualified Tank Officer from back in his military training days. 
It's funny, today we build large-scale systems for managing financial transactions together.

Volunteering with [REDACTED] has hit a bit of a standstill. Our key front-end developer has unexpectedly dropped off the
project without pushing his changes. We're hoping this is just a temporary issue. This is often the trouble with
online volunteer-backed projects.

## Media

#### Think Bayes by Allen B. Downey

The book focuses on Grid Approximation as a tool for teaching Bayesian statistics. It's a relatively short read, and is
a fantastic introduction to the topic.

I had previously overlooked GA (Grid Approximation) as a method, preferring MCMC methods for their flexibility. However,
as a tool for learning the topic, GA provides a much clearer understanding of the underlying mechanics (MCMC can seem
a bit magical at times). Having the student manually step through building a prior, applying an update, then analysing
the resulting posterior provides a clear step-by-step understanding of the mechanisms at play. For practical problems,
GA is still hindered by its inability to scale with model complexity. However, for simpler models, it avoids many 
of the sampling issues associated with MCMC.

The book does touch upon PyMC, which is a favourite of mine, but it does not do so in depth. Yet, this feels appropriate
given the scope of the book. For those interested, I would recommend following on with a personal favourite:
*Statistical Rethinking*. It focuses much more on applying MCMC methods in R (it has a PyMC translation).

I particularly enjoyed the practice problems that explain the theory throughout each chapter. It's a nice hands-on way
to introduce each new topic. I ended up writing my own empirical 
distribution library in Kotlin to follow along (perhaps I should publish it?). This left me wanting more, so I started
looking into what to read next. For the case-studies I found 
*Statistical Case Studies: A Collaboration Between Academe and Industry*. I would also like to do a deeper dive into
PyMC with *Bayesian Modeling and Computation in Python*, written by one of the libraries contributors.

#### The Principles of Modern Economics by Tyler Cowen, Alex Tabarrok

About 30% of the way through this book, but already have some thoughts. I picked this up as my background leans more
mathematical than economic and I wanted a more formal introduction to the topic.

The introduction to Comparative Advantage was a bit of a let-down. I found the claims made to be rather grandiose compared
to how simplistic the underlining models are. Comparative Advantage doesn't explain the trajectory China has taken 
relative to the US. 
However, digging into this led me down a deeper rabbit hole - starting with the original author, David Ricardo. 

Ricardo lived in the late 18th century to early 19th century, in a time when Britain was still transitioning from
a predominantly agricultural economy to an industrial one. One of his interesting observations was that population growth
would lead to higher costs for food, as the demand for food would lead to the cultivation of less fertile land (aligning
with Malthusianism). His involvement in the Corn Laws is also interesting - but back to Comparative Advantage.  
Regarding Comparative Advantage, Ricardo stipulates certain conditions. A major one being that capital is immobile, as
otherwise this would lead to offshoring (prescient). During the Corn Law debates he also argued that not placing
tariffs on foreign corn would lead to winners (consumers vs producers), and as such a gradual transition would be 
preferable. However, I was surprised to not find any mention of potential geopolitical risks.

One of the book's authors, Tyler Cowen, similarly finds Comparative Advantage to be somewhat lacking (see 
[link](https://marginalrevolution.com/marginalrevolution/2013/09/why-the-theory-of-comparative-advantage-is-overrated.html)).

Along a long dive into supply-demand curves, the authors touch upon the topic of monopolies. They argue that monopolies
are both a trade-off for R&D investment, but are also bad as they are economically inefficient. Inefficient in that,
although they produce greater profits for the monopolist, they reduce overall economic output by leaving consumer 
demand unmet. A similar situation occurs when the social cost of production is not reflected into the price of the 
good - overall economic output is reduced. This has left me thinking about companies like Deliveroo: does the social
cost they incur with foreign workers make them economically inefficient?  
Perhaps I will write a longer piece on this.

## Personal

My in-laws' dog passed away this week. A sad, and rather grim, event. She had been diagnosed with terminal pancreatic 
cancer. I came over to help them move the body to the garage for the night. She had a blanket over her, and her body
was already beginning to smell of decay (she used to smell faintly of sausage rolls). As we moved her, blood began 
to pour out of her mouth. She was a lovely dog, and it pained me to see her in that state.  
Handling dead bodies is never pleasant; everything tastes of death for a while after.
