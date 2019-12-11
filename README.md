# Idle Planet Miner helper

*This project is a WIP, please don't try to use it yet.*

The goal of this project is to help choosing the most optimal steps in the Idle Planet Miner game.

The algorithm will choose the "best" next step depending on its return on investment (ROI).
The ROI depends on the state of the galaxy, which includes all aspects of the game, from long-term bonuses,
to Galaxy-specific bonuses.

### Input

A good deal of elements are required as inputs, for the algorithm to be accurate:

- long-term bonuses
    - bought ships/upgrades
    - mothership room levels
    - beacon levels
- galaxy status
    - planet levels
    - planet colony levels/bonuses
    - researched projects
    - assigned managers (and the planet they're assigned to)
    - current market

### Algorithm

1. define a set of possible actions from the current state
    - if all planets have a mine rate lower than the ship/cargo combo
        - upgrade a planet's mine
        - research an unlocked project
        - buy an unlocked planet
    - if a planet's mine rate is too high for the ship/cargo, limit the possible actions to just:
        - upgrade that planet's ship
        - upgrade that planet's cargo 
2. for each available action:
    - calculate its cost in money and the time it takes
        - if it requires an instant payment, then take the price as cost, and the time is 0 
        - if it requires resources
            - find the equivalent cost given the current market
            - compute the time it takes to smelt/craft the resource (recursively)
              (NOTE: this can be done up front for all recipes)
    - compute the galaxy's state if the action is taken
    - compute the difference between the current galaxy's income and the target galaxy's income
        - if the diff is 0, it means the action is likely to be unlocking something for later
            - recursively search further states by exploring more actions (up to a max DEPTH, given as param)
            - sum the cost/time of all of the steps
        - if the diff is > 0
            - find the time at which we get back the investment made (ROI=1 time)
              (remember to add the time taken to smelt/craft stuff for all actions)
3. pick the action/sequence of actions with the earliest "ROI=1 time", and set the current state appropriately
4. repeat N times (given as parameter)

Things to consider:

- maybe we should not divide diff>0 VS diff=0, but go up to DEPTH in any case
    - takes into account actions with little immediate gain, but best combined gain with other actions
    - avoids the need to deal with mine upgrades VS other planet upgrades
    - will be slower
- DEPTH=1 prevents researching projects that unlocks stuff but bring no gain
