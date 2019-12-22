# Idle Planet Miner helper

*This project is a WIP, please don't try to use it yet.*

The goal of this project is to help choosing the most optimal steps in the Idle Planet Miner game.

The algorithm will choose the "best" next step depending on its return on investment (ROI).
The ROI depends on the state of the galaxy, which includes all aspects of the game, from long-term bonuses,
to Galaxy-specific bonuses.

### Input

In order for the algorithm to give relevant results, the whole current state of the galaxy needs to be given as input:

- long-term bonuses
    - bought ships/upgrades
    - mothership room levels
    - beacon levels
    - stars earned during challenges
- galaxy status
    - bought planets
    - planet mine/ship/cargo levels
    - planet colony levels/bonuses
    - researched projects
    - assigned managers (and the planet they're assigned to)
    - current market

Also some "configuration" inputs are necessary:

- N_STEPS: the desired number of steps to provide as output (otherwise we can continue playing forever) 
- DEPTH: the algorithm will explore what happens when performing different sequences of actions. The DEPTH is the
 length of each sequence of actions the algorithm will consider. Increasing DEPTH gives more accurate results, but
  slows the algorithm down exponentially. The DEPTH should not be lower than 2, otherwise the algorithm will never
   consider actions that bring no value by themselves (such as unlocking stuff).  

### Algorithm

1. define a set of possible actions from the current state:
    - buy an unlocked planet
    - upgrade a planet's mine (only for bought planets)
    - upgrade a planet's ship (only for bought planets)
    - upgrade a planet's cargo (only for bought planets)
    - research an unlocked project (only if resource constraints are met - see dedicated section below)
    - unlock the next smelting recipe (only if the SMELTER project is unlocked)
    - unlock the next crafting recipe (only if the CRAFTER project is unlocked)
    - build a new smelter (only if the SMELTER project is unlocked)
    - build a new crafter (only if the CRAFTER project is unlocked)
2. for each available action:
    - calculate its cost in money/resources and the equivalent time it takes
        - if it requires cash
            - compute the time to get that money based on the current income rate
        - if it requires resources
            - compute the time it takes to smelt/craft all the resources (recursively)
                - it is roughly `ore gathering time + max(craft time, smelt time)` (because they run in parallel)
                - each of craft time and smelt time can be roughly divided by the number of smelters/crafters available
                - accurate calculation of this may not be necessary, estimates will be easier to implement
    - compute the galaxy's state if the action is taken (if this step turns out to be slow -which is yet to be
     shown-, the states can be cached because the action order doesn't affect the resulting galaxy)
        - cash income is calculated by converting the total ore production into money using the current market 
    - compute the difference between the current galaxy's income and the target galaxy's income
        - find the time at which we get back the investment made (ROI=1 time) from the difference alone (remember to add
         the time taken to perform the actions)
        - note that the diff may be 0 (in case the action just unlocks something without improving revenue directly), 
        hence the need for considering more actions from here (even for non-0 diffs, because combined actions can
         yield more)
        - recursively search further states by exploring more actions (up to the given max DEPTH)
        - sum the cost & time of all of the steps along the way (this *does* depend on the actions' order)
3. for each of the explored states (even intermediate ones), select the one with earliest "ROI=1" time (as "next" state)
4. compact the action sequence to reach that state (upgrade a level multiple times can be done as a single action)
5. output the compacted actions to take
6. set the current state to that next state
7. repeat N_STEPS times (given as parameter)

Things to consider:

- We can later add colonization actions to the algorithm. These actions will require the planet to be bought, the
 COLONIZATION project to be researched, and all the usual constraints on resources mentioned in the section below. The 
 main pain point will be to account for the fact that not all planets can be colonized at a time. Also, we need to
  re 
    
### About resource constraints

Some actions require resources, like researching projects or colonizing a planet.
The algorithm does not track stocks for these products, but there are still a bunch of constraints that can rule out
 these actions for the search:
- all the recipes for all alloys/items (recursively required) need to be unlocked
- for all ores appearing in all these recipes, at least one of the bought planets needs to produce it
- if there are alloys in the recipes, then the SMELTER project needs to be researched already
- if there are items in the recipes, then the CRAFTER project needs to be researched already
