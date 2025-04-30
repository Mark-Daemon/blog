title: week_notes_8
date: 2025-04-29
excerpt: Football Hackers, Search Integration, and rethinking personal metrics.
section: week_notes
scan-time: 20s
performance:
    mental: 65
    physical: 30
    productivity: 50
---
{{ (enable-latex) }}


## Work
Nothing interesting to comment on.
## Media
#### Football Hackers: The Science and Art of a Data Revolution by Christoph Biermann
Football Hackers tells the history of using data science on the football field. Starting from the simpler metrics of possession and shots on goal to the more advanced concepts of "packing" and Expected Goals. The book focuses less on the details than on telling the story of their evolution and impact on the game.

A key theme throughout the book is the difficulty of finding useful metrics. Simple metrics such as possession can be interpreted in many ways—the team could have been quick on the attack or had instead struggled to maintain possession of the ball. Similarly, early video analysis, which tracked shots on goal and player positioning, failed to find metrics that correlated with winning games. Things start to get interesting when the Expected Goals arrive.

Expected Goals look at the likelihood of scoring a goal from the position of the shot on goal (dividing up the pitch to do so). This informs the team of where best to shoot from, but also allows us to evaluate the team - did they under or over-perform their Expected Goals. Yet this metric is incomplete for informing the game at large. It fails to model the importance of different roles in the game (such as defence) or the strategies involved.

Enter measures such as "danger" or "control of field". Voronoi diagrams built upon the distance between players (reflecting the time to get to the ball) estimate how much each team "controls the field". "Danger" follows a similar idea of measuring how each player's actions escalate the chances of scoring. An interesting metric is extending Expected Goals into the chain of actions that lead to the shot, giving us Expected Goal Chains. Another is "packing"; in short, the number of opponents a player has bypassed.

One style of modelling particularly appealed to me: measuring the rate of scoring goals when a particular player was 
on the pitch relative to when they were not. Across a large number of games, this provides an indication of their
contribution to scoring. I imagine the commercial model also incorporates their contribution to defence with a similar
metric. What likely appeals to me the most is that you can easily measure this without having to actually watch the game.

Throughout the book, the idea of finding the "ideal" model or metric for defining success in the game is repeated. It strikes me that there will not be one — the combination of variance in tactics and player roles is too large. The models presenting the most success appear to focus on the particulars of a team's playing style or strategy (and how the players lend themselves to this strategy).

Overall, this was not a highlight book for me. There was too much emphasis on telling the story of football, with plenty of namedropping, and too little focus on the analytical details. On the positive side, the book convinced me that football analysis was not for me. Besides the World Cup, I simply am not invested in the game, even when taking a data-driven perspective.
## Personal
#### Search Integration
Integrated a search feature into the site. When building the site, I pre-compute a text-index of search terms and auto-suggestions, then have a small amount of JS pull the records and sort them by the number of matching terms in the query. This project was largely an excuse to write more Clojure.
#### Personal Metrics
I have been re-thinking my personally tracked metrics. The current Physical/Mental/Productivity is a subjective estimate for the week - I wanted to make these less subjective and more useful. I've chosen to focus on Sleep, Physical Activity, and Productivity.

For sleep I will track only what time I go to bed at and what time I wake up at. I will use these to score my sleep habits. For going to bed I will score hitting the target of 11pm, with a score of 0 for 3am or beyond. Similarly for waking up, but with hitting the target of 8am, with a score of 0 for 12pm.

With {{ (latex "`T_b`") }} as time-to-bed and {{ (latex "`T_w`") }} as time-to-wake, the {{ (latex "`S_d`") }} (sleep score) will be calculated as follows: 

{{ (latex "S_b = \\max\\left(0, \\min\\left(100, 100 - 25 \\cdot (T_b - 23)\\right)\\right)") }}

{{ (latex "S_w = \\max\\left(0, \\min\\left(100, 100 - 25 \\cdot |T_w - 8|\\right)\\right)") }}

{{ (latex "S_d = \\frac\{S_b + S_w\}\{2\}") }}

Physical activity will follow a similar formula, however it will need to mix 3 workouts with the target of a daily physical activity.

{{ (latex "N_a") }}: Number of days with physical activity (max 7, including workout days).

{{ (latex "N_w") }}: Number of workouts completed (max 7, but typically <= 3 since that's the goal).

{{ (latex "S_\{\\text\{phys\}\}") }}: Weekly physical activity score.

{%
    (latex 
        "S_{\\text{phys}} = 0.4 \\cdot \\left( \\frac{N_a}{D_a} \\right) + 0.6 \\cdot \\left( \\frac{N_w}{W} \\right)"
    )
%}

For Productivity I will set out a block of time for working on a particular task at the beginning of the week. I will then score this on how many of the planned time blocks were completed, relative to the number planned. This will help me measure my own productivity in terms of time spent working, but also my discipline (following through with what was planned). 

Relatively simple measure compared to the other - divide the number of tasks completed by the number of tasks planned.

#### Latex Support
Added Latex support to the blog with Katex.